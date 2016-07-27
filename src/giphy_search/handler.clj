(ns giphy-search.handler
  (:use ring.util.response)
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
            [ring.middleware.json :as middleware-json]
            [clj-http.client :as client]
            [clojure.data.json :as json]))

(def giphy-url "http://api.giphy.com/v1/gifs/search")
(def giphy-beta-key "dc6zaTOxFJmzC")

(defn get-from-giphy-api [terms]
  (client/get giphy-url {:query-params {"q" terms "api_key" giphy-beta-key}}))

(defn data-from-result-take-5 [results]
  (take 5 (:data (json/read-str (:body results) :key-fn keyword))))

(defn build-output [data]
  (if (< 5 (count data))
    nil
    (hash-map :data (mapv #(hash-map :gif_id (:id %) :url (:url %)) data))))

(defn get-giphy [terms]
  (build-output
    (data-from-result-take-5
      (get-from-giphy-api terms))))

(defroutes app-routes
  (GET "/search/:terms" [terms] (response (get-giphy terms)))
  (route/not-found "Not Found"))

(def app (->
           app-routes
           (middleware-json/wrap-json-body {:keywords? true :bigdecimals? true})
           (middleware-json/wrap-json-response)
           (wrap-defaults site-defaults)))
