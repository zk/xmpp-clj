(ns xmpp-clj
  (:require [xmpp-clj.bot :as bot]))

(def bots (atom {}))

(defn stop-bot [& [name]]
  (bot/stop (or name :default)))

(defn start-bot-with [starter opts]
  (let [opts (merge {:name :default}
                    (apply hash-map opts))
        name (:name opts)]
    (swap!
     bots
     (fn [bots]
       (bot/stop (get bots name))
       (assoc bots name (starter opts (get opts :handler (fn [m]))))))))

(defn start-bot [& opts]
  (start-bot-with bot/start opts))

(defn start-bot-muc [& opts]
  (start-bot-with bot/start-muc opts))



(def restart-bot start-bot)
