(ns code-sniff.t-combine
  (:require [midje.sweet :refer :all]
            [code-sniff.combine :as subject]
            ))

(fact "combining only accepts Flare and Slurp schemas")


(fact "combining with an empty map builds a 'flare' root and children"

      (subject/combine {:name "flare" :children []}
                       [{:filename "foo/bar.txt" :data {:foo :bar}}])
      =>
      {:name "flare"
       :children [{:name "foo"
                   :children [{:name "bar.txt"
                               :data {:foo :bar}}]}]}
      )

(fact "combining multiple files adds them to the structure"

      (subject/combine {:name "flare" :children []}
                       [{:filename "foo/bar.txt" :data {:size 2}}
                        {:filename "foo/baz.txt" :data {:size 3}}
                        {:filename "another/file.txt" :data {:size 4}}])
      =>
      {:name     "flare"
       :children [{:name     "foo"
                      :children [{:name "bar.txt"
                                  :data {:size 2}}
                                 {:name "baz.txt"
                                  :data {:size 3}}]}
                     {:name     "another"
                      :children [{:name "file.txt" :data {:size 4}}]}]}
      )

(fact "You can store and combine data at non-leaf nodes"

      (subject/combine {:name "flare" :children []}
                       [{:filename "foo/bar" :data {:dir true}}
                        {:filename "foo/bar/baz.txt" :data {:size 3}}
                        {:filename "foo/bar" :data {:extra true}}])
      =>
      {:name     "flare"
       :children [{:name     "foo"
                   :children [{:name "bar"
                               :data {:dir true :extra true}
                               :children [{:name "baz.txt"
                                           :data {:size 3}}
                                          ]}]}]}
      )

(fact "combining data with simple data overrides"

      (subject/combine {:name "flare" :children []}
                       [{:filename "foo/bar.txt" :data {:size 2}}
                        {:filename "foo/bar.txt" :data {:size 3}}])
      =>
      {:name     "flare"
       :children [{:name     "foo"
                   :children [{:name "bar.txt"
                               :data {:size 3}}]}]})

(fact "combining data with lists concatenates"

      (subject/combine {:name "flare" :children []}
                       [{:filename "foo/bar.txt" :data {:fish [:red]}}
                        {:filename "foo/bar.txt" :data {:fish [:blue]}}])
      =>
      {:name     "flare"
       :children [{:name     "foo"
                   :children [{:name "bar.txt"
                               :data {:fish [:red :blue]}}]}]})

(fact "combining data with maps merges recursively"

      (subject/combine {:name "flare" :children []}
                       [{:filename "foo/bar.txt" :data {:kids {:roy {:traits [:evil]
                                                                     :age 99}
                                                               :patty "yay"}}}
                        {:filename "foo/bar.txt" :data {:kids {:roy {:traits [:good]
                                                                     :age 17}}}}])
      =>
      {:name     "flare"
       :children [{:name     "foo"
                   :children [{:name "bar.txt"
                               :data {:kids {:roy {:traits [:evil :good]
                                                   :age 17}
                                             :patty "yay"}}}]}]})
