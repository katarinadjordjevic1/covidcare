(ns covidcare.rootroute
  (:require
    [compojure.core :refer :all]
    [covidcare.database :as db]
    [covidcare.view :as v]
    [ring.util.response :refer [redirect]]
    [buddy.auth :refer [authenticated?]]))


(defn validate-email [email]
  (re-matches #".+\@.+\..+" email))


(defn login-submit [{{user :username pass :password :as params} :params session :session}]
  (if (or (nil? user) (nil? pass))
    (v/login "Invalid username or password")
    (let [dbparams (if (nil? (validate-email user)) {:username user :password pass} {:email user :password pass})
          userdata (first (db/get-user-by-params dbparams))]
      (if (empty? userdata)
        (v/login "Invalud username or password")
        (assoc (redirect "/schedules") :session (assoc session :identity userdata))))))
  

(defroutes root-routes
  (GET "/" {session :session :as request} (if (not (empty? session))
                                                   (v/schedules session) ; if user has session, go to shcedules
                                                   (v/login nil))) ; else request login
  (POST "/" request (login-submit request)))
