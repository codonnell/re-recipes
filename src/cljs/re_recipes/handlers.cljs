(ns re-recipes.handlers
  (:require [re-frame.core :as re-frame]
            [re-recipes.ws :as ws]
            [re-recipes.db :as db]
            [re-recipes.schema :as schema]
            [re-recipes.validation :as valid]
            [schema.core :as s]))

(re-frame/register-handler
 :initialize-db
 (fn  [_ _]
   (ws/all-recipes)
   db/default-db))

(re-frame/register-handler
 :set-active-panel
 (fn [db [_ active-panel]]
   (assoc db :active-panel active-panel)))

(s/defn add-recipe [db [_ new-recipe :- schema/Recipe]]
  (if (valid/valid-recipe? new-recipe)
    (update db :recipes conj [(random-uuid) new-recipe])
    db))

(re-frame/register-handler
  :add-recipe
  add-recipe)

(s/defn add-all-recipes [db [_ recipes :- [schema/Recipe]]]
  (reduce
    (fn [db recipe] (if (valid/valid-recipe? recipe) (update db :recipes conj [(random-uuid) recipe]) db))
    db recipes))

(re-frame/register-handler
  :add-all-recipes
  add-all-recipes)
