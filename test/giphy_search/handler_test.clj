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

)
