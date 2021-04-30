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
    :fx [[:fetch-onion {:url "https://www.reddit.com/r/TheOnion/.json?limit=100"
                        :method :get
                        :success-action [::fetch-success]
                        :error-action [::fetch-failure]}]]})) 


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

(def twenty-seconds 20)
(def eleven-seconds 11)
(def six-seconds 6)
(def four-seconds 4)

(def eight-correct-answers 8)
(def fourteen-correct-answers 14)

(rf/reg-event-db
 ::set-difficulty
 (fn [db [_ difficulty]]
   (case difficulty
     "easy"
     (-> db
         (assoc-in [:player-options :start-lives] 8)
         (assoc-in [:player-options :lives-left] 8)
         (assoc-in [:player-options :time-start] twenty-seconds)
         (assoc-in [:player-options :goal-to-win] eight-correct-answers))
     "medium"
     (-> db
         (assoc-in [:player-options :start-lives] 5)
         (assoc-in [:player-options :lives-left] 5)
         (assoc-in [:player-options :time-start] eleven-seconds)
         (assoc-in [:player-options :goal-to-win] fourteen-correct-answers))
         
     "hard"
     (-> db
         (assoc-in [:player-options :start-lives] 3)
         (assoc-in [:player-options :lives-left] 3)
         (assoc-in [:player-options :time-start] six-seconds)
         (assoc-in [:player-options :goal-to-win] fourteen-correct-answers))
     "hardcore"
     (-> db
         (assoc-in [:player-options :start-lives] 1)
         (assoc-in [:player-options :lives-left] 1)
         (assoc-in [:player-options :time-start] four-seconds)
         (assoc-in [:player-options :goal-to-win] fourteen-correct-answers))
     (assoc db :player-options nil))))
         

;; -----------------------------------------------------------------------------
;; Subscriptions

(rf/reg-sub
 :player-options
 (fn [db]
   (:player-options db)))

;; -----------------------------------------------------------------------------
;; Views

(defn StartGameButton
  []
  (let [player-options @(rf/subscribe [:player-options])]
     [:<>
      [:button.button.is-info {:disabled (when-not player-options
                                           true)
                               :on-click #(rf/dispatch [:fetch-all-news])}
        "Start Game"]]))

(defn GameModes
  []
  (let [player-options @(rf/subscribe [:player-options])]
    [:<>
     [:div {:style {:padding "1rem"}}
      [:select.has-text-centered {:on-change #(rf/dispatch [::set-difficulty (-> % .-target .-value)])}
                :style {:padding "rem"}
       [:option {:value ""} "-- Select a Difficulty --"]
       [:option {:value "easy"} "👍 Easy 👍"]
       [:option {:value "medium"} "👌 Medium 👌"]
       [:option {:value "hard"} "🔥 Hard 🔥"]
       [:option {:value "hardcore"} "💯💯 Hardcore 💯💯"]]]
     (when player-options
       [:div {:style {:padding "1rem"}} 
        [:p "You will start with " [:strong (:start-lives player-options) " lives"] "!"]
        [:p "You will only have " [:strong (:time-start player-options) " seconds"] " for each question!"]
        [:p "To win, you must answer " [:strong (:goal-to-win player-options)] " correctly!"]])
     [:div {:style {:padding "1rem"}} 
      [StartGameButton]]]))
  
(defn GameTitle 
  []
  [:h1.title.is-size-1 "Fake News Game"])

(defn GameDescription
  []
  [:div
   [:h1.subtitle.has-text-weight-medium
    "Guessing game to see which news headline is real or fake!"]
   [:h1.subtitle.has-text-weight-medium
    "Are you able to spot the differences? How many can you get correct?"]
   [:h1.subtitle.has-text-weight-medium
    "Select a difficulty below to start playing!"]])

(defn StartOfGameContainer []
  [:section.section
   [GameTitle]
   [GameDescription]
   [GameModes]])
