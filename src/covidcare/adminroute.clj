(ns covidcare.adminroute
  (:require
    [compojure.core :refer :all]
    [covidcare.database :as db]
    [covidcare.view :as v]
    [ring.util.response :refer [redirect]]))


(defn removeuser [session request]
  (let [userid (get-in request [:params :userid])]
    (db/delete-user {:userid userid})
    (v/admin session)))


(defn adduser [session request]
  (db/add-user (:params request))
  (v/admin session))


(defroutes admin-routes
  (GET "/admin"  {session :session} (v/admin session))
  (GET "/adduser"  {session :session} (v/adduser session))
  ;; api calls
  (GET "/removeuser"  {session :session :as request} (removeuser session request))
  (POST "/adduser" {session :session :as request} (adduser session request)))
