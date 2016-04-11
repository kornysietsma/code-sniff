(ns code-sniff.conv.code-maat
  (:require [cheshire.core :as cheshire]
            [clojure-csv.core :as csv]
            [semantic-csv.core :as sc]
            [clojure.java.io :as io]
            [clojure.walk :refer [keywordize-keys]])
  (:import (java.io Reader Writer)))

; Code-maat uses csv formats, can avoid a lot of duplication...

(defn convert "convert parsed csv into Slurp format with a given sub-namespace"
  [input category]
  (for [row input]
    (let [filename (:entity row)
          data (dissoc row :entity)]
      {:filename filename
       :data     {(or category :code-maat) (keywordize-keys data)}})))

(defn convert-csv-to-json "convert input yaml data to output json data"
  [category ^Reader in-file ^Writer out-file]
  (-> in-file
      csv/parse-csv
      sc/process
      (convert category)
      (cheshire/generate-stream out-file {:pretty true})))

; TODO - tests!

(comment

  (with-open [in-file (io/reader "../sample_projects/code-maat-logs/d3_age.csv")]
    (doall
      (sc/process (csv/parse-csv in-file))))

  )