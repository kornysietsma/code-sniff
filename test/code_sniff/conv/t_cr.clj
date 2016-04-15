(ns code-sniff.conv.t-cr
  (:require [midje.sweet :refer :all]
            [code-sniff.conv.cr :as subject]
            [cheshire.core :as cheshire]))

(def sample-parsed
  {:effort 280.675669322634,
   :maintainability 160.15633042856035,
   :adjacencyMatrix [[0 0] [0 0]],
   :params 0.5,
   :firstOrderDensity 0,
   :changeCost 50,
   :coreSize 0,
   :cyclomatic 1.5,
   :reports [{:path "/foo/src/color/color.js",
              :effort 19.651484454403228,
              :maintainability 171,
              :functions [{:name "d3_color",
                           :sloc {:logical 0, :physical 1},
                           :cyclomatic 1,
                           :halstead :truncated_for_clarity,
                           :params 0,
                           :line 3,
                           :cyclomaticDensity nil}
                          {:name "<anonymous>.toString",
                           :sloc {:logical 1, :physical 3},
                           :cyclomatic 1,
                           :halstead :truncated_for_clarity,
                           :params 0,
                           :line 5,
                           :cyclomaticDensity 100}],
              :params 0,
              :cyclomatic 1,
              :aggregate {:sloc {:logical 4, :physical 7},
                          :cyclomatic 1,
                          :halstead :truncated_for_clarity,
                          :params 0,
                          :line 1,
                          :cyclomaticDensity 25},
              :loc 0.5,
              :dependencies []}
             {:path "/foo/src/color/xyz.js",
              :effort 541.6998541908648,
              :maintainability 149.31266085712073,
              :functions [{:name "d3_xyz_lab",
                           :sloc {:logical 1, :physical 3},
                           :cyclomatic 2,
                           :halstead :truncated_for_clarity,
                           :params 1,
                           :line 1,
                           :cyclomaticDensity 200}
                          {:name "d3_xyz_rgb",
                           :sloc {:logical 1, :physical 3},
                           :cyclomatic 3,
                           :halstead :truncated_for_clarity,
                           :params 1,
                           :line 5,
                           :cyclomaticDensity 300}],
              :params 1,
              :cyclomatic 2,
              :aggregate {:sloc {:logical 4, :physical 7},
                          :cyclomatic 3,
                          :halstead :truncated_for_clarity,
                          :params 2,
                          :line 1,
                          :cyclomaticDensity 75},
              :loc 1,
              :dependencies []}],
   :visibilityMatrix [[0 0] [0 0]],
   :loc 0.75}
  )

(fact "converting generates summary data for each file"
      (subject/convert sample-parsed "/foo" :cat)
      =>
       [{:filename "src/color/color.js"
         :data {:cat {:cyclomatic 1
                      :effort 19.651484454403228
                      :maintainability 171
                      :worst-cyclomatic 1}}}
        {:filename "src/color/xyz.js"
         :data {:cat {:cyclomatic 2
                      :effort 541.6998541908648
                      :maintainability 149.31266085712073
                      :worst-cyclomatic 3}}}])
