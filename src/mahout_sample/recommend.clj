(ns mahout-sample.recommend
  (:import [java.io File]
           [java.util List]
           [org.apache.mahout.cf.taste.recommender RecommendedItem]
           [org.apache.mahout.cf.taste.impl.model.file FileDataModel]
           [org.apache.mahout.cf.taste.impl.similarity EuclideanDistanceSimilarity PearsonCorrelationSimilarity LogLikelihoodSimilarity TanimotoCoefficientSimilarity]
           [org.apache.mahout.cf.taste.impl.neighborhood NearestNUserNeighborhood ThresholdUserNeighborhood]
           [org.apache.mahout.cf.taste.recommender Recommender ItemBasedRecommender UserBasedRecommender]
           [org.apache.mahout.cf.taste.impl.recommender GenericUserBasedRecommender GenericItemBasedRecommender]
           [org.apache.mahout.cf.taste.eval RecommenderEvaluator RecommenderBuilder]
           [org.apache.mahout.cf.taste.impl.eval AverageAbsoluteDifferenceRecommenderEvaluator RMSRecommenderEvaluator]))

(defprotocol ToClojure
  (to-clojure [x]))

(extend-protocol ToClojure
  List (to-clojure [xs] (map to-clojure xs))
  RecommendedItem (to-clojure [^RecommendedItem x] {:item (.getItemID x)
                                                    :value (.getValue x)}))

(defn file-model
  [path]
  (FileDataModel. (File. path)))

(defn log-likelihood
  [m]
  (LogLikelihoodSimilarity. m))

(defn tanimoto
  [m]
  (TanimotoCoefficientSimilarity. m))

(defn user-recommender
  "Creates a file based user-recommender"
  [model]
  (let [similarity (PearsonCorrelationSimilarity. model)]
    (GenericUserBasedRecommender. model (NearestNUserNeighborhood. 5 similarity model) similarity)))

(defn item-recommender
  ([model]
     (item-recommender model (log-likelihood model)))
  ([model similarity]
     (GenericItemBasedRecommender. model similarity)))

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

(def avg-diff-evaluator (AverageAbsoluteDifferenceRecommenderEvaluator. ))

(def rms-evaluator (RMSRecommenderEvaluator. ))

(defn evaluate
  ([r-fn model]
     (evaluate r-fn model avg-diff-evaluator))
  ([r-fn model e]
     (let [b (proxy [RecommenderBuilder] []
               (buildRecommender [data-model]
                                 (r-fn data-model)))]
       (.evaluate e b nil model 0.7 1.0))))
