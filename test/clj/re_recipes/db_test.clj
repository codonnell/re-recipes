(ns re-recipes.db-test
  (:require [re-recipes.db :as db]
            [datomic.api :as d :refer [q]]
            [com.stuartsierra.component :as component]
            [clojure.test :as t :refer [deftest use-fixtures is]]))

(def test-uri "datomic:mem://re-recipes-test")

(defn speculate [db t]
  (:db-after
   (d/with db t)))

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

(defn connect-apply-schema-create-dbs [f]
  (let [db (component/start (db/new-db test-uri))
        conn (:db db)]
    (def empty-db (d/db conn))
    (def test-db (speculate empty-db db/test-data))
    (def two-recipes-db (speculate test-db (db/add-recipe-tx test-recipe)))
    (f)
    (component/stop db)))

(use-fixtures :once connect-apply-schema-create-dbs)

(deftest recipe-by-name
  (let [recipe (db/recipe-by-name* test-db "Ginger bread")]
    (is (= (:recipe/name recipe) "Ginger bread"))
    (is (= (:recipe/url recipe) "https://www.blueapron.com/recipes/ginger-bread"))
    (is (= (:recipe/his-rating recipe) {:rating/stars 5 :rating/review "Pretty good!"}))))

(deftest recipes-by-ingredient
  (is (= 2 (count (db/recipes-by-ingredient* two-recipes-db "ginger")))))

(deftest recipe-search
  (is (= 1 (count (db/recipe-search* two-recipes-db "bread"))))
  (is (= 2 (count (db/recipe-search* two-recipes-db "ginger"))))
  (is (= 1 (count (db/recipe-search* two-recipes-db "oil")))))

(deftest all-recipes
  (is (= 2 (count (db/all-recipes* two-recipes-db))))
  (is (= 1 (count (db/all-recipes* test-db))))
  (is (= 0 (count (db/all-recipes* empty-db)))))

(deftest add-ingredient-succeeds
  (is (= "potato"
        (:ingredient/name
         (db/ingredient-by-name*
           (speculate
             empty-db
             (db/add-entity-tx
               {:ingredient/name "potato"}))
           "potato")))))

(deftest add-recipe-succeeds
  (let [db-with-recipe (speculate empty-db (db/add-recipe-tx test-recipe))]
    (is (= test-recipe (db/recipe-by-name* db-with-recipe "latkes")))
    (is (= test-recipe (first (db/recipes-by-ingredient* db-with-recipe "oil"))))))

(def densed-test-recipe
  {:name "latkes"
   :url "http://www.latkes.com"
   :his-rating
   {:stars 4 :review "Tasty"}
   :her-rating
   {:stars 5 :review "Delicious"}
   :ingredients ["potato" "oil" "ginger"]})

(deftest namespace-recipe
  (is (= test-recipe (db/namespace-recipe (db/denamespace-recipe test-recipe))))
  (is (= densed-test-recipe (db/denamespace-recipe test-recipe)))
  (is (= test-recipe (db/namespace-recipe densed-test-recipe))))
