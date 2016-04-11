(ns code-sniff.combine
  (:require [cheshire.core :as cheshire])
  (:import (java.io File Writer Reader)))

(defn add-to-flare
  "add data to a flare structure when we know the file doesn't already exist"
  [{:keys [children] :as flare} [name & names] data]
  (let [new-child (if names
                    (-> {:name name :children []}
                        (add-to-flare names data))
                    {:name name :data data})]
    (assoc flare :children (conj children new-child))))

(defn- data-merge [v1 v2]
  (cond (and (sequential? v1) (sequential? v2)) (concat v1 v2)
        (and (associative? v1) (associative? v2)) (merge-with data-merge v1 v2)
        :else v2))

(defn merge-flare-data
  "merge new data with an existing flare node"
  [flare data]
  (assoc flare :data (merge-with data-merge (:data flare) data)))

(defn update-in-flare
  "like update-in but uses 'name' and 'children' for flare structures"
  [flare [name & names :as all-names] data]
  (let [{matches true mismatches false} (group-by #(= name (:name %)) (:children flare))
        mismatches (or mismatches [])]                      ; as nil mismatches causes strangeness
    (if-not matches
      (add-to-flare flare all-names data)
      (let [match (first matches)]
        (assert (empty (rest matches)) (str "multiple children with name " name))
        (if names
          (assoc flare :children (conj mismatches (update-in-flare match names data)))
          (assoc flare :children (conj mismatches (merge-flare-data match data))))))))

(def filesep (re-pattern File/separator))

(defn- combinefn [flare {:keys [filename data] :as val}]
  (let [pathbits (clojure.string/split filename filesep)]
    (update-in-flare flare pathbits data)))

(defn combine "Combine Slurped data into a Flare structure"
  [flare slurp]
  (reduce combinefn flare slurp))

(def empty-flare {:name "flare" :children []})

(defn combine-files "read files and combine, send output to file"
  ([^Reader in-file ^Writer out-file]
    (->
      in-file
      (cheshire/parse-stream true)
      (#(combine empty-flare %))
      (cheshire/generate-stream out-file {:pretty true})))
  ([^String base-file-name ^Reader in-file ^Writer out-file]
   (let [base-flare (cheshire/parse-string (slurp base-file-name) true)]
     (->
       in-file
       (cheshire/parse-stream true)
       (#(combine base-flare %))
       (cheshire/generate-stream out-file {:pretty true})))))