(ns aarandela.cljs-fake-news.components.modal
  (:require
   [re-frame.core :as rf]
   [taoensso.timbre :as timbre]))

;; -----------------------------------------------------------------------------
;; Events

(def default-error-msg
  "Internal error. Please refresh the page and try again or contact support for more information.")

(def error-msgs
  {:initial-data "Error fetching initial data. Please refresh the page and try again."})

(def default-error-modal
  {:msg default-error-msg
   :type "ERROR_MODAL"})

(rf/reg-event-db
 :show-error-modal
 (fn [db [_ modal]]
   (assoc db :modal (merge default-error-modal modal))))

(rf/reg-event-db
 :destroy-fetch-modal
 (fn [db _]
   (let [fake-news (:fake-news db)
         real-news (:real-news db)
         both-available? (and (seq fake-news)
                              (seq real-news))] 
     (when both-available? 
       (dissoc db :modal)))))

(rf/reg-event-db
 :destroy-modal
 (fn [db _]
   (dissoc db :modal)))


;; -----------------------------------------------------------------------------
;; Subscriptions

(rf/reg-sub
 :modal
 (fn [db _]
   (:modal db)))

;; -----------------------------------------------------------------------------
;; Views

;; (defn ModalButton [idx {:keys [click-action label]}]
;;   [:button.modal-btn
;;    {:key idx
;;     :on-click (when (vector? click-action)
;;                 (fn []
;;                   (rf/dispatch click-action)))}
;;    label])

(defmulti ModalInternal
  (fn [modal]
    (:type modal)))

(defmethod ModalInternal "GET_READY"
  [{:keys [msg type]}]
  [:<>
   [:div.modal (when type
                 {:class "is-active"})
    [:div.modal-background]
    [:div.modal-content
      msg]]])


(defmethod ModalInternal :default
  [{:keys [type]}]
  (timbre/warn "Unknown modal type for ModalInternal:" type))

(defn Modal []
  (when-let [modal @(rf/subscribe [:modal])]
    [ModalInternal modal]))

