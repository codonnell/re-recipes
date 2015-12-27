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

(defn namespace-recipe-map [recipe]
  "Takes a map like {:name \"Chris\" :ingredients [\"coriander\"]} and adds the proper namespaces. In this example, the map {:recipe/name \"Chris\" :recipe/ingredients [{:recipe/name \"coriander\"}]} would be returned."
  (into {}
    (for [[k v] recipe]
      [(->> (str k)
         (str ":recipe/")
         (keyword))
       (condp = k
         :ingredients (for [i v] {:ingredient/name i})
         :his-rating {:rating/stars (:stars v) :rating/review (:review v)}
         :her-rating {:rating/stars (:stars v) :rating/review (:review v)}
         :default v
         )])))

(defn get-recipe [attrs]
  nil)

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
