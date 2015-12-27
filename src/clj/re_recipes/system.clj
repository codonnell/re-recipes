(ns re-recipes.system
  (:require [com.stuartsierra.component :as component]
            [re-recipes.server :as server]))

(defn dev-system [config-options]
  (let [{:keys [port handler]} config-options]
    (component/system-map
      :server (server/new-server port handler))))

