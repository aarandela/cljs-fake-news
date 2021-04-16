(ns aarandela.cljs-fake-news.ajax
  (:require
   [ajax.core :refer [GET POST PUT DELETE]]))

(defn call-api
  [url method success-fn error-fn]
  (let [method-fn (case method
                    :post POST
                    :put PUT
                    :delete DELETE
                    GET)]
    (method-fn url {:keywords? true
                    :response-format :json
                    :format :json
                    :handler success-fn
                    :error-handler error-fn})))
