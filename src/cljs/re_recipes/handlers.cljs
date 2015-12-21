(ns re-recipes.handlers
  (:require [schema.core :as s]
            [re-frame.core :as re-frame]
            [re-recipes.db :as db]
            [re-recipes.validation :as valid]))

(re-frame/register-handler
 :initialize-db
 (fn  [_ _]
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
