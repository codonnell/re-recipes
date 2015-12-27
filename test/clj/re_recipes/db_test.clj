(ns re-recipes.db-test
  (:require [re-recipes.db :as db]
            [datomic.api :as d :refer [q]]
            [clojure.test :as t :refer [deftest use-fixtures is]]))

(def test-uri "datomic:mem://re-recipes-test")

(defn connect-and-apply-schema [f]
  (d/create-database test-uri)
  (let [conn (d/connect test-uri)]
    @(d/transact conn db/schema)
    (def db (d/db conn)))
  (f)
  (d/delete-database test-uri))

(use-fixtures :once connect-and-apply-schema)

(defn speculate [t]
  (:db-after
   (d/with db t)))

(deftest add-ingredient-succeeds
  (is (= "potato"
        (:ingredient/name
         (db/ingredient-by-name
           (speculate
             (db/add-ingredient-tx
               {:ingredient/name "potato"}))
           "potato")))))
