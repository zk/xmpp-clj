(ns smack_clj.examples.example1
  (:require [smack-clj :as smack]))

;; Simple jabber bot that responds with the message that was sent

;; Connection Info
(def connect-info {:username "testclojurebot@gmail.com"
	 :password "clojurebot12345"
	 :host "talk.google.com"
	 :domain "gmail.com"
	 })

(defn handle-message [message]
  (let [body (:body message)
	from-user (:from-name message)]
    (str "Hi " from-user ", you sent me " body)
    nil))

(defn reload-helper [message] 
  (try 
   (handle-message message)
   (catch Exception e (println e))))

(defonce x (smack/jabberbot connect-info reload-helper))

(defn reload []
  (.disconnect x)
  (def x (smack/jabberbot connect-info reload-helper)))

(reload)

