# smack-clj

Smack-clj allows you to write simple jabber bots in idiomatic clojure by providing a lightweight wrapper around the http://www.igniterealtime.org/projects/smack/ library.

## Usage


    (ns smack_clj.examples.example1
      (:require [smack-clj :as smack]))
  
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
        (str "Hi " from-user ", you sent me " body)))


    ;; Don't have to reload the bot every change
    (defn reload-helper [message] 
      (try 
        (handle-message message)
        (catch Exception e (println e))))

    (defonce x (smack/jabber-bot connect-info reload-helper))

    (defn reload []
      (.disconnect x)
      (def x (smack/jabber-bot connect-info reload-helper)))

    (reload)


See the src/smack_clj/examples directory for more useage examples.

## Installation

FIXME: write

## License

FIXME: write