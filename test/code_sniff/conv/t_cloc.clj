(ns code-sniff.conv.t-cloc
  (:require [midje.sweet :refer :all]
            [code-sniff.conv.cloc :as subject]
            ))


(def trivial-sample {"SUM" {:ignored :stuff}
                     "header" {:ignored :stuff}
                     "./foo.clj" {"blank" 0 "comment" 0 "code" 1 "language" "Clojure"}
                     "./baz/bat.php" {"blank" 1 "comment" 2 "code" 3 "language" "Php"}})

(fact "input data is validated against a minimal schema")

(fact "you can convert parsed cloc data into a Slurp format"
      (subject/convert trivial-sample :cat)
      =>
      (just [{:filename "foo.clj" :data {:cat {:blank 0 :comment 0 :code 1 :language "Clojure"}}}
             {:filename "baz/bat.php" :data {:cat {:blank 1 :comment 2 :code 3 :language "Php"}}}]
            :in-any-order))

