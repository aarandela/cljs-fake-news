(ns aarandela.cljs-fake-news.effects
  (:require
   [aarandela.cljs-fake-news.ajax :as ajax]
   [reagent.core :as r]
   [re-frame.core :as rf]))

(rf/reg-fx
 :call-api
 (fn [{:keys [url method success-action error-action]}]
   (ajax/call-api
    url
    method
    #(rf/dispatch (conj success-action %))
    #(rf/dispatch (conj error-action %)))))

(defonce one-second-interval (js/setInterval (fn []
                                               (rf/dispatch [:countdown])
                                               (rf/dispatch [:check-time-left])) 
                                        1000))

(rf/reg-fx
 :interval
 (fn [{:keys [action]}]
   (when (= action :start-countdown) 
     one-second-interval)))

(rf/reg-fx
 :cancel-interval
 (fn [{:keys [action]}]
   (when (= action :cancel-countdown) 
     (js/clearInterval one-second-interval))))
