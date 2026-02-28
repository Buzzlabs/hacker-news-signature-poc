(ns buzzlabs.hacker-news-signature
  (:require
    [clojure.data.json :as json]
    [clojure.java.io :as io]
    [hato.client :as hc])
  (:import
    (java.security
      MessageDigest)
    (java.util
      HexFormat)
    (javax.crypto
      Mac)
    (javax.crypto.spec
      SecretKeySpec)))

(defn sign
  "Modern HMAC-SHA256 signature using JDK 17+ HexFormat"
  [message secret]
  (let [algo "HmacSHA256"
        key-spec (SecretKeySpec. (.getBytes secret) algo)
        mac (doto (Mac/getInstance algo)
              (.init key-spec))]
    (-> (HexFormat/of)
      (.formatHex (.doFinal mac (.getBytes message))))))

(defn verify
  "Constant-time verification to prevent timing attacks"
  [secret message signature]
  (let [expected (sign secret message)]
    (MessageDigest/isEqual (.getBytes expected)
      (.getBytes signature))))

(defn sha256-hex
  "Computes the SHA-256 hash of a string and returns it as a hex string."
  [input-string]
  (let [digest (.digest (MessageDigest/getInstance "SHA-256")
                 (.getBytes input-string "UTF-8"))]
    (apply str (map (partial format "%02x") digest))))

(def hacker-news-url "https://hacker-news.firebaseio.com")
(def top-stories "/v0/topstories.json?print=pretty")

(defn story [item-id]
  (str "/v0/item/" item-id ".json?print=pretty"))

(defn hn-get [endpoint]
  (hc/get (str hacker-news-url endpoint)))

(defn -main [& args]
  (let [our-sig (-> "buzzlabs/hacker_news_signature/our_sig" io/resource slurp)
        infra-aberta-sig (first args)
        derived-hash (sha256-hex (str infra-aberta-sig our-sig))]
    (-> top-stories
      hn-get
      :body
      read-string
      first
      story
      hn-get
      :body
      (json/read-str :key-fn keyword)
      :title
      (sign derived-hash)
      prn)))
