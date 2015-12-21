(ns re-recipes.handlers-test
  (:require [goog.string :refer [splitLimit]]
            [cljs.test :refer-macros [deftest testing is]]
            [schema.core :as s]
            [re-recipes.schema :as schema]
            [re-recipes.db-test :as db]
            [re-recipes.handlers :as h]))

(s/def valid-recipe :- schema/Recipe
  {:name "recipe"
   :ingredients ["stuff" "other stuff"]
   :url "https://www.blueapron.com/recipes/foo-bar"
   :his-rating {:stars 5 :review "meh"}
   :her-rating {:stars 1 :review "ZOMG"}})

(def empty-review-recipe (merge valid-recipe {:his-rating {:stars 5 :review ""}}))

(def empty-name-recipe (merge valid-recipe {:name ""}))
(def no-ingredients-recipe (merge valid-recipe {:ingredients []}))
(def empty-ingredient-recipe (merge valid-recipe {:ingredients ["" "toast"]}))
(def no-url-recipe (merge valid-recipe {:url ""}))
(def invalid-url-recipe (merge valid-recipe {:url "http://www.google.com"}))
(def no-his-rating-recipe (merge valid-recipe {:his-rating nil}))
(def no-her-rating-recipe (merge valid-recipe {:her-rating nil}))

(defn adds-recipe? [recipe]
  (some #(= recipe %) (vals (:recipes (h/add-recipe db/test-data [:add-recipe recipe])))))

(deftest adds-valid-recipe
  (testing "Add handler adds a valid recipe"
    (is (adds-recipe? valid-recipe))))

(deftest adds-empty-review-recipe
  (testing "Add handler adds a recipe with an empty review"
    (is (adds-recipe? empty-review-recipe))))

(def doesnt-add-recipe? (comp not adds-recipe?))

(deftest doesnt-add-invalid-recipes
  (testing "Add handler doesn't add invalid recipes"
    (testing "Add handler doesn't add empty name recipe"
      (is (doesnt-add-recipe? empty-name-recipe)))
    (testing "Add handler doesn't add recipe with no ingredients"
      (is (doesnt-add-recipe? no-ingredients-recipe)))
    (testing "Add handler doesn't add recipe with an empty ingredient string"
      (is (doesnt-add-recipe? empty-ingredient-recipe)))
    (testing "Add handler doesn't add empty url recipe"
      (is (doesnt-add-recipe? no-url-recipe)))
    (testing "Add handler doesn't add invalid url recipe"
      (is (doesnt-add-recipe? invalid-url-recipe)))
    (testing "Add handler doesn't add recipe with no his-rating"
      (is (doesnt-add-recipe? no-his-rating-recipe)))
    (testing "Add handler doesn't add recipe with no her-rating"
      (is (doesnt-add-recipe? no-her-rating-recipe)))))
