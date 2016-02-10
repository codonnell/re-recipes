(ns re-recipes.system
  (:require [com.stuartsierra.component :as component]
            [re-recipes.server :as server]
            [re-recipes.handler :as handler]
            [re-recipes.ws :as ws]
            [re-recipes.db :as db]))

(defn dev-system [config-options]
  (let [{:keys [port]} config-options]
    (component/system-map
      :db (db/new-db "datomic:mem://re-recipes")
      :ws (component/using
           (ws/new-ws)
           [:db])
      :routes (component/using
                (handler/new-routes)
                [:db :ws])
      :server (component/using
                (server/new-server port)
                [:db :ws :routes]))))

