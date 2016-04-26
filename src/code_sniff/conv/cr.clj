(ns code-sniff.conv.cr
  (require [cheshire.core :as cheshire])
  (:import (java.io File Reader Writer)))

; complexity-report

; cr -f json --ignoreerrors -o ../d3_out/complexity.json
(defn- relativize "strip prefix of one path from another"
  ; note - you could do this with Java 1.7 Path/relativize
  ; but I'd prefer to stay friendly for old Java versions.
  [path basepath]
  ; new File(base).toURI().relativize(new File(path).toURI()).getPath()
  (let [base-uri (-> basepath File. .toURI)
        uri (-> path File. .toURI)]
    (.getPath (.relativize base-uri uri))))

(defn- data-for [report]
  (let [cyclomatics (map :cyclomatic (:functions report))
        worst-cyclo (if (empty? cyclomatics)
                      nil
                      (apply max cyclomatics))]
    (merge (select-keys report [:cyclomatic :maintainability :effort])
           {:worst-cyclomatic worst-cyclo})))

(defn convert "convert a parsed cr json structure to Slurp format"
  [input basepath category]
  (for [report (:reports input)
        :let [filename (if basepath
                         (relativize (:path report) basepath)
                         (:path report))]]
    (do
      {:filename filename
       :data     {(or category :complexity-report) (data-for report)}})))

(defn convert-json-to-json "convert input json data to output json data"
  [{:keys [basepath category] :as config} ^Reader in-file ^Writer out-file]
  (-> in-file
      (cheshire/parse-stream true)
      (convert basepath category)
      (cheshire/generate-stream out-file {:pretty true})))

