(ns covidcare.view
  (:require [hiccup.core :refer [html]]
            [hiccup.page :refer [include-css]]))


(defn login [req]
  (html
   [:head
    [:title "login"]
    (include-css "style.css")
    [:div {:class "loginpanel"}
     [:h1 "Covid Care"]
     [:h1 "Provide Service For Elder People / Accept Service From Volunteers"]
     [:div {:class "center"}
      [:form {:method "post" :action "post-login"}
       [:br]
       [:div {:class "logininputlabel"} "Username:"]
       [:input {:class "logininput" :type "text" :name "user"}]
       [:br]
       [:div {:class "logininputlabel"} "Password:"]
       [:input {:class "logininput" :type "text" :name "pass"}]
       [:br][:br]
       [:input {:class "center" :type "submit" :value "submit"}]]]]]))
