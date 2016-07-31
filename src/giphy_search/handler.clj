(ns giphy-search.handler
  (:use ring.util.response)
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
            [ring.middleware.json :as middleware-json]
            [clj-http.client :as client]
            [clojure.data.json :as json]
            [ring.adapter.jetty :refer :all])
  (:gen-class))

(def giphy-url "http://api.giphy.com/v1/gifs/search")
(def giphy-beta-key "dc6zaTOxFJmzC")

(defn get-from-giphy-api 
"
Executes the get request to the giphy url.
terms: represents the search terms being sent to giphy.
"
[terms]
  (client/get giphy-url {:query-params {"q" terms "api_key" giphy-beta-key}}))

(defn pull-keyword
"
Extracts the keyword value from a map.
key: represents a keyword to pull data.
value: represents a map of an object.
"
[key value]
  (if (keyword? key)
    (get value key)
    nil))

(defn convert-to-map
"
Convert the body of the results into a map.
str: represents the string we want to convert to a map
"
[str]
  (json/read-str str :key-fn keyword))

(defn take-only-5
"
Takes only the first 5 records, if less than 5 return nil.
array: represents an array object.
"
[array]
  (if (> 5 (count array))
    nil
    (take 5 array)))

(defn output
"
Pull only the :id & :url from the data, and wrap a :data map around it.
in this format:
{
    data: [
        {
            gif_id: \"FiGiRei2ICzzG\",
            url: \"http://giphy.com/gifs/funny-cat-FiGiRei2ICzzG\",
        }
    ]
}
"
[data]
  (hash-map :data (mapv #(hash-map :gif_id (:id %) :url (:url %)) data)))

(defn get-giphy-search
"
Pull it all together.
terms: represents the search terms being sent to giphy.
"
[terms]
  (->> (get-from-giphy-api terms)
       (pull-keyword :body)
       (convert-to-map)
       (pull-keyword :data)
       (take-only-5)
       (output)))

(defroutes app-routes
  (GET "/search/:terms" [terms] (response (get-giphy-search terms)))
  (route/not-found "Not Found"))

(def app 
  (->
    app-routes
    (middleware-json/wrap-json-body {:keywords? true :bigdecimals? true})
    (middleware-json/wrap-json-response)
    (wrap-defaults site-defaults)))

(defn main
"Pass the handler to Jetty on port 5000" 
[]
  (run-jetty app {:port 5000}))