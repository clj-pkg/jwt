(ns clj-pkg.jwt
  (:require [clojure.data.json :as json]
            [clojure.string :as string])
  (:import (java.util Base64)
           (java.nio.charset StandardCharsets)
           (javax.crypto Mac)
           (javax.crypto.spec SecretKeySpec)))

(def algorithms {:hs256 "HS256"})
(def signing-methods {"HS256" :hs256})

(defn base64-encode [^bytes bytes]
  (.encodeToString (.withoutPadding (Base64/getUrlEncoder)) bytes))

(defn str->base64 [str-to-encode]
  (base64-encode (.getBytes str-to-encode StandardCharsets/UTF_8)))

(defn base64->str [str-to-decode]
  (String. (.decode (Base64/getUrlDecoder) (.getBytes str-to-decode))))

(defn hs256
  "Function to sign data with HmacSHA256 algorithm."
  [data key]
  (let [secret-key (-> key
                       (.getBytes StandardCharsets/UTF_8)
                       (SecretKeySpec. "HmacSHA256"))

        sha-256-hmac (doto (Mac/getInstance "HmacSHA256")
                       (.init secret-key))]
    (-> sha-256-hmac
        (.doFinal (.getBytes data StandardCharsets/UTF_8))
        (base64-encode))))

; TODO add claims validation
; TODO add expiration date validation

(defrecord JWT [raw signing-method header claims signature])

(defprotocol JsonWebToken
  "Protocol for JsonWebToken"
  (sign [this key] "Sign, and return complete token")
  (verify [this key] "Verify token"))

(extend-protocol JsonWebToken
  JWT
  (sign [this key]
    (let [encoded-header (-> this :header json/write-str str->base64)
          encoded-claims (-> this :claims json/write-str str->base64)
          data (str encoded-header "." encoded-claims)]
      (str data "." (hs256 data key))))
  (verify [this key]
    (let [[encoded-header encoded-claims] (-> this :raw (string/split #"\."))
          data (str encoded-header "." encoded-claims)]
      (= (hs256 data key)
         (:signature this)))))

(defn jwt [signing-method claims]
  (map->JWT {:signing-method signing-method
             :header         {:typ "JWT" :alg (get algorithms signing-method)}
             :claims         claims}))


(defn str->jwt [str]
  (let [[encoded-header encoded-claims signature] (string/split str #"\.")
        header (-> encoded-header base64->str (json/read-str :key-fn keyword))
        claims (-> encoded-claims base64->str (json/read-str :key-fn keyword))]
    (map->JWT {:raw            str
               :signing-method (get signing-methods (:alg header))
               :header         header
               :claims         claims
               :signature      signature})))
