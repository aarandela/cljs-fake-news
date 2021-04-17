(ns aarandela.cljs-fake-news.components.past-news
  (:require
   [re-frame.core :as rf]
   [taoensso.timbre :as timbre]))


;; -----------------------------------------------------------------------------
;; Events




;; -----------------------------------------------------------------------------
;; Subscriptions

(rf/reg-sub
 :past-news-links
 (fn [db _]
   (->> db
       :past-news-links
       reverse)))

;; -----------------------------------------------------------------------------
;; Views

(defn PastNewsRow 
  [idx {:keys [question-num news-link thumbnail title correct?]}]
  [:tr {:key idx}
   [:td question-num]
   [:td [:img {:src thumbnail}]]
   [:td [:a {:href news-link} title]]
   [:td (if correct?
          [:i.fas.fa-check {:style {:color "green"}} "yes"]
          [:i.fas.fa-times {:style {:color "red"}} "No"])]])


(defn PastNewsTable []
  (let [news-links @(rf/subscribe [:past-news-links])]
    [:table.table.is-striped.is-narrow.is-hoverable
     [:tbody
      (map-indexed #(PastNewsRow %1 %2) news-links)]]))

(defn PastNewsContainer []
  [:<> 
   [:div.column
    "Past News"
    [PastNewsTable]]])
