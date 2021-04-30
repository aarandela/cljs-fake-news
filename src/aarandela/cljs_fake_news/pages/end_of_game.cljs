(ns aarandela.cljs-fake-news.pages.end-of-game
  (:require
   [aarandela.cljs-fake-news.components.past-news :refer [PastNewsContainer]]

   [re-frame.core :as rf]
   [taoensso.timbre :as timbre]))


;; -----------------------------------------------------------------------------
;; Events

;; (rf/reg-event-fx
;;  ::restart-game
;;  (fn [{:keys [db]} [_ _]]
;;    {:db (-> db
;;             (assoc :game-started? false)
;;             (assoc :game-ended? false)
;;             (assoc :game-question-ids []))}))

;; (rf/reg-event-fx
;;  ::fetch-giphy
;;  (fn [{:keys [db]} [_ _]]
;;    {:db (-> db
;;             (assoc :game-started? false)
;;             (assoc :game-ended? false)
;;             (assoc :game-question-ids []))
;;     :fx [[:call-api {:url ""
;;                      :method :get
;;                      :success-action [::fetch-success]
;;                      :error-action [::fetch-failure]}]]}))


;; -----------------------------------------------------------------------------
;; Subscriptions

(rf/reg-sub
 :winner?
 (fn [db]
   (:winner? db)))

;; -----------------------------------------------------------------------------
;; Views


(defn RestartGameButton
  []
  [:div {:style {:padding "1rem"}}
   [:button.button.is-success.is-large {:on-click #((-> js/document
                                                        (. -location)
                                                        (. (reload))))} ;; FIXME: dont refresh
     "Restart Game"]])

(defn EndOfGameContainer []
  (let [winner? @(rf/subscribe [:winner?])]
    [:section.section
     
     [:div.has-text-centered 
      [RestartGameButton]]
     (if winner?
       [:div.title.is-size-1.has-text-centered "🔥🔥🔥 Good Job! 🔥🔥🔥"]
       [:div.title.is-size-1.has-text-centered "😂😂😂 Try again 😂😂😂"])
     [PastNewsContainer]]))
