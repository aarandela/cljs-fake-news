(ns aarandela.cljs-fake-news.pages.start-of-game
  (:require
   [re-frame.core :as rf]
   [taoensso.timbre :as timbre]))


;; -----------------------------------------------------------------------------
;; Events

(rf/reg-event-fx
 :fetch-all-news
 (fn [{:keys [db]} _]
   {:db (assoc db :modal {:type "GET_READY"
                          :msg "Get Ready! Loading news..."})
    :fetch-onion {:url "https://www.reddit.com/r/TheOnion/.json?limit=100"
                  :method :get
                  :success-action [::fetch-success]
                  :error-action [::fetch-failure]}}))


(rf/reg-event-fx
 ::fetch-success
 (fn [{:keys [db]} [_ response]]
   (let [fake-news (->> (get-in response [:data :children])
                        (map #(:data %))
                        (filter #(:subreddit %))) ;; possible dirty data where subreddit was nil
         fake-news (zipmap (map :id fake-news) fake-news)]
     {:db (assoc db :fake-news fake-news)
      :fetch-not-the-onion {:url "https://www.reddit.com/r/nottheonion/.json?limit=100"
                            :method :get
                            :success-action [::fetch-not-onion-success]
                            :error-action [::fetch-failure]}})))

(rf/reg-event-fx
 ::fetch-not-onion-success
 (fn [{:keys [db]} [_ response]]
   (let [real-news (->> (get-in response [:data :children])
                        (map #(:data %))
                        (filter #(:subreddit %))) 
         real-news (zipmap (map :id real-news) real-news)
         time-start (get-in db [:player-options :time-start])]
     {:db (assoc db :real-news real-news)
      :fx [[:dispatch [:destroy-fetch-modal]]
           [:dispatch [::start-game]]
           [:dispatch [:set-and-start-timer time-start]]]})))

(rf/reg-event-db
 ::start-game
 (fn [db _]
   (let [combined-news (merge (:fake-news db) (:real-news db))
         all-news-ids (keys combined-news)
         news-on-deck (get combined-news (rand-nth all-news-ids))]
     (-> db
         (assoc :game-started? true)
         (assoc :combined-news combined-news)
         (assoc :game-question-ids all-news-ids)
         (assoc :news-on-deck news-on-deck)
         (assoc :num-of-questions (count all-news-ids))))))

(rf/reg-event-db
 ::set-difficulty
 (fn [db [_ difficulty]]
   (case difficulty
     "easy"
     (-> db
         (assoc-in [:player-options :start-lives] 8)
         (assoc-in [:player-options :lives-left] 8)
         (assoc-in [:player-options :time-start] 20)
         (assoc-in [:player-options :goal-to-win] 8))
     "medium"
     (-> db
         (assoc-in [:player-options :start-lives] 5)
         (assoc-in [:player-options :lives-left] 5)
         (assoc-in [:player-options :time-start] 11)
         (assoc-in [:player-options :goal-to-win] 14))
         
     "hard"
     (-> db
         (assoc-in [:player-options :start-lives] 3)
         (assoc-in [:player-options :lives-left] 3)
         (assoc-in [:player-options :time-start] 6)
         (assoc-in [:player-options :goal-to-win] 14))
     "hardcore"
     (-> db
         (assoc-in [:player-options :start-lives] 1)
         (assoc-in [:player-options :lives-left] 1)
         (assoc-in [:player-options :time-start] 4)
         (assoc-in [:player-options :goal-to-win] 14)))))
         

;; -----------------------------------------------------------------------------
;; Subscriptions

(rf/reg-sub
 :player-options
 (fn [db]
   (:player-options db)))

;; -----------------------------------------------------------------------------
;; Views

;; Game modes? easy medium hard // time and lives
;; fetch page gifs? also data for game in the background

(defn GameModes
  []
  [:div
   [:label "Choose Difficulty"]
   [:select {:on-change #(rf/dispatch [::set-difficulty (-> % .-target .-value)])}
    [:option {:value ""} "-- Select a Difficulty --"]
    [:option {:value "easy"} "Easy"]
    [:option {:value "medium"} "medium"]
    [:option {:value "hard"} "hard"]
    [:option {:value "hardcore"} "hardcore"]]])

(defn StartGameButton
  []
  (let [player-options @(rf/subscribe [:player-options])]
    [:button.button.is-info {:disabled (when-not player-options
                                         true)
                             :on-click #(rf/dispatch [:fetch-all-news])}
     "Start Game"]))

(defn StartOfGameContainer []
  [:section.section
   [GameModes]
   [StartGameButton]])
