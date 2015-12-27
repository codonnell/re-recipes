(ns re-recipes.system
  (:require [com.stuartsierra.component :as component]
            [re-recipes.server :as server]
            [re-recipes.db :as db]))

(defn dev-system [config-options]
  (let [{:keys [port handler]} config-options]
    (component/system-map
      :db (db/new-db "datomic:mem://re-recipes")
      :server (component/using
                (server/new-server port handler)
                [:db]))))

