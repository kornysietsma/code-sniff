(defproject code-sniff "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [me.raynes/fs "1.4.6"]
                 [me.raynes/conch "0.8.0"]
                 [clj-yaml "0.4.0"]
                 [org.clojure/data.xml "0.0.8"]
                 [org.clojure/data.zip "0.1.1"]
                 [org.clojure/tools.cli "0.3.3"]
                 [prismatic/schema "1.1.0"]
                 [cheshire "5.5.0"]
                 [clj-yaml "0.4.0"]
                 [clojure-csv/clojure-csv "2.0.1"]
                 [semantic-csv "0.1.0"]]
  :main code-sniff.cli
  :profiles {
             :uberjar {:aot :all}
             :dev {:dependencies [[midje "1.8.3"]]}
             }
  :plugins [[lein-midje "3.2"]]
  )
