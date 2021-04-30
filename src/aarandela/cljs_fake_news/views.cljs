(ns aarandela.cljs-fake-news.views
  (:require
   ;; Load for call-api effect
   [aarandela.cljs-fake-news.effects]

   [aarandela.cljs-fake-news.components.modal :refer [Modal]]
   [aarandela.cljs-fake-news.pages.start-of-game :refer [StartOfGameContainer]]
   [aarandela.cljs-fake-news.pages.game :refer [TheGameContainer]]
   [aarandela.cljs-fake-news.pages.end-of-game :refer [EndOfGameContainer]]
   [re-frame.core :as rf]
   [taoensso.timbre :as timbre]))

;; -----------------------------------------------------------------------------
;; Events

;; (rf/reg-event-fx
;;  :global-init
;;  (fn [db _]
;;    (assoc db :modal nil
;;              :fake-news {}
;;              :real-news {}
;;              :game-question-ids []
;;              :past-question-ids #{}
;;              :title-question nil
;;              :game-ended? false
;;              :game-started? false
;;              :time-left 999999999
;;              :player-options nil
;;              :num-correct 0
;;              :question-num 1
;;              :past-news-links [])))

(rf/reg-event-fx
 :global-init
 (fn [{:keys [db]} _]
   {:db (assoc db :modal nil
                  :fake-news {}
                  :real-news {}
                  :game-question-ids []
                  :past-question-ids #{}
                  :title-question nil
                  :game-ended? false
                  :game-started? false
                  :time-left 999999999
                  :player-options nil
                  :num-correct 0
                  :question-num 1
                  :past-news-links [])
    :fx [[:call-api {:url "https://www.reddit.com/r/TheOnion+nottheonion/.json?limit=8"
                     :method :get
                     :success-action [::fetch-example-success]
                     :error-action [::fetch-failure]}]]}))

(rf/reg-event-fx
  ::fetch-example-success
  (fn [{:keys [db]} [_ response]]
    {:db (assoc db :example-question (->> (get-in response [:data :children])
                                          (map #(:data %))
                                          rand-nth))
     :fx [[:dispatch [:destroy-fetch-modal]]]}))

;; (rf/reg-event-db
;;   ::fetch-failure
;;   (fn [db [_ response]]
;;     (assoc db :modal {:type "ERROR_MODAL"
;;                       :msg (-> response :response :message)
;;                       :title "Failure loading data"
;;                       :buttons [close-button]})))

;; -----------------------------------------------------------------------------
;; Subscriptions

(rf/reg-sub
  :game-started?
  (fn [db _]
    (:game-started? db)))

(rf/reg-sub
  :game-ended?
  (fn [db _]
    (:game-ended? db)))

(rf/reg-sub
  :example-question
  (fn [db _]
    (:example-question db)))

;; -----------------------------------------------------------------------------
;; Views

(defn App 
  []
  (let [game-started? @(rf/subscribe [:game-started?])
        game-ended? @(rf/subscribe [:game-ended?])]
    [:div
     (cond

       (and (not game-started?) (not game-ended?))
       [:<>
        [Modal]
        [StartOfGameContainer]]
       
       (and game-started? (not game-ended?))
       [TheGameContainer]
       
       (and game-started? game-ended?)
       [EndOfGameContainer]
       
       :else
       [:div
        "idk"])]))
      
     
     
