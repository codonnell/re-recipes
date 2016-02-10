(ns re-recipes.ws
  (:require [taoensso.sente :as sente]
            [taoensso.timbre :refer [debug]]
            [taoensso.sente.server-adapters.http-kit :refer [sente-web-server-adapter]]
            [clojure.core.async :as async :refer [go-loop <!]]
            [com.stuartsierra.component :as component]
            [re-recipes.db :as db]
            [clojure.pprint :refer [pprint]]))

;; (defmulti event-msg-handler (fn [_ ev-msg] (:id ev-msg)))

;; (defmethod event-msg-handler :recipe/all
;;   [db {:as ev-msg :keys [event ?reply-fn]}]
;;   (println ev-msg)
;;   (?reply-fn {:text "hello"}))

;; (defmethod event-msg-handler :default
;;   [db {:as ev-msg :keys [event]}]
;;   (println "Unhandled event:" event))

;; (defn event-msg-handler* [db ev-msg]
;;   (event-msg-handler db ev-msg))

(defn event-msg-handler* [{:keys [?reply-fn event] :as ev-msg}]
  (debug "Received message: " (str event))
  (when ?reply-fn
    (?reply-fn {:text "hello"})))

(defrecord WebSocket [db
                      ring-ajax-post
                      ring-ajax-get-or-ws-handshake
                      ch-recv
                      send-fn
                      connected-uids]
  component/Lifecycle

  (start [component]
    (let [{:keys [ch-recv send-fn ajax-post-fn ajax-get-or-ws-handshake-fn connected-uids] :as socket}
          (sente/make-channel-socket! sente-web-server-adapter {})

          stop-fn (sente/start-chsk-router! ch-recv event-msg-handler*)]
      (assoc component
             :ring-ajax-post ajax-post-fn
             :ring-ajax-get-or-ws-handshake ajax-get-or-ws-handshake-fn
             :ch-recv ch-recv
             :send-fn send-fn
             :connected-uids connected-uids
             :stop-fn stop-fn)))

  (stop [component]
    ((get component :stop-fn))
    (assoc component
           :ring-ajax-post nil
           :ring-ajax-get-or-ws-handshake nil
           :ch-recv nil
           :send-fn nil
           :connected-uids nil
           :stop-fn nil)))

(defn new-ws [] (map->WebSocket {}))
