(ns code-sniff.conv.t-cloc
  (:require [midje.sweet :refer :all]
            [code-sniff.conv.cloc :as subject]
            ))

(def parsed-sample {"SUM" {"blank" 40, "code" 2328, "comment" 2, "nFiles" 61},
                    "./test/metrics_slurp/conv/t_cloc.clj" {"blank" 0, "comment" 0, "code" 1, "language" "Clojure"},
                    "./project.clj" {"blank" 0, "comment" 0, "code" 20, "language" "Clojure"},
                    "./test/metrics_slurp/flare_sample.json" {"blank" 0, "comment" 0, "code" 380, "language" "JSON"},
                    "./src/metrics_slurp/core.clj" {"blank" 1, "comment" 0, "code" 5, "language" "Clojure"},
                    "./src/metrics_slurp/conv/cloc.clj" {"blank" 0, "comment" 0, "code" 1, "language" "Clojure"},
                    "./src/metrics_slurp/combine.clj" {"blank" 7, "comment" 0, "code" 37, "language" "Clojure"},
                    "./src/metrics_slurp/cli.clj" {"blank" 5, "comment" 0, "code" 51, "language" "Clojure"},
                    "./test/metrics_slurp/core_test.clj" {"blank" 2, "comment" 0, "code" 3, "language" "Clojure"},
                    "./src/metrics_slurp/schemas.clj" {"blank" 3, "comment" 1, "code" 13, "language" "Clojure"},
                    "./test/metrics_slurp/t_combine.clj" {"blank" 14, "comment" 0, "code" 75, "language" "Clojure"},
                    "./test/metrics_slurp/t_schemas.clj" {"blank" 8, "comment" 1, "code" 24, "language" "Clojure"},
                    "header" {"cloc_url" "http://cloc.sourceforge.net",
                              "cloc_version" 1.62,
                              "elapsed_seconds" 0.320614099502563,
                              "n_files" 61,
                              "n_lines" 2370,
                              "files_per_second" 190.259879695379,
                              "lines_per_second" 7392.06417832866}})

(def trivial-sample {"SUM" {:ignored :stuff}
                     "header" {:ignored :stuff}
                     "./foo.clj" {"blank" 0 "comment" 0 "code" 1 "language" "Clojure"}
                     "./baz/bat.php" {"blank" 1 "comment" 2 "code" 3 "language" "Php"}})

(fact "input data is validated against a minimal schema")

(fact "you can convert parsed cloc data into a Slurp format"
      (subject/convert trivial-sample)
      =>
      (just [{:filename "foo.clj" :data {:cloc {:blank 0 :comment 0 :code 1 :language "Clojure"}}}
             {:filename "baz/bat.php" :data {:cloc {:blank 1 :comment 2 :code 3 :language "Php"}}}]
            :in-any-order))

