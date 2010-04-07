(ns xmpp_clj.examples.example1
  (:require [xmpp-clj :as xmpp]))

;; Simple jabber bot that responds with the message that was sent

;; Connection Info
(def connect-info {:username "testclojurebot@gmail.com"
		   :password "clojurebot12345"
		   :host "talk.google.com"
		   :domain "gmail.com"
		   })


;; Important stuff
(defn handle-message [message]
  (let [body (:body message)
	from-user (:from-name message)]
    (str "Hi " from-user ", you sent me '" body "'")))

;; Don't have to reload the bot every change
(defn reload-helper [message] 
  (try 
   (handle-message message)
   (catch Exception e (println e))))

(defonce my-bot (xmpp/start-bot connect-info reload-helper))

(defn reload []
  (xmpp/stop-bot my-bot)
  (def my-bot (xmpp/start-bot connect-info reload-helper)))

(reload)

