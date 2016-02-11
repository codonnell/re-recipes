(ns re-recipes.validation
  (:require
   [schema.core :as s]
   [re-recipes.schema :as schema]))

(defn non-empty-string? [input]
  (not (empty? input)))

(def valid-recipe-name? non-empty-string?)

(def valid-ingredient? non-empty-string?)

(def url-regex #"https?://(www\.)?blueapron\.com/recipes/([a-z\-]+)")
(defn valid-url? [input]
  (re-find url-regex input))

(defn valid-stars? [input]
  (and (pos? input) (<= input 5)))

(defn valid-recipe? [recipe]
  (try (s/validate schema/Recipe recipe) ;; Checks that types are proper
       (and ;; Custom validation conditions
         (valid-recipe-name? (:name recipe))
         (not (empty? (:ingredients recipe)))
         (every? identity (map valid-ingredient? (:ingredients recipe)))
         ;; (valid-url? (:url recipe))
         (valid-stars? (get-in recipe [:his-rating :stars]))
         (valid-stars? (get-in recipe [:her-rating :stars])))
       (catch #?(:cljs :default :clj Exception) e
         false)))
