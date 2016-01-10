(ns re-recipes.handler
  (:require [re-recipes.ws :as ws]
            [compojure.core :refer [GET POST]]
            [component.compojure :refer [defroutes]]
            [ring.middleware.defaults :refer [site-defaults wrap-defaults]]
            [clojure.pprint :refer [pprint]]))

(defroutes ServerRoutes [db ws]
  (GET "/chsk" req (do ;(pprint req)
                       ((get-in req [:system-deps :ws :ajax-get-or-ws-handshake-fn]) req)))
  (POST "/chsk" req (do ;(pprint req)
                        ((get-in req [:system-deps :ws :ajax-post-fn]) req))))

(defn new-routes [] (map->ServerRoutes {}))

(defn get-routes [routes]
  (wrap-defaults routes site-defaults))
