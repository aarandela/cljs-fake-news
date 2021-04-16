(ns aarandela.cljs-fake-news.pages.end-of-game
  (:require
   [re-frame.core :as rf]
   [taoensso.timbre :as timbre]))


;; -----------------------------------------------------------------------------
;; Events

(rf/reg-event-fx
 ::restart-game
 (fn [{:keys [db]} [_ _]]
   {:db (-> db
            (assoc :game-started? false)
            (assoc :game-ended? false)
            (assoc :game-question-ids []))}))


;; -----------------------------------------------------------------------------
;; Subscriptions



;; -----------------------------------------------------------------------------
;; Views


(defn RestartGameButton
  []
  [:button.button.is-success {:on-click #(rf/dispatch [::restart-game])}
   "Restart Game"])

(defn EndOfGameContainer []
  [:section.section
    [RestartGameButton]])