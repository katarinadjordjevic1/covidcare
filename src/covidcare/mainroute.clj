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


(defn removeuser [session request]
  (let [userid (get-in request [:params :userid])]
    (db/delete-user {:userid userid})
    (v/admin session)))


(defn adduser [session request]
  (db/add-user (:params request))
  (v/admin session))


(defn addoffer [session request]
  (let [userid (get-in session [:identity :userid])]
    (db/add-schedule (assoc (:params request) :userid userid))
    (v/offers session)))


(defroutes main-routes
  (GET "/schedules" {session :session} (v/schedules session))
  (GET "/offers" {session :session} (v/offers session))
  (GET "/logout" request (assoc (redirect "/") :session {}))
  (GET "/admin"  {session :session} (v/admin session))
  (GET "/adduser"  {session :session} (v/adduser session))
  (GET "/addoffer"  {session :session} (v/addoffer session))
  ;; api calls
  (GET "/reserve"  {session :session :as request} (reserve session request))
  (GET "/apply"  {session :session :as request} (applyfor session request))
  (GET "/removeuser"  {session :session :as request} (removeuser session request))
  (POST "/adduser" {session :session :as request} (adduser session request))
  (POST "/addoffer" {session :session :as request} (addoffer session request))
  )
