(ns re-recipes.server
  (:require [re-recipes.handler :refer [get-routes]]
            [environ.core :refer [env]]
            [org.httpkit.server :refer [run-server]]
            [com.stuartsierra.component :as component])
  (:gen-class))

(defrecord Server [port db ws routes]
  component/Lifecycle

  (start [component]
    (println "Starting server")
    (let [server (run-server (get-routes (:routes routes)) {:port port :join? false})]
      (assoc component :server server)))

  (stop [component]
    (println "Stopping server")
    (when-let [stop-fn (:server component)]
      (stop-fn))
    (assoc component :server nil)))

(defn new-server [port]
  (map->Server {:port port}))
