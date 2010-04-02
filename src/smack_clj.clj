(ns smack-clj
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
  (println (str "params " from-message-map " " to-message-body))
  (try
   (let [to (:from from-message-map)
	 rep (Message.)]
     (println (str "MESSAGE: " to " " rep))
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

(defn jabberbot [connect-info packet-processor]
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



