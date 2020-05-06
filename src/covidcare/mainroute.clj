(ns covidcare.mainroute
  (:require
    [compojure.core :refer :all]
    [covidcare.database :as db]
    [covidcare.view :as v]
    [ring.util.response :refer [redirect]]
    [buddy.auth :refer [authenticated?]]))


(defroutes main-routes
  (GET "/schedules" request (v/schedules nil))
  (GET "/offers" request (v/offers nil))
  (GET "/logout" request (assoc (redirect "/") :session {})))
