(ns re-recipes.subs
    (:require-macros [reagent.ratom :refer [reaction]])
    (:require [re-frame.core :as re-frame]))

(re-frame/register-sub
 :name
 (fn [db]
   (reaction (:name @db))))

(defn active-recipes [db _]
  (if-let [ids (:active-recipes db)]
    ids
    (keys (:recipes db))))

(re-frame/register-sub
  :active-recipes
  (fn [db v]
    (reaction (active-recipes @db v))))

(defn get-recipe [db [_ id]]
  (get-in db [:recipes id] :invalid-id))

(re-frame/register-sub
  :recipe
  (fn [db v]
    (reaction (get-recipe @db v))))

(re-frame/register-sub
 :active-panel
 (fn [db _]
   (reaction (:active-panel @db))))
