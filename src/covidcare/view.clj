(ns covidcare.view
  (:require [hiccup.core :refer [html]]
            [hiccup.page :refer [include-css include-js]]
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
       [:input {:class "center" :type "submit" :value "Submit"}]]
      [:br][:br]
      [:div {:class "center"} "(user/user or admin/admin)"]
      ]]]))


(defn menuview [session]
  [:div {:class "menubar"}
   [:div {:class "menubutton col5"} [:a {:href "/schedules"} "Schedules"]]
   [:div {:class "menubutton col6"} [:a {:href "/offers"} "Your Offers"]]
   [:div {:class "menubutton col5"} [:a {:href "/requests"} "Your Requests"]]
   [:div {:class "menubutton col6"} [:a {:href "/logout"} "Logout"]]
   (if (is-admin? session) [:div {:class "menubutton col5"} [:a {:href "/admin"} "Admin"]])
   [:div {:class "menubutton"} (str "Logged in as : " (:firstname (:identity session)) (:lastname (:identity session)))]])


(defn scheduleheader []
  [:div {:class "scheduleitem"}
   [:div {:class "cell col3"} "From"]
   [:div {:class "cell col4"} "Until"]
   [:div {:class "cell col3"} "Helper"]
   [:div {:class "cell col4"} "Helpee"]
   [:div {:class "cell col3"} "Service"]
   [:div {:class "cell col4"} "City"]
   [:div {:class "cell col3"} "District"]
   [:div {:class "itembutton col4"} "Action"]])


(defn scheduleitem [index schedule showbutton]
  (let [username (str (get-in schedule [:helper :firstname]) " " (get-in schedule [:helper :lastname]))
        helpeename (str (get-in schedule [:helpee :firstname]) " " (get-in schedule [:helpee :lastname]))
        from (.format (java.text.SimpleDateFormat. "MM/dd/yyyy hh:mm") (:fromdate schedule))
        to  (.format (java.text.SimpleDateFormat. "MM/dd/yyyy hh:mm") (:todate schedule))
        city (:city schedule)
        district (:district schedule)
        service (:service schedule)
        label (cond
                (= 0 (:userid schedule)) "Apply"
                (= 0 (:helpeeid schedule)) "Reserve"
                :else "")
        popup (cond
                (= 0 (:userid schedule)) (str "Do you really want to apply for helping " helpeename " between " from " and " to "?")
                (= 0 (:helpeeid schedule))  (str "Do you really want to reserve " username " between " from " and " to "?")
                :else "")
        url (cond
                (= 0 (:userid schedule)) (str "apply?scheduleid=" (:scheduleid schedule))
                (= 0 (:helpeeid schedule)) (str "reserve?scheduleid=" (:scheduleid schedule))
                :else "")]
    [:div {:class "scheduleitem"}
     [:div {:class (if (even? index) "cell col1" "cell col3")} from]
     [:div {:class (if (even? index) "cell col2" "cell col4")} to]
     [:div {:class (if (even? index) "cell col1" "cell col3")} username]
     [:div {:class (if (even? index) "cell col2" "cell col4")} helpeename]
     [:div {:class (if (even? index) "cell col1" "cell col3")} service]
     [:div {:class (if (even? index) "cell col1" "cell col3")} city]
     [:div {:class (if (even? index) "cell col2" "cell col4")} district]
     (if showbutton
       [:div {:class (if (even? index) "itembutton col1" "itembutton col3")
              :onclick (str "getConfirmation(\"" popup "\",\"" url "\");")
              } label]
       [:div {:class (if (even? index) "itembutton col1" "itembutton col3")}])]))


(defn schedules [session]
  (let [schedules (db/get-all-schedules)
        extended (map (fn [schedule]
                        (let [helper (first (db/get-user-by-params {:userid (:userid schedule)}))
                              helpee (first (db/get-user-by-params {:userid (:helpeeid schedule)}))]
                              (assoc schedule :helper helper :helpee helpee)
                              )) schedules)
        offers (filter #( = 0 (:helpeeid %)) extended)
        requests (filter #( = 0 (:userid %)) extended)
        reserved (filter #(and (< 0 (:userid %)) (< 0 (:helpeeid %))) extended)]
    (html
     [:head
      [:title "Covid Care Schedules"]
      (include-css "style.css")
      (include-js "popup.js")
      [:div {:class "mainpanel"}
       (menuview session)
       [:p "Active Offers"]
       (scheduleheader)
       [:div
        (map (fn [[index schedule]] (scheduleitem index schedule true)) (map-indexed vector offers))]
       [:p "Active Requests"]
       (scheduleheader)
       [:div
        (map (fn [[index schedule]] (scheduleitem index schedule true)) (map-indexed vector requests))]
       [:p "Already Reserved"]
       (scheduleheader)
       [:div
        (map (fn [[index schedule]] (scheduleitem index schedule true)) (map-indexed vector reserved))]
       ]])))


(defn offers [session]
  (let [schedules (db/get-schedules-by-params {:userid (:userid (:identity session))})
        extended (map (fn [schedule]
                        (let [helper (first (db/get-user-by-params {:userid (:userid schedule)}))
                              helpee (first (db/get-user-by-params {:userid (:helpeeid schedule)}))]
                              (assoc schedule :helper helper :helpee helpee)
                              )) schedules)
        offers (filter #( = 0 (:helpeeid %)) extended)
        reserved (filter #(and (< 0 (:userid %)) (< 0 (:helpeeid %))) extended)]        
    (html
     [:head
      [:title "Covid Care Offers"]
      (include-css "style.css")
      [:div {:class "mainpanel"}
       (menuview session)
       [:div {:class "menubutton col1"} [:a {:href "/addoffer"} "Add Offer"]]
       [:p "Active Offers"]
       (scheduleheader)
       [:div
        (map (fn [[index schedule]] (scheduleitem index schedule false)) (map-indexed vector offers))]
       [:p "Already Reserved"]
       (scheduleheader)
       [:div
        (map (fn [[index schedule]] (scheduleitem index schedule false)) (map-indexed vector reserved))]
       ]])))


(defn requests [session]
  (let [schedules (db/get-schedules-by-params {:helpeeid (:userid (:identity session))})
        extended (map (fn [schedule]
                        (let [helper (first (db/get-user-by-params {:userid (:userid schedule)}))
                              helpee (first (db/get-user-by-params {:userid (:helpeeid schedule)}))]
                              (assoc schedule :helper helper :helpee helpee)
                              )) schedules)
        requests (filter #( = 0 (:userid %)) extended)
        reserved (filter #(and (< 0 (:userid %)) (< 0 (:helpeeid %))) extended)]        
    (html
     [:head
      [:title "Covid Care Requests"]
      (include-css "style.css")
      [:div {:class "mainpanel"}
       (menuview session)
       [:div {:class "menubutton col1"} [:a {:href "/addrequest"} "Add Request"]]
       [:p "Active Requests"]
       (scheduleheader)
       [:div
        (map (fn [[index schedule]] (scheduleitem index schedule false)) (map-indexed vector requests))]
       [:p "Already Reserved"]
       (scheduleheader)
       [:div
        (map (fn [[index schedule]] (scheduleitem index schedule false)) (map-indexed vector reserved))]
       ]])))


(defn userheader []
  [:div {:class "scheduleitem"}
   [:div {:class "cell col3"} "Firstname"]
   [:div {:class "cell col4"} "Lastname"]
   [:div {:class "cell col3"} "Username"]
   [:div {:class "cell col4"} "Email"]
   [:div {:class "cell col3"} "Role"]
   [:div {:class "itembutton col4"} "Action"]])


(defn useritem [index {:keys [userid firstname lastname username email role]}]
    [:div {:class "scheduleitem"
           :onclick (str "getConfirmation(\"Do you really want to remove user?\",\"removeuser?userid=" userid "\");")}
     [:div {:class (if (even? index) "cell col1" "cell col3")} firstname]
     [:div {:class (if (even? index) "cell col2" "cell col4")} lastname]
     [:div {:class (if (even? index) "cell col1" "cell col3")} username]
     [:div {:class (if (even? index) "cell col2" "cell col4")} email]
     [:div {:class (if (even? index) "cell col1" "cell col3")} role]
     [:div {:class (if (even? index) "itembutton col2" "itembutton col4")} "Delete"]])


(defn admin [session]
  (let [users (db/get-all-users)]
  (html
   [:head
    [:title "Covid Care Admin"]
    (include-css "style.css")
    (include-js "popup.js")
    [:div {:class "mainpanel"}
     (menuview session)
     [:div {:class "menubutton col1"} [:a {:href "/adduser"} "Add User"]]
     (userheader)
     [:div
      (map (fn [[index user]] (useritem index user)) (map-indexed vector users))]
     ]])))


(defn adduser [session]
  (html
   [:head
    [:title "Add New User"]
    (include-css "style.css")
    [:div {:class "loginpanel"}
     [:div {:class "center"}
      [:form {:method "post" :action "/adduser"}
       [:br]
       [:div {:class "logininputlabel"} "Username"]
       [:input {:class "logininput" :type "text" :name "username"}]
       [:br]
       [:div {:class "logininputlabel"} "First Name"]
       [:input {:class "logininput" :type "text" :name "firstname"}]
       [:br]
       [:div {:class "logininputlabel"} "Last Name"]
       [:input {:class "logininput" :type "text" :name "lastname"}]
       [:br]
       [:div {:class "logininputlabel"} "Email"]
       [:input {:class "logininput" :type "text" :name "email"}]
       [:br]
       [:div {:class "logininputlabel"} "Role"]
       [:input {:class "logininput" :type "text" :name "role"}]
       [:br]
       [:div {:class "logininputlabel"} "Password"]
       [:input {:class "logininput" :type "text" :name "password"}]
       [:br]
       [:input {:class "center" :type "submit" :value "Submit"}]
       [:br][:br]
       [:div {:class "ibutton center"} [:a {:href "/admin"} "Cancel"]]]
      [:br][:br]
      ]]]))


(defn addoffer [session]
  (html
   [:head
    [:title "Add New Offer"]
    (include-css "style.css")
    [:div {:class "loginpanel"}
     [:div {:class "center"}
      [:form {:method "post" :action "/addoffer"}
       [:br]
       [:div {:class "logininputlabel"} "From Date/Time"]
       [:input {:class "logininput" :type "text" :name "fromdate" :value "2020-05-01"}]
       [:br]
       [:div {:class "logininputlabel"} "Until Date/Time"]
       [:input {:class "logininput" :type "text" :name "todate" :value "2020-05-01"}]
       [:br]
       [:div {:class "logininputlabel"} "City"]
       [:input {:class "logininput" :type "text" :name "city"}]
       [:br]
       [:div {:class "logininputlabel"} "District"]
       [:input {:class "logininput" :type "text" :name "district"}]
       [:br]
       [:select {:id "service" :name "service"}
        [:option {:value "Groceries"} "Grocieries"]
        [:option {:value "Walk" :selected "" } "Walk"]
        [:option {:value "Walking the dog"} "Walking the dog"]
        [:option {:value "Cleaning"} "Cleaning"]
        [:option {:value "Cut grass"} "Cut grass"]]
       [:br]
       [:input {:class "center" :type "submit" :value "Submit"}]
       [:br][:br]
       [:div {:class "ibutton center"} [:a {:href "/offers"} "Cancel"]]]
      [:br][:br]
      ]]]))


(defn addrequest [session]
  (html
   [:head
    [:title "Add New Request"]
    (include-css "style.css")
    [:div {:class "loginpanel"}
     [:div {:class "center"}
      [:form {:method "post" :action "/addrequest"}
       [:br]
       [:div {:class "logininputlabel"} "From Date/Time"]
       [:input {:class "logininput" :type "text" :name "fromdate" :value "2020-05-01"}]
       [:br]
       [:div {:class "logininputlabel"} "Until Date/Time"]
       [:input {:class "logininput" :type "text" :name "todate" :value "2020-05-01"}]
       [:br]
       [:div {:class "logininputlabel"} "City"]
       [:input {:class "logininput" :type "text" :name "city"}]
       [:br]
       [:div {:class "logininputlabel"} "District"]
       [:input {:class "logininput" :type "text" :name "district"}]
       [:br]
       [:select {:id "service" :name "service"}
        [:option {:value "Groceries"} "Grocieries"]
        [:option {:value "Walk" :selected "" } "Walk"]
        [:option {:value "Walking the dog"} "Walking the dog"]
        [:option {:value "Cleaning"} "Cleaning"]
        [:option {:value "Cut grass"} "Cut grass"]]
       [:br]
       [:input {:class "center" :type "submit" :value "Submit"}]
       [:br][:br]
       [:div {:class "ibutton center"} [:a {:href "/requests"} "Cancel"]]]
      [:br][:br]
      ]]]))
