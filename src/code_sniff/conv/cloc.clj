(ns code-sniff.conv.cloc
  (:require [clj-yaml.core :as yaml]
            [cheshire.core :as cheshire]
            [clojure.walk :refer [keywordize-keys]])
  (:import (java.io Reader Writer)
           (clojure.lang Keyword)))

(defn- normalize "strip ./ from unix-style paths"
  ; note - you could do this with Java 1.7 Path/normalize
  ; but I'd prefer to stay friendly for old Java versions.
  ; TODO: what happens in Windows???
  [path]
  (if (clojure.string/starts-with? path "./")
    (subs path 2)
    path))

(defn convert "convert a parsed cloc yaml structure to Slurp format"
  [input category]
  (for [[name data] input
        :when (not (#{"SUM" "header"} name)) ]
    {:filename (normalize name)
     :data     {(or category :cloc) (keywordize-keys data)}}))

(defn convert-yaml-to-json "convert input yaml data to output json data"
  [^Keyword category ^Reader in-file ^Writer out-file]
  (-> in-file
      slurp
      (yaml/parse-string false)
      (convert category)
      (cheshire/generate-stream out-file {:pretty true})))

; TODO: schemas!
