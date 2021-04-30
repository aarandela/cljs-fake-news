(ns aarandela.cljs-fake-news.components.example-question
  (:require
    [re-frame.core :as rf]
    [clojure.string :as string]
    [taoensso.timbre :as timbre]))

;; -----------------------------------------------------------------------------
;; Events

(rf/reg-event-db
 :check-example-question
 (fn [db [_ answer]]
   (let [example-question (:example-question db)
         subreddit (string/lower-case (:subreddit example-question))
         correct-answer? (= subreddit (string/lower-case answer))]
     (-> db 
         (assoc :example-question-correct? correct-answer?)
         (assoc :example-question-answered? true)))))

;; -----------------------------------------------------------------------------
;; Subscriptions

(rf/reg-sub
 :example-question
 (fn [db]
   (:example-question db)))

(rf/reg-sub
 :example-question-correct?
 (fn [db]
   (:example-question-correct? db)))

(rf/reg-sub
 :example-question-answered?
 (fn [db]
   (:example-question-answered? db)))

;; -----------------------------------------------------------------------------
;; Views

(defn CorrectAnswerText [question-answered?]
  (let [example-question-correct? @(rf/subscribe [:example-question-correct?])]
    [:div.columns
      [:div.column.has-text-centered
       (when (and question-answered? example-question-correct?)
         [:p.has-text-success "Correct! Now go make me proud grasshopper!"])
       (when (and question-answered? (not example-question-correct?))
         [:p.has-text-danger "Wrong! You get the gist though, go start dissapointing me."])]
      [:div.column]]))

(defn ExampleQuestion [example-question]
  (let [question-answered? @(rf/subscribe [:example-question-answered?])]
    [:<>
     [:h1.subtitle.has-text-weight-medium.is-size-3 "Example Headline:"]
     [:p.subtitle.is-4 (:title example-question)]
     [CorrectAnswerText question-answered?]
     [:div.columns
      [:div.column
       [:button.button.is-warning
        {:on-click #(rf/dispatch [:check-example-question "TheOnion"])
         :disabled question-answered?}
        "Click me if you think this headline is fake!"]]
      [:div.column
        [:button.button.is-success
         {:on-click #(rf/dispatch [:check-example-question "nottheonion"])
          :disabled question-answered?}
         "Click me if you think this is definitely real news!"]]
      [:div.column] ;;hacky for css
      [:div.column]
      [:div.column]
      [:div.column]]]))

(defn LoadingExampleQuestion []
  [:div "Loading example question..."])


(defn ExampleQuestionContainer 
  []
  (let [example-question @(rf/subscribe [:example-question])]
    (if example-question
      [ExampleQuestion example-question]
      [LoadingExampleQuestion])))
