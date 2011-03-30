(ns mahout-sample.recommend
  (:import [java.io File]
           [java.util List]
           [org.apache.mahout.cf.taste.recommender RecommendedItem]
           [org.apache.mahout.cf.taste.impl.model.file FileDataModel]
           [org.apache.mahout.cf.taste.impl.similarity LogLikelihoodSimilarity]
           [org.apache.mahout.cf.taste.impl.neighborhood ThresholdUserNeighborhood]
           [org.apache.mahout.cf.taste.recommender UserBasedRecommender]
           [org.apache.mahout.cf.taste.impl.recommender GenericUserBasedRecommender GenericItemBasedRecommender]))

(defprotocol ToClojure
  (to-clojure [x]))

(extend-protocol ToClojure
  List (to-clojure [xs] (map to-clojure xs))
  RecommendedItem (to-clojure [^RecommendedItem x] {:item (.getItemID x)
                                                    :value (.getValue x)}))

(defn user-recommender
  "Creates a file based user-recommender"
  [file]
  (let [model (FileDataModel. file)
        similarity (LogLikelihoodSimilarity. model)]
    (GenericUserBasedRecommender. model (ThresholdUserNeighborhood. 0.5 similarity model) similarity)))

(defn user-recommendations
  "Using recommender r, generates a sequence of n recommended item id's and their value."
  ([^UserBasedRecommender r u]
     (recommendations r u 10))
  ([^UserBasedRecommender r u n]
     (to-clojure (.recommend r u n))))

(defn estimate-user-preference
  "Using recommender r, estimates user u's preference for item i."
  [^UserBasedRecommender r u i]
  (.estimatePreference r u i))
