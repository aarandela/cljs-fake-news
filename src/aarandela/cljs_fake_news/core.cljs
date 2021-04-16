(ns aarandela.cljs-fake-news.core
  (:require
   [reagent.dom :as rdom]
   [re-frame.core :as rf]
   [aarandela.cljs-fake-news.views :as views]
   [aarandela.cljs-fake-news.config :as config]
   [taoensso.timbre :as timbre]))
   
(defn dev-setup []
  (when config/debug?
    (println "dev mode")))

(defn ^:dev/after-load mount-root []
  (rf/clear-subscription-cache!)
  (let [root-el (.getElementById js/document "app")]
    (rdom/unmount-component-at-node root-el)
    (rdom/render [(var views/App)] root-el)))

(defn init []
  (timbre/info "Initializing Fake News")
  (rf/dispatch-sync [:global-init])
  (dev-setup)
  (mount-root))
