(ns xmpp_clj.examples.basic
  (:use [clojure.test])
  (:require [xmpp-clj :as xmpp]))

;; Simple jabber bot that responds back with message text

;; Important stuff
(defn handle-message [message]
  (let [body (:body message)
	from-user (:from-name message)]
    (str "Hi " from-user ", you sent me '" body "'")))

(xmpp/start-bot :name :awesome-bot
                :username "testclojurebot@gmail.com"
                :password "clojurebot12345"
                :host "talk.google.com"
                :domain "gmail.com"
                :handler (var handle-message))

;; With the request / response details abstracted away from you, you
;; can focus on your bot's logic.  Testing is simplified down to
;; sending maps through your handler function and checking the output.

(def test-message {:subject nil
                   :from "zachary.kim@gmail.com/gmail.3167A379"
                   :to "testclojurebot@gmail.com"
                   :thread nil
                   :error nil
                   :packet-id "5A19D18217BBD43_1"
                   :type :chat
                   :from-name "zachary.kim@gmail.com"
                   :body "hello world"})

(defn say-hi-handler [message]
  (str "Hey there, " (:body message) "."))

(deftest test-handle-message
  (is (= (say-hi-handler {:body "zk"})
         "Hey there, zk.")))
