(ns re-recipes.ws
  (:require [taoensso.sente :as sente]
            [taoensso.sente.server-adapters.http-kit :refer [sente-web-server-adapter]]
            [clojure.core.async :as async :refer [go-loop <!]]
            [com.stuartsierra.component :as component]))

(defn event-msg-handler [ev-msg]
  nil)

(defrecord WebSocket []
  component/Lifecycle

  (start [component]
    (let [{:keys [ch-recv] :as socket} (sente/make-channel-socket! sente-web-server-adapter {})
          stop-fn (sente/start-chsk-router! ch-recv event-msg-handler)
          ret-component (merge component (assoc socket :stop-fn stop-fn))]
      (println "starting" ret-component)
      ret-component))

  (stop [component]
    (println "stopping" component)
    ((get component :stop-fn))
    (map->WebSocket {})))

(defn new-ws [] (map->WebSocket {}))
