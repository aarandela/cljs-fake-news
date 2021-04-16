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

(rf/reg-fx
 :start-countdown
 (fn [_]
   (js/setInterval (fn []
                     (rf/dispatch [:countdown])
                     (rf/dispatch [:check-time-left]))
                   1000)))
