(ns code-sniff.cli
  (:require
    [code-sniff.conv.cloc :as cloc]
    [code-sniff.conv.code-maat :as code-maat]
    [code-sniff.combine :as combine]
    [clojure.tools.cli :refer [parse-opts]]
    [clojure.java.io :as io]
    [clojure.string :as string])
  (:gen-class))

(def cli-options
  [["-i" "--input filename" "select an input file name (default is STDIN)"
    :validate [#(.exists (io/file %)) "Must be a valid file"]]
   ["-b" "--base filename" "select a base flare-format json file to combine with"]
   ["-o" "--output filename" "select an output file name (default is STDOUT)"]
   ["-c" "--category cat" "store data that is slurped, in a named category under 'data' (good for different code-maat inputs)"]
   ["-h" "--help"]])

(defn usage [options-summary]
  (->> ["Utilities for slurping and combining source code metrics"
        ""
        "Usage: code-sniff [options] action"
        ""
        "Options:"
        options-summary
        ""
        "Actions:"
        "combine - combine slurped input data into flare format - use '-b' to specify base data to merge with"
        " "
        "slurp-cloc  - slurp yaml data from cloc"
        "   - generate with 'cloc . --by-file --yaml --quiet > data.yml'"
        "  "
        "slurp-maat - slurp code-maat output - you should use a category to specify _which_ code maat output you are slurping, anything with an 'entity' column will be loaded"
        " "
        "you can pipe things together if you want, on a unix-y system:"
        "'cloc . --by-file --yaml --quiet | lein run slurp-cloc | lein run combine > out.flare'"
        "(this assumes you are running from leiningen - if using a jar file, substitute 'java -jar whatever.jar')"
        ""]
       (string/join \newline)))

(defn error-msg [errors]
  (str "The following errors occurred while parsing your command:\n\n"
       (string/join \newline errors)))

(defn exit [status msg]
  (binding [*out* *err*]
    (println msg))
  (System/exit status))

(defn -main [& args]
  (let [{:keys [options arguments errors summary]} (parse-opts args cli-options)]
    (cond
      (:help options) (exit 0 (usage summary))
      (not= (count arguments) 1) (exit 1 (usage summary))
      errors (exit 1 (error-msg errors)))
    (let [in-file (if (:input options)
                    (io/reader (:input options))
                    *in*)
          out-file (if (:output options)
                     (io/writer (:output options))
                     *out*)
          category (keyword (:category options))]
      (try
        (case (first arguments)
          "slurp-cloc" (cloc/convert-yaml-to-json category in-file out-file)
          "slurp-maat" (code-maat/convert-csv-to-json category in-file out-file)
          "combine" (if (:base options)
                      (combine/combine-files (:base options) in-file out-file)
                      (combine/combine-files in-file out-file))
          (exit 1 (usage summary)))
        (finally
          (if (:input options)
            (.close in-file))
          (if (:output options)
            (.close out-file)))))))