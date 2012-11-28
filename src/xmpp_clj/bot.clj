(ns xmpp-clj.bot
  (:import [org.jivesoftware.smack
            Chat ChatManager ConnectionConfiguration MessageListener
	    SASLAuthentication XMPPConnection XMPPException PacketListener]
	   [org.jivesoftware.smack.packet
            Message Presence Presence$Type Message$Type]
	   [org.jivesoftware.smack.filter MessageTypeFilter]
	   [org.jivesoftware.smack.util StringUtils]))

(def available-presence (Presence. Presence$Type/available))

(def chat-message-type-filter (MessageTypeFilter. Message$Type/chat))

(defn packet-listener [conn processor]
  (proxy 
      [PacketListener] 
      []
    (processPacket [packet] (processor conn packet))))

(defn error->map [e]
  (if (nil? e) 
    nil
    {:code (.getCode e) :message (.getMessage e)}))

(defn message->map [#^Message m]
  (try
   {:body (.getBody m)
    :subject (.getSubject m)
    :thread (.getThread m)
    :from (.getFrom m)
    :from-name (StringUtils/parseBareAddress (.getFrom m))
    :to (.getTo m)
    :packet-id (.getPacketID m)
    :error (error->map (.getError m))
    :type (keyword (str (.getType m)))}
   (catch Exception e (println e) {})))

(defn parse-address [from]
  (try
   (first (.split from "/"))
   (catch Exception e (println e) from)))

(defn create-reply [from-message-map to-message-body]
  (try
   (let [to (:from from-message-map)
	 rep (Message.)]
     (.setTo rep to)
     (.setBody rep (str to-message-body))
     rep)
   (catch Exception e (println e))))

(defn reply [from-message-map to-message-body conn]
  (.sendPacket conn (create-reply from-message-map to-message-body)))

(defn with-message-map [handler]
  (fn [conn packet]
    (let [message (message->map #^Message packet)]
      (try
       (handler conn message)
       (catch Exception e (println e))))))

(defn wrap-responder [handler]
  (fn [conn message]
    (let [resp (handler message)]
      (reply message resp conn))))

(defn start
  "Defines and starts an instant messaging bot that will respond to incoming
   messages. `start` takes 2 parameters, the first is a map representing 
   the data needed to make a connection to the jabber server:
   
   connnect-info example:
   {:host \"talk.google.com\"
    :domain \"gmail.com\"
    :username \"testclojurebot@gmail.com\"
    :password \"clojurebot12345\"}

   The second parameter expects a single-arg function, which is passed
   a map representing a message on receive. Return a string from this
   function to pass a message back to the sender, or nil for no
   response

   received message map example (nils are possible where n/a):
   {:body
    :subject
    :thread <Id used to correlate several messages, such as a converation>
    :from <entire from id, ex. zachary.kim@gmail.com/Zachary KiE0124793>
    :from-name <Just the 'name' from the 'from', ex. zachary.kim@gmail.com>
    :to <To whom the message was sent, i.e. this bot>
    :packet-id <donno>
    :error <a map representing the error, if present>
    :type <Type of message: normal, chat, group_chat, headline, error.
           see javadoc for org.jivesoftware.smack.packet.Message>}
   "
  [connect-info packet-processor]
  (let [un (:username connect-info)
	pw (:password connect-info)
	host (:host connect-info)
	domain (:domain connect-info)
        port (get connect-info :port 5222)
	connect-config (ConnectionConfiguration. host port domain)
	conn (XMPPConnection. connect-config)]
    (if-not (and un pw host domain)
      (throw (Exception. "Required connection params not provided (:username :password :host :domain)")))
    (.connect conn)
    (try
      (.login conn un pw)
      (catch XMPPException e
        (throw (Exception. (str "Couldn't log in with user's credentials: "
                                un
                                " / "
                                (apply str (take (count pw) (repeat "*"))))))))
    (.sendPacket conn available-presence)
    (.addPacketListener
     conn
     (packet-listener conn (with-message-map (wrap-responder packet-processor)))
     chat-message-type-filter)
    conn))

(defn stop [#^XMPPConnection conn]
  (when conn
    (.disconnect conn)))
