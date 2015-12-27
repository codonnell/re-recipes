(ns re-recipes.db-test
  (:require [re-recipes.db :as db]
            [datomic.api :as d :refer [q]]
            [clojure.test :as t :refer [deftest use-fixtures is]]))

(def test-uri "datomic:mem://re-recipes-test")

(defn connect-apply-schema-create-dbs [f]
  (d/create-database test-uri)
  (let [conn (d/connect test-uri)]
    @(d/transact conn db/schema)
    (def empty-db (d/db conn))
    (def test-db (:db-after (d/with empty-db db/test-data))))
  (f)
  (d/delete-database test-uri))

(use-fixtures :once connect-apply-schema-create-dbs)

(defn speculate [t db]
  (:db-after
   (d/with db t)))

(deftest recipe-by-name
  (let [recipe (db/recipe-by-name test-db "Ginger bread")]
    (is (= (:recipe/name recipe) "Ginger bread"))
    (is (= (:recipe/url recipe) "https://www.blueapron.com/recipes/ginger-bread"))
    (is (= (:recipe/his-rating recipe) {:rating/stars 5 :rating/review "Pretty good!"}))))

(deftest recipes-by-ingredient
  (let [db-with-recipes (speculate (db/add-recipe-tx test-recipe) test-db)]
    (is (= 2 (count (db/recipes-by-ingredient db-with-recipes "ginger"))))))

(deftest recipe-search
  (let [db-with-recipes (speculate (db/add-recipe-tx test-recipe) test-db)]
    (is (= 1 (count (db/recipe-search db-with-recipes "bread"))))
    (is (= 2 (count (db/recipe-search db-with-recipes "ginger"))))
    (is (= 1 (count (db/recipe-search db-with-recipes "oil"))))))

(deftest all-recipes
  (let [db-with-recipes (speculate (db/add-recipe-tx test-recipe) test-db)]
    (is (= 2 (count (db/all-recipes db-with-recipes))))
    (is (= 1 (count (db/all-recipes test-db))))
    (is (= 0 (count (db/all-recipes empty-db))))))

(deftest add-ingredient-succeeds
  (is (= "potato"
        (:ingredient/name
         (db/ingredient-by-name
           (speculate
             (db/add-entity-tx
               {:ingredient/name "potato"})
             empty-db)
           "potato")))))

(def test-recipe
  {:recipe/name "latkes"
   :recipe/url "http://www.latkes.com"
   :recipe/his-rating
   {:rating/stars 4 :rating/review "Tasty"}
   :recipe/her-rating
   {:rating/stars 5 :rating/review "Delicious"}
   :recipe/ingredients
   [{:ingredient/name "potato"} {:ingredient/name "oil"} {:ingredient/name "ginger"}]
   })

(deftest add-recipe-succeeds
  (let [db-with-recipe (speculate (db/add-recipe-tx test-recipe) empty-db)]
    (is (= test-recipe (db/recipe-by-name db-with-recipe "latkes")))
    (is (= test-recipe (first (db/recipes-by-ingredient db-with-recipe "oil"))))))
