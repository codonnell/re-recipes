(ns re-recipes.views
  (:require [re-frame.core :as re-frame :refer [subscribe]]
            [clojure.string :as string]))


;; home

(defn- title-case [s]
  (->> (string/split s #" ")
    (map string/capitalize)
    (string/join " ")))

(defn recipe-view [id]
  (let [recipe (subscribe [:recipe id])]
    (fn recipe-render []
      (let [{:keys [name ingredients url his-rating her-rating]} @recipe]
        [:div
         [:div.row [:a {:href url :target "_blank"} [:h3 (title-case name)]]]
         [:div.row "Ingredients: " (string/join ", " ingredients)]
         [:div.col-md-6
          [:h4 "Her Rating"]
          [:p "Stars: " (str (:stars her-rating))]
          [:p (:review her-rating)]]
         [:div.col-md-6
          [:h4 "His Rating"]
          [:p "Stars: " (str (:stars his-rating))]
          [:p (:review his-rating)]]]))))

(defn recipes-panel []
  (let [active-ids (re-frame/subscribe [:active-recipes])]
    (fn []
      [:div (for [id @active-ids]
              ^{:key id} [recipe-view id])])))

(defn home-panel []
  (fn []
    [:div.container
     [:h2 (str "Hello! This is the Home Page.")]
     [:div [:a {:href "#/about"} "go to About Page"]]
     [recipes-panel]]))

;; about

(defn about-panel []
  (fn []
    [:div "This is the About Page."
     [:div [:a {:href "#/"} "go to Home Page"]]]))


;; main

(defmulti panels identity)
(defmethod panels :home-panel [] [home-panel])
(defmethod panels :about-panel [] [about-panel])
(defmethod panels :default [] [:div])

(defn main-panel []
  (let [active-panel (re-frame/subscribe [:active-panel])]
    (fn []
      (panels @active-panel))))
