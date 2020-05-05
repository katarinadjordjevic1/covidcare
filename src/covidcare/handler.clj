(ns covidcare.handler
  (:require [compojure.core :refer :all]
            [compojure.route :as route]            
            [buddy.auth.backends.session :refer [session-backend]]
            [buddy.auth.middleware :refer [wrap-authentication wrap-authorization]]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]])
  (:use [clojure.pprint]))


(defroutes app-routes
  (GET "/" [] "Hello World")
  (route/not-found "Not Found"))


(defn req-res-displayer [handler]
  (fn [req]
    (let [res (handler req)]
      (println "\nRequest:")
      (clojure.pprint/pprint req)
      (println "\nResponse:")
      (clojure.pprint/pprint res)
      res)))


(def backend (session-backend))


(def app
  (-> app-routes
      (req-res-displayer)
      (wrap-authentication backend)
      (wrap-authorization backend)
      (wrap-defaults (assoc-in site-defaults [:security :anti-forgery] false))))
