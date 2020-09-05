(ns clj-pkg.jwt-test
  (:require [clojure.test :refer [deftest is testing]]
            [clj-pkg.jwt :as jwt]))

(def valid-jwt "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJhIjoiMSIsImIiOiIyIn0.y_kE5_QdyqJUGqcx-kaSBbbeqdo75NzJaQ91DkINLmU")

(deftest str->jwt-test
  (is (= (jwt/str->jwt valid-jwt)
         #clj_pkg.jwt.JWT{:claims         {:a "1"
                                           :b "2"}
                          :header         {:alg "HS256"
                                           :typ "JWT"}
                          :raw            "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJhIjoiMSIsImIiOiIyIn0.y_kE5_QdyqJUGqcx-kaSBbbeqdo75NzJaQ91DkINLmU"
                          :signature      "y_kE5_QdyqJUGqcx-kaSBbbeqdo75NzJaQ91DkINLmU"
                          :signing-method :hs256})))

(deftest jwt-sign-test
  (is (= (jwt/sign (jwt/jwt :hs256 {:a "1" :b "2"}) "key")
         valid-jwt)))

(deftest verify-test
  (testing "with valid key"
    (is (true? (jwt/verify (jwt/str->jwt valid-jwt) "key"))))
  (testing "with invalid key"
    (is (false? (jwt/verify (jwt/str->jwt valid-jwt) "key2")))))
