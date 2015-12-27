(ns user
  (:require [reloaded.repl :refer [system init start stop go reset reset-all]]
            [re-recipes.system :refer [dev-system]]
            [re-recipes.handler :refer [app]]))

(reloaded.repl/set-init! #(dev-system {:port 3000 :handler app}))
