(ns user
  (:require [reloaded.repl :refer [system init start stop go reset reset-all]]
            [re-recipes.system :refer [dev-system]]))

(reloaded.repl/set-init! #(dev-system {:port 3000}))
