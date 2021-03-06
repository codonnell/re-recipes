(ns re-recipes.ws
  (:require-macros
   [cljs.core.async.macros :as asyncm :refer (go go-loop)])
  (:require [cljs.core.async :as async :refer (<! >! put! chan)]
            [taoensso.encore :as encore :refer (debugf)]
            [taoensso.sente :as sente :refer (cb-success?)]
            [re-frame.core :as re-frame]))

(enable-console-print!)

(let [{:keys [chsk ch-recv send-fn state]}
      (sente/make-channel-socket! "/chsk" ; Note the same path as before
        {:type :auto ; e/o #{:auto :ajax :ws}
         })]
  (def chsk       chsk)
  (def ch-chsk    ch-recv) ; ChannelSocket's receive channel
  (def chsk-send! send-fn) ; ChannelSocket's send API fn
  (def chsk-state state)   ; Watchable, read-only atom
  )

(defn event-msg-handler [ev-msg]
  nil)

(def router (atom nil))
(defn stop-router! [] (when-let [stop-fn @router] (stop-fn)))
(defn start-router! []
  (stop-router!)
  (reset! router (sente/start-chsk-router! ch-chsk event-msg-handler)))
