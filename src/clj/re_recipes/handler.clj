(ns re-recipes.handler
  (:require [re-recipes.ws :as ws]
            [compojure.core :refer [GET POST defroutes]]
            [compojure.route :refer [resources]]
            [ring.middleware.defaults :refer [site-defaults wrap-defaults]]
            [ring.middleware.reload :refer [wrap-reload]]
            [environ.core :refer [env]]
            [ring.util.response :refer [file-response]]))

(defroutes routes
  (GET "/chsk" req (ws/ring-ajax-get-or-ws-handshake req))
  (POST "/chsk" req (ws/ring-ajax-post req))
  (resources "/"))

(def app
  (let [handler (wrap-defaults #'routes site-defaults)]
    (if (env :dev)
      (-> handler
        wrap-reload)
      handler)))
