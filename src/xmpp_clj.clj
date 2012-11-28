(ns xmpp-clj
  (:require [xmpp-clj.bot :as bot]))

(def bots (atom {}))

(defn stop-bot [& [name]]
  (bot/stop (or name :default)))

(defn start-bot [& opts]
  (let [opts (merge {:name :default}
                    (apply hash-map opts))
        name (:name opts)]
    (swap!
     bots
     (fn [bots]
       (bot/stop (get bots name))
       (assoc bots name (bot/start opts (get opts :handler (fn [m]))))))))

(def restart-bot start-bot)
