(ns re-recipes.db-test
  (:require [cljs.test :refer-macros [deftest testing is]]
            [schema.core :as s]
            [re-recipes.schema :as schema]
            [re-recipes.db :as db]))

(s/def first-recipe :- schema/Recipe
  {:name "tamarind-glazed cod"
   :ingredients ["cod fillets" "jasmine rice" "persian cucumber" "lime" "jicama" "cilantro" "golden mountain sauce" "rice vinegar" "honey" "ginger" "tamarind concentrate"]
   :url "https://www.blueapron.com/recipes/ginger-tamarind-glazed-cod-with-jicama-cucumber-relish-jasmine-rice"
   :his-rating {:stars 5
                :review "Delicious!"}
   :her-rating {:stars 3
                :review "Too much ginger"}})

(s/def second-recipe :- schema/Recipe
  {:name "pizza"
   :ingredients ["cheese" "tomato sauce" "pizza dough"]
   :url "http://blueapron.com/recipes/pizza"
   :his-rating {:stars 5
                :review "I LOVE PIZZA"}
   :her-rating {:stars 5
                :review "OMG ME TOO"}})

(s/def test-data :- {:recipes {s/Uuid schema/Recipe}}
  {:recipes {(uuid "0") first-recipe
             (uuid "1") second-recipe}})
