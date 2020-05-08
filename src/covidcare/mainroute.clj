(ns covidcare.mainroute
  (:require
    [compojure.core :refer :all]
    [covidcare.database :as db]
    [covidcare.view :as v]
    [ring.util.response :refer [redirect]]))


(defn reserve [session request]
  (let [scheduleid (get-in request [:params :scheduleid])
        userid (get-in session [:identity :userid])]
    (db/update-schedule {:scheduleid scheduleid :helpeeid userid})
    (v/schedules session)))


(defn applyfor [session request]
  (let [scheduleid (get-in request [:params :scheduleid])
        userid (get-in session [:identity :userid])]
    (db/update-schedule {:scheduleid scheduleid :userid userid})
    (v/schedules session)))


(defn addoffer [session request]
  (let [userid (get-in session [:identity :userid])]
    (db/add-schedule (assoc (:params request) :userid userid))
    (v/offers session)))


(defn addrequest [session request]
  (let [userid (get-in session [:identity :userid])]
    (db/add-schedule (assoc (:params request) :helpeeid userid))
    (v/requests session)))


(defroutes main-routes
  (GET "/schedules" {session :session} (v/schedules session))
  (GET "/offers" {session :session} (v/offers session))
  (GET "/requests" {session :session} (v/requests session))
  (GET "/logout" request (assoc (redirect "/") :session {}))
  (GET "/addoffer"  {session :session} (v/addoffer session))
  (GET "/addrequest"  {session :session} (v/addrequest session))
  ;; api calls
  (GET "/reserve"  {session :session :as request} (reserve session request))
  (GET "/apply"  {session :session :as request} (applyfor session request))
  (POST "/addoffer" {session :session :as request} (addoffer session request))
  (POST "/addrequest" {session :session :as request} (addrequest session request)))
