(ns aarandela.cljs-fake-news.components.past-news
  (:require
   [re-frame.core :as rf]
   [taoensso.timbre :as timbre]))


;; -----------------------------------------------------------------------------
;; Events




;; -----------------------------------------------------------------------------
;; Subscriptions





;; -----------------------------------------------------------------------------
;; Views

(defn PastNewsContainer []
  [:<> 
   [:div.column
    "Past News"]])
