(ns code-sniff.t-schemas
  (:require [midje.sweet :refer :all]
            [schema.core :as s]
            [cheshire.core :as cheshire]
            [code-sniff.schemas :as subject]
            ))

(fact "flare structures always have a root 'flare' element")
; TODO: allow data at child nodes!

(fact "Flare schema handles a range of data"
      (s/check subject/Flare {} =not=> nil)
      (s/check subject/Flare {:name "goo" :children [{:name "oy" :size 3 :data {:foo 123 :bar ["bar"]}}]})
      => nil)

(fact "Flare validates actual json parsed in"
      (->
        "test/metrics_slurp/flare_sample.json"
        clojure.java.io/reader
        (cheshire/parse-stream true)
        (#(s/check subject/Flare %)))
      => nil)

(fact "Flare names have no file separators")

(fact "Slurp schema handles intermediate data for slurping"
      (s/check subject/Slurp [{:filename "foo/bar.txt" :data {:baz :bat}}
                              {:filename "a/b/c.txt" :data {}}]) => nil)

(fact "Slurp filenames are relative")

(fact "Slurp filenames don't end with a file separator")

