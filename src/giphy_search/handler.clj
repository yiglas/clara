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
Execute the get request to the giphy url.

terms : string representing the requested search options.
"
[terms]
  (client/get giphy-url {:query-params {"q" terms "api_key" giphy-beta-key}}))

(defn data-from-result-take-5 
"
From the results take only the first 5 from the :body :data section.

results : is the result of calling the giphy api get method.
"
[results]
  (take 5 (:data (json/read-str (:body results) :key-fn keyword))))

(defn build-output
"
If less than 5 results returned, return nil else format the return to be
in this format:
{
    data: [
        {
            gif_id: \"FiGiRei2ICzzG\",
            url: \"http://giphy.com/gifs/funny-cat-FiGiRei2ICzzG\",
        }
    ]
}

data : represents the array of results returned from the giphy search
" 
[data]
  (if (< 5 (count data))
    nil
    (hash-map :data (mapv #(hash-map :gif_id (:id %) :url (:url %)) data))))

(defn get-giphy 
"
Put it all together, call the giphy api, take what we want from the results, then format the results.

terms : string representing the requested search options.
"
[terms]
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

(defn -main
"Pass the handler to Jetty on port 5000" 
[]
  (run-jetty app {:port 5000}))