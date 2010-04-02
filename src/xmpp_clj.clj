(ns xmpp-clj
  (:import [org.jivesoftware.smack 
	    Chat 
	    ChatManager 
	    ConnectionConfiguration 
	    MessageListener
	    SASLAuthentication
	    XMPPConnection
	    XMPPException
	    PacketListener]
	   [org.jivesoftware.smack.packet Message Presence Presence$Type Message$Type]
	   [org.jivesoftware.smack.filter MessageTypeFilter]
	   [org.jivesoftware.smack.util StringUtils]))

(defonce *available-presence* (Presence. Presence$Type/available))

(defonce *chat-message-type-filter* (MessageTypeFilter. Message$Type/chat))

(defn packet-listener [conn processor]
     (proxy 
	 [PacketListener] 
	 []
       (processPacket [packet] (processor conn packet))))


(defn mapify-error [e]
  (if (nil? e) 
    nil
    {:code (.getCode e) :message (.getMessage e)}))

(defn mapify-message [#^Message m]
  (try
   {:body (.getBody m)
    :subject (.getSubject m)
    :thread (.getThread m)
    :from (.getFrom m)
    :from-name (StringUtils/parseBareAddress (.getFrom m))
    :to (.getTo m)
    :packet-id (.getPacketID m)
    :error (mapify-error (.getError m))
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
    (let [message (mapify-message #^Message packet)]
      (try
       (handler conn message)
       (catch Exception e (println e))))))

(defn wrap-responder [handler]
  (fn [conn message]
    (try
     (let [resp (handler message)]
       (reply message resp conn)))))

(defn bot
  "Defines an instant messaging bot that will respond to incoming messages. 
   jabberbot takes 2 parameters, the first is a map representing the data 
   needed to make a connection to the jabber server:
   
   connnect-info example:
   {:host \"talk.google.com\"
    :domain \"gmail.com\"
    :username \"testclojurebot@gmail.com\"
    :password \"clojurebot12345\"}

   The second parameter is the function that does the heavy-lifting.  It is
   a function that takes a map representing a received message, and must
   return either a string, which will be sent back to the user as the
   response, or nil, where nothing will be sent back to the user.

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
	connect-config (ConnectionConfiguration. host 5222 domain)
	conn (XMPPConnection. connect-config)]
    (.connect conn)
    (.login conn un pw)
    (.sendPacket conn *available-presence*)
    (.addPacketListener conn (packet-listener conn (with-message-map (wrap-responder packet-processor))) *chat-message-type-filter*)
    conn))



