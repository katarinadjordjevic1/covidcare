(ns covidcare.view
  (:require [hiccup.core :refer [html]]
            [hiccup.page :refer [include-css]]
            [covidcare.database :as db]
            ))


(defn login [error]
  (html
   [:head
    [:title "login"]
    (include-css "style.css")
    [:div {:class "loginpanel"}
     [:h1 "Covid Care"]
     [:h1 "Offer Service / Reserve Service"]
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
       [:input {:class "center" :type "submit" :value "submit"}]]]]]))


(defn menuview []
  [:div {:class "menu"}
   [:a {:href "/schedules"} "Schedules / "] 
   [:a {:href "/offers"} "Offers / "]
   [:a {:href "/logout"} "Logout"]
   [:a {:href "/users.html"} "/ All Users (Admin Only)"]])


(defn schedules [req]

  ;; all offers / requests / reservedc
  (let [schedules (db/get-all-schedules)]
    (println "schedules" schedules)
    (html
     [:head
      [:title "main"]
      (include-css "style.css")
      [:div
       (menuview)
       [:p "Available volunteers:"]
       [:div
        (map (fn [schedule] [:p (str schedule "Reserve") ]) schedules)]
       ]])))


(defn offers [req]
  ;; user active offers / reserved offers
  )
