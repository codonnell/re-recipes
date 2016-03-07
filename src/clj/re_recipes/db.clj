(ns re-recipes.db
  (:require [com.stuartsierra.component :as component]
            [datomic.api :refer [q db] :as d]))

(defrecord Database [uri]
  component/Lifecycle

  (start [component]
    (d/create-database uri)
    (let [conn (d/connect uri)]
      @(d/transact conn (read-string (slurp "schema.edn")))
      (assoc component :conn conn)))

  (stop [component]
    (d/release (:conn component))
    (assoc component :conn nil)))

(defn new-db [uri]
  (map->Database {:uri uri}))

(defn denamespace-recipe
  "Takes a map like {:recipe/name \"Chris\" :recipe/ingredients [{:ingredient/name \"coriander\"}]} and removes namespaces. In this example, the map {:name \"Chris\" :ingredients [\"coriander\"]} would be returned."
  [recipe]
  (into {}
    (let [de-ns (comp keyword name)
          de-ns-map (fn [m] (into {} (mapv (fn [[k v]] [(de-ns k) v]) m)))]
      (for [[k v] recipe]
        [(de-ns k) ; Removes namspace from key
         (condp = k
           :recipe/ingredients (mapv :ingredient/name v)
           :recipe/his-rating (de-ns-map v)
           :recipe/her-rating (de-ns-map v)
           v
           )]))))

(defn namespace-recipe
  "Takes a map like {:name \"Chris\" :ingredients [\"coriander\"]} and adds the proper namespaces. In this example, the map {:recipe/name \"Chris\" :recipe/ingredients [{:recipe/name \"coriander\"}]} would be returned. Note that :d is turned into :db/id."
  [recipe]
  (into {}
    (for [[k v] recipe]
      [(if (= k :id) :db/id
           (->> (str k) (rest) (apply str "recipe/") (keyword)))
       (condp = k
         :ingredients (mapv (fn [i] {:ingredient/name i}) v)
         :his-rating {:rating/stars (:stars v) :rating/review (:review v)}
         :her-rating {:rating/stars (:stars v) :rating/review (:review v)}
         v
         )])))

(defmacro database-fn
  "Defines two functions fname and fname*. The fname function takes as its first argument a database component of stuartsierra's system. The function fname* takes a datomic db value. They are otherwise identical."
  [fname [db & args] & fbody]
  `(do
     (defn ~(symbol (str fname "*")) [~db ~@args]
       ~@fbody)
     (defn ~fname [~db ~@args]
       (~(symbol (str fname "*")) (d/db (get ~db :conn)) ~@args))))

(def full-recipe-pull
  [:db/id :recipe/name :recipe/url {:recipe/ingredients [:ingredient/name]}
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
