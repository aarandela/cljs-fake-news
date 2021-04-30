(ns aarandela.cljs-fake-news.components.multiplayer
  (:require
    [re-frame.core :as rf]
    [taoensso.timbre :as timbre]))


;; -----------------------------------------------------------------------------
;; Events




;; -----------------------------------------------------------------------------
;; Subscriptions

(rf/reg-sub
 :multiplayer
 (fn [db]
   (:multiplayer db)))


;; -----------------------------------------------------------------------------
;; Views

(defn MultiplayerContainer []
  (let [multiplayer @(rf/subscribe [:multiplayer])]
    [:div.column {:style {:border-style  "solid"
                          :border-color "#D3D3D3"
                          :border-width  "0.5px"
                          :padding       "1.25rem"
                          :border-radius "15px"}}
     (when multiplayer 
       "Placeholder")]))

