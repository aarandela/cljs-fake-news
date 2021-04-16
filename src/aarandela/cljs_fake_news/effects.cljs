(ns aarandela.cljs-fake-news.effects
  (:require
   [aarandela.cljs-fake-news.ajax :as ajax]
   [reagent.core :as r]
   [re-frame.core :as rf]))

(rf/reg-fx
 :fetch-onion
 (fn [{:keys [url method success-action error-action]}]
   (ajax/call-api
    url
    method
    #(rf/dispatch (conj success-action %))
    #(rf/dispatch (conj error-action %)))))

(rf/reg-fx
 :fetch-not-the-onion
 (fn [{:keys [url method success-action error-action]}]
   (ajax/call-api
    url
    method
    #(rf/dispatch (conj success-action %))
    #(rf/dispatch (conj error-action %)))))

;; (defonce time-left-atom (r/atom 0))

;; (rf/reg-fx
;;  :timer
;;  (fn [start-time]
;;    (reset! time-left-atom start-time)
;;    (when (> @time-left-atom 0) 
;;      (swap! time-left-atom
;;          (fn [the-atom]
;;            (js/setInterval
;;              #(rf/dispatch [:countdown (dec the-atom)])
;;              1000))))))
   

(rf/reg-fx
 :start-countdown
 (fn [_]
   (js/setInterval (fn []
                     (rf/dispatch [:countdown])
                     (rf/dispatch [:check-time-left]))
                   1000)))
