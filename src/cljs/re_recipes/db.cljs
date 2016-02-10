(ns re-recipes.db)

;; (def default-db
;;   {:recipes {(random-uuid) {:name "tamarind-glazed cod"
;;                             :ingredients ["cod fillets" "jasmine rice" "persian cucumber" "lime" "jicama" "cilantro" "golden mountain sauce" "rice vinegar" "honey" "ginger" "tamarind concentrate"]
;;                             :url "https://www.blueapron.com/recipes/ginger-tamarind-glazed-cod-with-jicama-cucumber-relish-jasmine-rice"
;;                             :his-rating {:stars 5
;;                                          :review "Delicious!"}
;;                             :her-rating {:stars 3
;;                                          :review "Too much ginger"}}}})

(def default-db
  {:recipes {}
   :loading false})
