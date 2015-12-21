(ns re-recipes.runner
    (:require [doo.runner :refer-macros [doo-tests]]
              [re-recipes.core-test]
              [re-recipes.handlers-test]
              [re-recipes.subs-test]))

(doo-tests 're-recipes.core-test 're-recipes.handlers-test 're-recipes.subs-test)
