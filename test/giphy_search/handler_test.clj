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



  (testing "pull-keyword where key is keyword and value has the key"
    (let [value {:a 12 :b 13}
          result (pull-keyword :a value)]
      (is (= result 12))))

  (testing "pull-keyword where key is keyword and value doesn't have key"
    (let [value {:a 12 :b 13}
          result (pull-keyword :c value)]
      (is (nil? result))))

  (testing "pull-keyword where key is not keyword"
    (let [value {:a 12 :b 13}
          result (pull-keyword "c" value)]
      (is (nil? result))))

  (testing "convert-to-map convert string to map with keywords"
    (let [str "{\"a\" 12 \"b\" 13 }"
          result (convert-to-map str)]
      (is (= 12 (:a result)))))

  (testing "convert-to-map returns nil if failes to convert to map"
    (let [str ""
          result (convert-to-map str)]
      (is (nil? results))))

  (testing "take-only-5 array has less than 5 returns nill results"
    (let [array [{:a 12 :b 13} {:a 14 :b 15} {:a 16 :b 17}]
          result (take-only-5 array)]
      (is (nil? result))))

  (testing "take-only-5 array has more than 5 retuns the first 5 from the results"
    (let [array [{:a 8 :b 9} {:a 10 :b 11} {:a 12 :b 13} {:a 14 :b 15} {:a 16 :b 17} {:a 18 :b 19}]
          result (take-only-5 array)]
      (is (= 5 (count result)))))

)
