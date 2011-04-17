(ns mahout-sample.recommend
  (:import [java.io File]
           [java.util List]
           [org.apache.mahout.cf.taste.recommender RecommendedItem]
           [org.apache.mahout.cf.taste.impl.model.file FileDataModel]
           [org.apache.mahout.cf.taste.impl.similarity EuclideanDistanceSimilarity PearsonCorrelationSimilarity LogLikelihoodSimilarity]
           [org.apache.mahout.cf.taste.impl.neighborhood NearestNUserNeighborhood ThresholdUserNeighborhood]
           [org.apache.mahout.cf.taste.recommender Recommender ItemBasedRecommender UserBasedRecommender]
           [org.apache.mahout.cf.taste.impl.recommender GenericUserBasedRecommender GenericItemBasedRecommender]
           [org.apache.mahout.cf.taste.eval RecommenderEvaluator RecommenderBuilder]
           [org.apache.mahout.cf.taste.impl.eval AverageAbsoluteDifferenceRecommenderEvaluator]))

(defprotocol ToClojure
  (to-clojure [x]))

(extend-protocol ToClojure
  List (to-clojure [xs] (map to-clojure xs))
  RecommendedItem (to-clojure [^RecommendedItem x] {:item (.getItemID x)
                                                    :value (.getValue x)}))

(defn file-model
  [path]
  (FileDataModel. (File. path)))

(defn user-recommender
  "Creates a file based user-recommender"
  [model]
  (let [similarity (PearsonCorrelationSimilarity. model)]
    (GenericUserBasedRecommender. model (NearestNUserNeighborhood. 5 similarity model) similarity)))

(defn item-recommender
  [model]
  (GenericItemBasedRecommender. model (LogLikelihoodSimilarity. model)))

(defn similar
  ([^ItemBasedRecommender r i]
     (similar r i 10))
  ([^ItemBasedRecommender r i n]
     (to-clojure (.mostSimilarItems r i n))))

(defn recommend
  "Using recommender r, generates a sequence of n recommended item id's and their value."
  ([^Recommender r u]
     (recommend r u 10))
  ([^Recommender r u n]
     (to-clojure (.recommend r u n))))

(defn estimate-user-preference
  "Using recommender r, estimates user u's preference for item i."
  [^Recommender r u i]
  (.estimatePreference r u i))

(defn evaluate
  [r model]
  (let [b (proxy [RecommenderBuilder] []
            (buildRecommender [data-model]
                              r))
        e (AverageAbsoluteDifferenceRecommenderEvaluator.)]
    (.evaluate e b nil model 0.7 1.0)))
