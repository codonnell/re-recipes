(ns re-recipes.subs-test
  (:require [cljs.test :refer-macros [deftest testing is]]
            [re-recipes.db-test :as db]
            [re-recipes.subs :as subs]))

(deftest gets-existing-recipe
  (testing "get-recipe retrieves existing recipe"
    (is (= db/first-recipe (subs/get-recipe db/test-data [:recipe (uuid "0")])))))

(deftest get-recipe-invalid-id
  (testing "get-recipe returns :invalid-id when recipe doesn't exist in db"
    (is (= :invalid-id (subs/get-recipe db/test-data [:recipe (random-uuid)])))))

(deftest active-recipes-nil
  (testing "active-recipes gets all recipe ids when :active-recipes isn't set"
    (is (= (keys (:recipes db/test-data)) (subs/active-recipes db/test-data [:active-recipes])))))

(deftest active-recipes-non-nil
  (testing "active-recipes gets the specified recipe ids when :active-recipes is set"
    (is (= [(uuid "0")] (subs/active-recipes
                          (merge db/test-data {:active-recipes [(uuid "0")]})
                          [:active-recipes])))))
