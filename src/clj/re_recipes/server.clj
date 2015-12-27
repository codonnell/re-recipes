(ns re-recipes.server
  (:require [re-recipes.handler :refer [app]]
            [environ.core :refer [env]]
            [org.httpkit.server :refer [run-server]]
            [com.stuartsierra.component :as component])
  (:gen-class))

(defn -main [& args]
  (let [port (Integer/parseInt (or (env :port) "3000"))]
    (run-server app {:port port :join? false})))

(defrecord Server [port handler]
  component/Lifecycle

  (start [component]
    (println "Starting server")
    (let [server (run-server handler {:port port :join? false})]
      (assoc component :server server)))

  (stop [component]
    (println "Stopping server")
    (when-let [stop-fn (:server component)]
      (stop-fn))
    (assoc component :server nil)))

(defn new-server [port handler]
  (map->Server {:port port :handler handler}))
