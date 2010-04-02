(ns smack_clj.examples.example1
  (:require [smack-clj :as smack]))

;; Simple jabber bot that responds with the message that was sent

;; Connection Info
(def ci {:username "testclojurebot@gmail.com"
	 :password "clojurebot12345"
	 :host "talk.google.com"
	 :domain "gmail.com"
	 })

(defn handle-message [from-message]
  (let [body (:body from-message)
	from-user (:from from-message)]
    (str "Hello " body " " from-user)))

(defn reload-helper [message] 
  (try 
   (handle-message conn message)
   (catch Exception e (println e))))

(defonce x (smack/jabberbot ci reload-helper))

(defn reload []
  (.disconnect x)
  (def x (smack/jabberbot ci reload-helper)))

(reload)

