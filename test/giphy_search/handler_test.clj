(ns giphy-search.handler-test
  (:require [clojure.test :refer :all]
            [ring.mock.request :as mock]
            [giphy-search.handler :refer :all]))

(deftest test-app
  (testing "main route"
    (let [response (app (mock/request :get "/"))]
      (is (= (:status response) 404))
      (is (= (:body response) "Not Found"))))

  (testing "not-found route"
    (let [response (app (mock/request :get "/invalid"))]
      (is (= (:status response) 404))))



  (testing "GIVEN: pull-keyword 
            WHEN: value is a map and has key 
            THEN: value of keyword returns"
    (let [value {:a 12 :b 13}
          key :a
          result (pull-keyword key value)]
      (is (= result 12))))

  (testing "GIVEN: pull-keyword 
            WHEN: value is nil 
            THEN: nil returns"
    (let [value nil
          key :c
          result (pull-keyword key value)]
      (is (nil? result))))

  (testing "GIVEN: pull-keyword 
            WHEN: value is a map but doesn't have the key 
            THEN: nil returns"
    (let [value {:a 12 :b 13}
          key :c
          result (pull-keyword key value)]
      (is (nil? result))))

  (testing "GIVEN: pull-keyword 
            WHEN: value is a map but key is not a keyword
            THEN: nil returns"
    (let [value {:a 12 :b 13}
          key "c"
          result (pull-keyword key value)]
      (is (nil? result))))

  (testing "GIVEN: convert-to-map
            WHEN: str is a map in string format
            THEN: a map returns"
    (let [str "{\"a\" 12 \"b\" 13 }"
          result (convert-to-map str)]
      (is (= 12 (:a result)))))

  (testing "GIVEN: convert-to-map
            WHEN: str not a map
            THEN: nil returns"
    (let [str ""
          result (convert-to-map str)]
      (is (nil? result))))

  (testing "GIVEN: take-only-5
            WHEN: array is nil
            THEN: nil returns"
    (let [array nil
          result (take-only-5 array)]
      (is (nil? result))))

  (testing "GIVEN: take-only-5
            WHEN: array is a vector of maps but has less than 5
            THEN: nil returns"
    (let [array [{:a 12 :b 13} {:a 14 :b 15} {:a 16 :b 17}]
          result (take-only-5 array)]
      (is (nil? result))))

  (testing "GIVEN: take-only-5
            WHEN: array is a array of maps but has more than or equal to 5
            THEN: the first 5 maps are returned"
    (let [array [{:a 8 :b 9} {:a 10 :b 11} {:a 12 :b 13} {:a 14 :b 15} {:a 16 :b 17} {:a 18 :b 19}]
          result (take-only-5 array)]
      (is (= 5 (count result)))))

  (testing "GIVEN: format-item
            WHEN: item is a map with :id & :url in it
            THEN: returns a map with :gif_id value from :id and :url value from :url"
    (let [item {:id 12 :url "http://google.com"}
          result (format-item item)]
      (is (= 12 (:gif_id result)))))

  (testing "GIVEN: format-item
            WHEN: item is a map with :url in it but not :id
            THEN: returns a map with :gif_id is nil and :url value from :url"
    (let [item {:url "http://google.com"}
          result (format-item item)]
      (is (nil? (:gif_id result)))))

  (testing "GIVEN: format-item
            WHEN: item that is nil
            THEN: returns a map with :gif_id is nil and :url nil"
    (let [item {:a 12}
          result (format-item item)]
      (is (and 
            (nil? (:gif_id result))
            (nil? (:url result))))))

)
