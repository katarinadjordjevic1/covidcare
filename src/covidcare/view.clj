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
    [:title "Covid Care Login"]
    (include-css "style.css")
    [:div {:class "loginpanel"}
     [:h1 "Covid Care"]
     [:h2 "Offer Service / Request Service"]
     (if error [:e1 (str "Error: " error)])
     [:div {:class "center"}
      [:form {:method "post" :action ""}
       [:br]
       [:div {:class "logininputlabel"} "Username/Email"]
       [:input {:class "logininput" :type "text" :name "username"}]
       [:br]
       [:div {:class "logininputlabel"} "Password"]
       [:input {:class "logininput" :type "text" :name "password"}]
       [:br][:br]
       [:input {:class "center" :type "submit" :value "submit"}]]
      [:br][:br]
      [:div {:class "center"} "(user/user or admin/admin)"]
      ]]]))


(defn menuview [session]
  [:div {:class "menubar"}
   [:div {:class "menubutton"} [:a {:href "/schedules"} "Schedules"]]
   [:div {:class "menubutton"} [:a {:href "/offers"} "Offers"]]
   [:div {:class "menubutton"} [:a {:href "/logout"} "Logout"]]
   (if (is-admin? session) [:div {:class "menubutton"} [:a {:href "/admin"} "Admin"]])])


(defn scheduleitem [schedule]
  (println "scheduleitem" schedule)
  [:div {:class "scheduleitem"} (str "From : " (:fromdate schedule) " To : " (:todate schedule) " Helper : " (:userid schedule) " Helpee :" (:helpee schedule)) " Reserve"])


(defn schedules [session]
  (let [schedules (db/get-all-schedules)]
    (println "schedules" schedules)
    (html
     [:head
      [:title "Covid Care Schedules"]
      (include-css "style.css")
      [:div {:class "mainpanel"}
       (menuview session)
       [:p "Active Offers"]
       [:div
        (map (fn [schedule] (scheduleitem schedule)) schedules)]
       [:p "Active Requests"]
       [:div
        (map (fn [schedule] (scheduleitem schedule)) schedules)]
       [:p "Already Reserved"]
       [:div
        (map (fn [schedule] (scheduleitem schedule)) schedules)]
       ]])))


(defn offers [session]
  (let [schedules (db/get-all-schedules)]
    (println "schedules" schedules)
    (html
     [:head
      [:title "Covid Care Offers"]
      (include-css "style.css")
      [:div {:class "mainpanel"}
       (menuview session)
       [:p "Active Offers"]
       [:div
        (map (fn [schedule] (scheduleitem schedule)) schedules)]
       [:p "Active Requests"]
       [:div
        (map (fn [schedule] (scheduleitem schedule)) schedules)]
       [:p "Already Reserved"]
       [:div
        (map (fn [schedule] (scheduleitem schedule)) schedules)]
       ]])))


(defn admin [session]
  (let [users (db/get-all-users)]
  (html
   [:head
    [:title "Covid Care Admin"]
    (include-css "style.css")
    [:div {:class "mainpanel"}
     (menuview session)
     [:p "All users"]
       [:div
        (map (fn [user] [:p (str user " / Delete") ]) users)]
     ]])))
