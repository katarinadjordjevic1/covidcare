(ns covidcare.view
  (:require [hiccup.core :refer [html]]
            [hiccup.page :refer [include-css]]
            [covidcare.database :as db]
            [buddy.auth :refer [authenticated?]]))


(defn is-admin? [session]
  (and (authenticated? session)
       ( = "admin" (:role (:identity session)))))


(defn login [error]
  (html
   [:head
    [:title "login"]
    (include-css "style.css")
    [:div {:class "loginpanel"}
     [:h1 "Covid Care"]
     [:h1 "Offer Service / Request Service"]
     (if error [:h1 (str "Error: " error)])
     [:div {:class "center"}
      [:form {:method "post" :action ""}
       [:br]
       [:div {:class "logininputlabel"} "Username/Email:"]
       [:input {:class "logininput" :type "text" :name "username"}]
       [:br]
       [:div {:class "logininputlabel"} "Password:"]
       [:input {:class "logininput" :type "text" :name "password"}]
       [:br][:br]
       [:input {:class "center" :type "submit" :value "submit"}]]
      [:h1 "user/user or admin/admin"]
      ]]]))


(defn menuview [session]
  [:div {:class "menu"}
   [:a {:href "/schedules"} "Schedules / "] 
   [:a {:href "/offers"} "Offers / "]
   [:a {:href "/logout"} "Logout"]
   (if (is-admin? session)[:a {:href "/admin"} " / Admin"])])


(defn schedules [session]
  (let [schedules (db/get-all-schedules)]
    (println "schedules" schedules)
    (html
     [:head
      [:title "main"]
      (include-css "style.css")
      [:div
       (menuview session)
       [:p "Active Offers"]
       [:div
        (map (fn [schedule] [:p (str schedule "Reserve") ]) schedules)]
       [:p "Active Requests"]
       [:div
        (map (fn [schedule] [:p (str schedule "Apply") ]) schedules)]
       [:p "Already Reserved"]
       [:div
        (map (fn [schedule] [:p (str schedule) ]) schedules)]
       ]])))


(defn offers [session]
  (let [schedules (db/get-all-schedules)]
    (println "schedules" schedules)
    (html
     [:head
      [:title "main"]
      (include-css "style.css")
      [:div
       (menuview session)
       [:p "Active Offers"]
       [:div
        (map (fn [schedule] [:p (str schedule "Reserve") ]) schedules)]
       [:p "Already Reserved"]
       [:div
        (map (fn [schedule] [:p (str schedule) ]) schedules)]]])))


(defn admin [session]
  (let [users (db/get-all-users)]
  (html
   [:head
    [:title "users"]
    (include-css "style.css")
    [:div
     (menuview session)
     [:p "All users"]
       [:div
        (map (fn [user] [:p (str user " / Delete") ]) users)]
     ]])))
