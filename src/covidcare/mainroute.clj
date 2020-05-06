(ns covidcare.mainroute
  (:require
    [compojure.core :refer :all]
    [covidcare.database :as db]
    [covidcare.view :as v]
    [ring.util.response :refer [redirect]]))


(defroutes main-routes
  (GET "/schedules" {session :session} (v/schedules session))
  (GET "/offers" {session :session} (v/offers session))
  (GET "/logout" request (assoc (redirect "/") :session {}))
  (GET "/admin"  {session :session} (v/admin session)))
