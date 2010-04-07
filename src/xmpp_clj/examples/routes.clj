(ns xmpp-clj.examples.routes)

(defn route-message [message & routes]
  (first (filter #(if ((:pred %) message) true false) routes)))

(defmacro defresponse [name pred responder]
  `(def ~name {:pred ~pred :responder ~responder}))

(defresponse basic 
  (fn [message] (.contains message "basic"))
  (fn [message] "Hello Basic!!"))

(defresponse other
  #(.contains % "other")
  (fn [m] "Hello World"))

(defresponse advanced
  (fn [message] (.contains message "advanced"))
  (fn [message] "Hi Advanced."))

(let [msg "other"]
  (run-route (route-message msg basic advanced other) msg))


(defn run-route [route message]
  (when (not (nil? route))
    ((:responder route) message)))

