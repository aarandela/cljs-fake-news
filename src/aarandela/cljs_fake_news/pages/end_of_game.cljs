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


;; -----------------------------------------------------------------------------
;; Subscriptions



;; -----------------------------------------------------------------------------
;; Views


(defn RestartGameButton
  []
  [:button.button.is-success {:on-click #((-> js/document
                                              (. -location)
                                              (. (reload))))} ;; FIXME: dont refresh
   "Restart Game"])

(defn EndOfGameContainer []
  [:section.section
    [RestartGameButton]
    [PastNewsContainer]])