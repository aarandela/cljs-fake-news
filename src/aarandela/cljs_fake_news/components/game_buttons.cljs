(ns aarandela.cljs-fake-news.components.game-buttons
  (:require
    [re-frame.core :as rf]
    [taoensso.timbre :as timbre]))

(defn GameButtonsContainer [dispatch-kwd]
  
  
     [:button.button.is-warning.is-large {:on-click #(rf/dispatch [dispatch-kwd "TheOnion"])}
      "FAKE FAKE FAKE!"]
     [:button.button.is-success.is-large {:on-click #(rf/dispatch [dispatch-kwd "nottheonion"])}
      "REAL NEWS!"])
