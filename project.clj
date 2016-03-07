(defproject re-recipes "0.1.0-SNAPSHOT"
  :dependencies [[org.clojure/clojure "1.7.0"]
                 [org.clojure/clojurescript "1.7.170"]
                 [org.clojure/core.async "0.2.374"]

                 [com.datomic/datomic-pro "0.9.5327" :exclusions [joda-time]]

                 [com.taoensso/sente "1.7.0"]
                 [com.taoensso/timbre "4.1.4"]

                 [com.taoensso.forks/http-kit "2.1.20"]
                 [ring "1.4.0"]
                 [ring/ring-defaults "0.1.5"]
                 [compojure "1.4.0"]
                 [environ "1.0.1"]

                 [com.stuartsierra/component "0.3.1"]
                 [valichek/component-compojure "0.2-SNAPSHOT"]

                 [hiccup "1.0.5"]
                 [reagent "0.5.1"]
                 [re-frame "0.6.0"]
                 [secretary "1.2.3"]
                 [prismatic/schema "1.0.4"]]

  :min-lein-version "2.5.3"

  :source-paths ["src/clj" "src/cljc" "test/clj"]

  :profiles {:dev {:source-paths ["dev"]
                   :dependencies [[reloaded.repl "0.2.1"]
                                  [com.cemerick/piggieback "0.2.1"]
                                  [figwheel-sidecar "0.5.0-2"]]
                   :repl-options {:nrepl-middleware [cemerick.piggieback/wrap-cljs-repl]}}}

  :plugins [[lein-cljsbuild "1.1.1"]
            [lein-figwheel "0.5.0-2"]
            [lein-doo "0.1.6"]]

  :clean-targets ^{:protect false} ["resources/public/js/compiled" "target"
                                    "test/js"]

  :figwheel {:css-dirs ["resources/public/css"]
             :ring-handler re-recipes.handler/handler}

  :cljsbuild {:builds [{:id "dev"
                        :source-paths ["src/cljs" "src/cljc"]
                        :figwheel {:on-jsload "re-recipes.core/mount-root"}
                        :compiler {:main re-recipes.core
                                   :output-to "resources/public/js/compiled/app.js"
                                   :output-dir "resources/public/js/compiled/out"
                                   :asset-path "js/compiled/out"
                                   :source-map-timestamp true}}

                       {:id "test"
                        :source-paths ["src/cljs" "test/cljs" "src/cljc"]
                        :compiler {:output-to "resources/public/js/compiled/test.js"
                                   :main re-recipes.runner
                                   :optimizations :none}}

                       {:id "min"
                        :source-paths ["src/cljs" "src/cljc"]
                        :compiler {:main re-recipes.core
                                   :output-to "resources/public/js/compiled/app.js"
                                   :optimizations :advanced
                                   :closure-defines {goog.DEBUG false}
                                   :pretty-print false}}]})

