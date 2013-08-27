# xmpp-clj

xmpp-clj allows you to write simple jabber bots in idiomatic clojure by providing a lightweight wrapper around the [smack](http://www.igniterealtime.org/projects/smack/) library.

## Lein

    [xmpp-clj "0.3.1"]


## Usage

Create a temporary jabber account for your bot.  I've used gmail here, but there are a bunch of free providers
<br />

Create a leiningen project and cd into the project directory

    lein new mybot
    cd ./mybot
<br />

Add xmpp-clj to your deps (project.clj):

    (defproject mybot "0.1.0"
      :description "FIXME: write"
      :dependencies [[xmpp-clj "0.3.1"]])
<br />

Open up src/mybot/core.clj and require the xmpp lib:

    (ns mybot.core
      (:require [xmpp-clj :as xmpp]))
<br />

Define a handler and start the bot. Handlers accept a single parameter
-- the message map -- and should return a string with a message back
to the sender. Return `nil` to omit a response.  Here's a very simple
example:

    ;; This bot always responds with the message 'Ermahgerd!!!'

    (xmpp/start-bot :username "testclojurebot@gmail.com"
                    :password "clojurebot12345"
                    :host "talk.google.com"
                    :domain "gmail.com"
                    :handler (fn [m] "Ermahgerd!!!")

Next, fire up your chat client, add your new bot buddy, and send him /
her a message.  The response should look someting like this:

> me: hello chatbot

> chatbot: Ermahgerd!!!

<br />

    ;; Stop the bot when you're done:

    (xmpp/stop-bot)



    ;; You can use a name to start / stop multiple bots in the same
    ;; process:

    (xmpp/start-bot :name :bot1
                    :username ...)

    (xmpp/start-bot :name :bot2
                    :username ...)

    (xmpp/stop-bot :bot1)
    (xmpp/stop-bot :bot2)



    ;; And names can be any value / object:

    (xmpp/start-bot :name 0
                    :username ...)

    (xmpp/start-bot :name 1
                    :username ...)



See the `src/xmpp_clj/examples` folder for additional examples,
including MUC chat. If you'd like to manually manage connections, see
the `xmpp-clj.bot` namespace.

<br />

## Problems?

Open up an [issue](http://github.com/zkim/xmpp-clj/issues)

## License

[Eclipse Public License v1.0](http://www.eclipse.org/legal/epl-v10.html)
