(ns aarandela.cljs-fake-news.pages.game
  (:require
   [aarandela.cljs-fake-news.components.modal :refer [Modal]]
   [aarandela.cljs-fake-news.components.multiplayer :refer [MultiplayerContainer]]
   [aarandela.cljs-fake-news.components.past-news :refer [PastNewsContainer]]

   [clojure.string :as string]
   [re-frame.core :as rf]
   [taoensso.timbre :as timbre]))


(defn get-new-news
  [{:keys [all-news all-ids past-ids]}]
  (loop [new-news-id (rand-nth all-ids)]
    (if-not (contains? past-ids new-news-id)
      (get all-news new-news-id)
      (recur (rand-nth all-ids)))))

;; -----------------------------------------------------------------------------
;; Events


(rf/reg-event-db
 :new-news-on-deck
 (fn [db _]
   (let [{:keys [combined-news past-question-ids game-question-ids
                 num-of-questions question-num]} db]
     (when (< question-num num-of-questions)
       (assoc db :news-on-deck (get-new-news {:all-news combined-news
                                              :all-ids game-question-ids
                                              :past-ids past-question-ids}))))))

(rf/reg-event-fx
 :check-game-over
 (fn [{:keys [db]} [_ _]]
   (let [{:keys [player-options num-of-questions question-num]} db]
     (if (or (= (:lives-left player-options) 0) 
             (>= question-num num-of-questions))
       {:db (assoc db :game-ended? true)
        :cancel-interval {:action :cancel-countdown}}
       {:dispatch-n [[:new-news-on-deck]
                     [:reset-timer]]}))))

(rf/reg-event-fx
 ::verify-answer
 (fn [{:keys [db]} [_ answer]]
   (let [news-on-deck (:news-on-deck db)
         subreddit (string/lower-case (:subreddit news-on-deck))
         correct-answer? (= subreddit (string/lower-case answer))
         updated-db (-> db
                        (update :past-news-links conj {:question-num (:question-num db)
                                                       :news-link (:url news-on-deck)
                                                       :thumbnail (:thumbnail news-on-deck)
                                                       :title (:title news-on-deck)
                                                       :correct? correct-answer?})
                        (update :past-question-ids conj (:id news-on-deck))
                        (update :question-num inc))]
     (if correct-answer?
       {:db (-> updated-db
                (update :num-correct inc))
        :dispatch [:check-game-over]} 
       {:db (-> updated-db
                (update-in [:player-options :lives-left] dec))
        :dispatch [:check-game-over]}))))

(rf/reg-event-fx
 :set-and-start-timer
 (fn [{:keys [db]} [_ set-time]]
   {:db (-> db
            (assoc-in [:player-options :time-start] set-time)
            (assoc :time-left set-time))
    :interval {:action :start-countdown}}))

(rf/reg-event-db
 :countdown
 (fn [db [_ set-time]]
   (update db :time-left dec set-time)))

(rf/reg-event-db
 :reset-timer
 (fn [db [_ _]]
   (let [time-start (-> db :player-options :time-start)]
     (assoc db :time-left time-start))))

(rf/reg-event-fx
 :check-time-left
 (fn [{:keys [db]} [_ _]]
    (when (= (:time-left db) 0)
      {:db (-> db
              (update :past-news-links conj {:question-num (:question-num db)
                                             :news-link (-> db :new-news-on-deck :url)
                                             :thumbnail (-> db :news-on-deck :thumbnail)
                                             :title (-> db :news-on-deck :title)
                                             :correct? false})
              (update :question-num inc)
              (update-in [:player-options :lives-left] dec))
       :dispatch [:check-game-over]})))
   
;; -----------------------------------------------------------------------------
;; Subscriptions

(rf/reg-sub
 ::news-title
 (fn [db _]
   (-> db :news-on-deck :title)))

(rf/reg-sub
 ::time-left
 (fn [db _]
   (:time-left db)))

(rf/reg-sub
 ::time-start
 (fn [db _]
   (-> db :player-options :time-start)))

(rf/reg-sub
 :lives-left
 (fn [db _]
   (-> db :player-options :lives-left)))


;; -----------------------------------------------------------------------------
;; Views

(defn NewsTitleContainer []
  (let [title @(rf/subscribe [::news-title])]
    [:div {:style {:max-height "15rem"
                   :min-height "10rem"}} ;; FIXME: put in css
     [:h1.subtitle.is-2.has-text-centered
      title]]))

(def seconds-left 5)

(defn TimeBarContainer []
  (let [time-left @(rf/subscribe [::time-left])
        time-start @(rf/subscribe [::time-start])]
    [:section.section
     [:div "TIME LEFT: " time-left]
     [:progress.progress {:class (if (<= time-left seconds-left)
                                   "is-danger"
                                   "is-primary")
                          :value time-left
                          :max time-start}]]))

(defn GameButtonsContainer []
  [:div.column
   [:div.columns
    [:div.column
     [:button.button.is-warning.is-large {:on-click #(rf/dispatch [::verify-answer "TheOnion"])}
      "ThIs nEwS Is FaKe!"]]
    [:div.column
     [:button.button.is-success.is-large {:on-click #(rf/dispatch [::verify-answer "nottheonion"])}
      "REAL NEWS!"]]]])

(defn LivesContainer []
  (let [lives-left @(rf/subscribe [:lives-left])]
    [:div "LIVES LEFT: " lives-left]))

(defn TheGameContainer []
  [:<>
   [Modal]
   [:section.section
    [NewsTitleContainer]
    [TimeBarContainer]
    [:div.columns.is-centered
     [PastNewsContainer]
     [:div
      [GameButtonsContainer]
      [LivesContainer]]
     [MultiplayerContainer]]]])
