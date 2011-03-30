(ns mahout-sample.recommend
  (:import [java.io File]
           [java.util List]
           [org.apache.mahout.cf.taste.recommender RecommendedItem]
           [org.apache.mahout.cf.taste.impl.model.file FileDataModel]
           [org.apache.mahout.cf.taste.impl.similarity LogLikelihoodSimilarity]
           [org.apache.mahout.cf.taste.impl.neighborhood ThresholdUserNeighborhood]
           [org.apache.mahout.cf.taste.impl.recommender GenericUserBasedRecommender GenericItemBasedRecommender]))

(defprotocol ToClojure
  (to-clojure [x]))

(extend-protocol ToClojure
  List (to-clojure [xs] (map to-clojure xs))
  RecommendedItem (to-clojure [^RecommendedItem x] {:item (.getItemID x)
                                                    :value (.getValue x)}))

(defn user-recommender
  [file]
  (let [model (FileDataModel. file)
        similarity (LogLikelihoodSimilarity. model)]
    (GenericUserBasedRecommender. model (ThresholdUserNeighborhood. 0.5 similarity model) similarity)))

(defn user-recommendations
  ([r u]
     (recommendations r u 10))
  ([r u n]
     (to-clojure (.recommend r u n))))
