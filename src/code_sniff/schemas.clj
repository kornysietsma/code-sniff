(ns code-sniff.schemas
  (:require [schema.core :as s]))

; flare - see https://gist.github.com/mbostock/1093025

(def Flare
  "Schema for d3 visualisations"
  {:name s/Str
   (s/optional-key :filename) s/Str
   (s/optional-key :size) s/Int
   (s/optional-key :children) [(s/recursive #'Flare)]
   (s/optional-key :data) {s/Keyword s/Any}})

(def Slurp
  "Schema for intermediate slurped data - keep it simple!"
  [{:filename s/Str
    :data {s/Keyword s/Any}}])