(ns re-recipes.ws
  (:require [taoensso.sente :as sente]
            [taoensso.sente.server-adapters.http-kit :refer [sente-web-server-adapter]]
            [clojure.core.async :as async :refer [go-loop <!]]))

(let [{:keys [ch-recv send-fn ajax-post-fn ajax-get-or-ws-handshake-fn connected-uids]}
      (sente/make-channel-socket! sente-web-server-adapter {})]
  (def ring-ajax-post ajax-post-fn)
  (def ring-ajax-get-or-ws-handshake ajax-get-or-ws-handshake-fn)
  (def ch-chsk ch-recv)
  (def chsk-send! send-fn)
  (def connected-uids connected-uids))

(defn broadcast [data]
  (doseq [uid (:any @connected-uids)]
    (chsk-send! uid data)))

(defn event-msg-handler [ev-msg]
  nil)

(defonce router (atom nil))

(defn stop-router! [] (when-let [stop-fn @router] (stop-fn)))
(defn start-router! []
  (stop-router!)
  (reset! router (sente/start-chsk-router! ch-chsk event-msg-handler)))

(start-router!)
