(ns code-sniff.cli
  (:require
    [code-sniff.conv.cloc :as cloc]
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
                     *out*)]
      (try
        (case (first arguments)
          "slurp-cloc" (cloc/convert-yaml-to-json in-file out-file)
          "combine" (if (:base options)
                      (combine/combine-files (:base options) in-file out-file)
                      (combine/combine-files in-file out-file))
          (exit 1 (usage summary)))
        (finally
          (if (:input options)
            (.close in-file))
          (if (:output options)
            (.close out-file)))))))