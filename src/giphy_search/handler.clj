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

;create atom state holdes array token - results (5)
(def token-store (atom []))
(def current-key (atom 0))

(defn add-to-store 
"
terms will be the search terms used
key will be the next unique id
offset current off set
"
[terms key offset]
  (swap! token-store (conj @token-store {:terms terms :key key :offset offset})))

(defn get-from-giphy-api 
"
Executes the get request to the giphy url.
terms: represents the search terms being sent to giphy.
"
[terms offset]
  (client/get giphy-url {:query-params {"q" terms "api_key" giphy-beta-key "limit" 5 "offset" offset}}))

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
  (try
    (json/read-str str :key-fn keyword)
    (catch java.io.EOFException e 
      nil)))

(defn take-only-5
"
Takes only the first 5 records, if less than 5 return nil.
array: represents an array object.
"
[array]
  (if (> 5 (count array))
    nil
    array))

(defn format-item
"
take a map with :id and :url (atleat) and returns a map with :gif_id and :url
"
[item]
  (hash-map :gif_id (:id item) :url (:url item)))

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
  (hash-map :data (mapv format-item data)) :next key))

(defn generate-key
"create a unique key"
[]
  (swap! current-key inc))


(defn get-giphy-search
"
Pull it all together.
terms: represents the search terms being sent to giphy.
"
[terms key]
  (let [offset (---- terms key)
        newkey (generate-key)
        url (add-to-store terms newkey offset)]
    (->> (get-from-giphy-api terms offset)
        (pull-keyword :body)
        (convert-to-map)
        (pull-keyword :data)
        (take-only-5)
        (output url newkey))))

(defroutes app-routes
  (GET "/search/:terms" [terms] (response (get-giphy-search terms)))
  (GET "/search/:terms/:key" [terms key] (response (get-giphy-search terms key)))
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