(ns xmpp_clj.examples.muc
  (require [xmpp-clj :as xmpp]))

;; Simple jabber bot that responds to MUC chatroom messages This bot
;; will only responnd to groupchat messages, and it will only respond
;; *with* groupchat messages.
(xmpp/start-bot-muc :username "testbot@test.host"
                    :password "12345"
                    :host "test.host"
                    :domain "host"
                    :nick "testbot"
                    :room "chatroom@test.host"
                    :handler (fn [m]
                               ;; We check that the message was
                               ;; actually addressed to us, so we
                               ;; don't respond to our own messages.
                               ;; That way DoSing the chat server
                               ;; lies.
                               ;;
                               ;; A falsey return value from the
                               ;; handler function means no response
                               ;; is sent.
                               (when (re-find #"^testbot:" (:body m))
                                 (str "I'm sorry "
                                      (:from-nick m)
                                      ", I'm afraid I can't do that."))))