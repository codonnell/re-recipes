(ns re-recipes.core
    (:require [reagent.core :as reagent]
              [re-frame.core :as re-frame]
              [re-recipes.handlers]
              [re-recipes.subs]
              [re-recipes.routes :as routes]
              [re-recipes.views :as views]
              [re-recipes.config :as config]
              [re-recipes.ws :as ws]))

(when config/debug?
  (println "dev mode"))

(defn mount-root []
  (reagent/render [views/main-panel]
                  (.getElementById js/document "app")))

(defn ^:export init []
  (routes/app-routes)
  (re-frame/dispatch-sync [:initialize-db])
  (mount-root)
  (ws/start-router!))
