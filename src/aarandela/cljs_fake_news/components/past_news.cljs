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
   [:td [:a {:href news-link :target "_blank"} title]]
   [:td (if correct?
          [:span.icon [:i.fas.fa-check {:style {:color "green"}}]]
          [:span.icon [:i.fas.fa-times {:style {:color "red"}}]])]])


(defn PastNewsTable []
  (let [news-links @(rf/subscribe [:past-news-links])]
    [:table.table.is-striped.is-narrow.is-hoverable
     [:tbody
      (map-indexed #(PastNewsRow %1 %2) news-links)]]))

(defn PastNewsContainer []
  [:div.column {:style {:border-style  "solid" 
                        :border-color "#D3D3D3"
                        :border-width  "0.5px"
                        :padding       "1.25rem"
                        :border-radius "15px"}}
    [:h1.subtitle.has-text-centered.is-size-2 "Past News"]
    [PastNewsTable]])
