(ns re-recipes.schema
  (:require [schema.core :as s
             #?(:cljs :include-macros :true)]))

(def non-empty-string (s/conditional s/Str (comp not empty?)))

(def Rating
  "A schema for a recipe rating"
  {:stars s/Int
   :review s/Str})

(def Recipe
  "A schema for a recipe record"
  {:name s/Str
   :ingredients [s/Str]
   :url s/Str
   :his-rating Rating
   :her-rating Rating})

(def Database
  "A schema for the re-frame database"
  {:recipes {s/Uuid Recipe}
   (s/optional-key :active-panel) s/Keyword})
