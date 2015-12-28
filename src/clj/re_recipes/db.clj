(ns re-recipes.db
  (:require [com.stuartsierra.component :as component]
            [datomic.api :refer [q db] :as d]))

(defrecord Database [uri]
  component/Lifecycle

  (start [component]
    (d/create-database uri)
    (let [conn (d/connect uri)]
      (assoc component :db conn)))

  (stop [component]
    (d/release (:db component))
    (assoc component :db nil)))

(defn new-db [uri]
  (map->Database {:uri uri}))

(defmacro database-fn
  "Defines two functions fname and fname*. The fname function takes as its first argument a database component of stuartsierra's system. The function fname* takes a datomic db value. They are otherwise identical."
  [fname [db & args] & fbody]
  `(do
     (defn ~(-> fname (str "*") (symbol)) [~db ~@args]
       ~@fbody)
     (defn fname [~db ~@args]
       (~(-> fname (str "*") (symbol)) (d/db (get :db ~db)) ~@args))))

(def full-recipe-pull
  [:recipe/name :recipe/url {:recipe/ingredients [:ingredient/name]}
   {:recipe/his-rating [:rating/review :rating/stars]}
   {:recipe/her-rating [:rating/review :rating/stars]}])

(database-fn recipe-by-name [db name]
  (ffirst (d/q '[:find (pull ?r pattern)
                 :in $ ?name pattern
                 :where [?r :recipe/name ?name]]
            db name full-recipe-pull)))

(database-fn recipes-by-ingredient [db ingredient-name]
  (q '[:find [(pull ?r pattern) ...]
       :in $ ?i-name pattern
       :where
       [?r :recipe/ingredients ?i]
       [?i :ingredient/name ?i-name]]
    db ingredient-name full-recipe-pull))

(defn add-entity-tx [m]
  [(merge m {:db/id (d/tempid :db.part/user)})])

(defn add-recipe-tx [m]
  (let [ingredient-txs (apply concat (map add-entity-tx (:recipe/ingredients m)))
        ingredient-tempids (mapv :db/id ingredient-txs)
        his-rating (add-entity-tx (:recipe/his-rating m))
        her-rating (add-entity-tx (:recipe/her-rating m))]
    (concat ingredient-txs his-rating her-rating
      (add-entity-tx
        (assoc m
          :recipe/ingredients ingredient-tempids
          :recipe/his-rating (:db/id (first his-rating))
          :recipe/her-rating (:db/id (first her-rating)))))))

(database-fn ingredient-by-name [db name]
  (let [results (q '[:find (pull ?i [:ingredient/name])
                     :in $ ?name
                     :where [?i :ingredient/name ?name]]
                  db name)]
    (ffirst results)))

(def search-rules
  '[[(search-matches? ?s ?r) [(fulltext $ :recipe/name ?s) [[?r _ _ _]]]]
    [(search-matches? ?s ?r)
     [(fulltext $ :ingredient/name ?s) [[?ingredient _ _ _]]]
     [?r :recipe/ingredients ?ingredient]]])

(database-fn recipe-search [db search-term]
  (q '[:find [(pull ?r pattern) ...]
       :in $ % ?search-term pattern
       :where (search-matches? ?search-term ?r)]
    db search-rules search-term full-recipe-pull))

(database-fn all-recipes [db]
  (q '[:find [(pull ?r pattern) ...]
       :in $ pattern
       :where [?r :recipe/name _]]
    db full-recipe-pull))

(def test-data
  [{:db/id #db/id[:db.part/user -100001]
    :rating/stars 5
    :rating/review "Pretty good!"}
   {:db/id #db/id[:db.part/user -100002]
    :rating/stars 4
    :rating/review "Too much ginger"}
   {:db/id #db/id[:db.part/user -100003]
    :ingredient/name "ginger"}
   {:db/id #db/id[:db.part/user -100004]
    :ingredient/name "bread"}
   {:db/id #db/id[:db.part/user -100005]
    :recipe/name "Ginger bread"
    :recipe/url "https://www.blueapron.com/recipes/ginger-bread"
    :recipe/ingredients [#db/id[:db.part/user -100003] #db/id[:db.part/user -100004]]
    :recipe/his-rating #db/id[:db.part/user -100001]
    :recipe/her-rating #db/id[:db.part/user -100002]}])

(def schema
  [{:db/id #db/id[:db.part/db]
    :db/ident :recipe/name
    :db/valueType :db.type/string
    :db/cardinality :db.cardinality/one
    :db/fulltext true
    :db/doc "A recipe's name"
    :db.install/_attribute :db.part/db}
   {:db/id #db/id[:db.part/db]
    :db/ident :recipe/url
    :db/valueType :db.type/string
    :db/cardinality :db.cardinality/one
    :db/doc "A recipe's url"
    :db.install/_attribute :db.part/db}
   {:db/id #db/id[:db.part/db]
    :db/ident :recipe/his-rating
    :db/valueType :db.type/ref
    :db/cardinality :db.cardinality/one
    :db/doc "His rating for a recipe"
    :db.install/_attribute :db.part/db}
   {:db/id #db/id[:db.part/db]
    :db/ident :recipe/her-rating
    :db/valueType :db.type/ref
    :db/cardinality :db.cardinality/one
    :db/doc "Her rating for a recipe"
    :db.install/_attribute :db.part/db}
   {:db/id #db/id[:db.part/db]
    :db/ident :recipe/ingredients
    :db/valueType :db.type/ref
    :db/cardinality :db.cardinality/many
    :db/doc "Ingredients in a recipe"
    :db.install/_attribute :db.part/db}
   {:db/id #db/id[:db.part/db]
    :db/ident :ingredient/name
    :db/valueType :db.type/string
    :db/cardinality :db.cardinality/one
    :db/unique :db.unique/identity
    :db/fulltext true
    :db/doc "Ingredient's name"
    :db.install/_attribute :db.part/db}
   {:db/id #db/id[:db.part/db]
    :db/ident :rating/stars
    :db/valueType :db.type/long
    :db/cardinality :db.cardinality/one
    :db/doc "Number of stars in a rating"
    :db.install/_attribute :db.part/db}
   {:db/id #db/id[:db.part/db]
    :db/ident :rating/review
    :db/valueType :db.type/string
    :db/cardinality :db.cardinality/one
    :db/doc "Review in a rating"
    :db.install/_attribute :db.part/db}
   ])
