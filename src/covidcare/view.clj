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
     (if error [:e1 error])
     [:form {:method "post" :action ""}
      [:br]
      [:div {:class "logininputlabel"} "Username/Email"]
      [:input {:class "logininput" :type "text" :name "username"}]
      [:br]
      [:div {:class "logininputlabel"} "Password"]
      [:input {:class "logininput" :type "text" :name "password"}]
      [:br][:br]
      [:input {:class "center submitbtn" :type "submit" :value "Submit"}]]
     [:br][:br]
     [:div {:class "menubutton"} [:a {:href "/register"} "Register"]]
     [:br]]]))


(defn register [error params]
  (html
   [:head
    [:title "Add New User"]
    (include-css "style.css")
    [:div {:class "loginpanel"}
     [:h2 "Register"]
     (if error [:e1 error])
     [:form {:method "post" :action "/register"}
      [:br]
      [:div {:class "logininputlabel"} "Username"]
      [:input {:class "logininput" :type "text" :name "username" :value (:username params)}]
      [:br]
      [:div {:class "logininputlabel"} "First Name"]
      [:input {:class "logininput" :type "text" :name "firstname" :value (:firstname params)}]
      [:br]
      [:div {:class "logininputlabel"} "Last Name"]
      [:input {:class "logininput" :type "text" :name "lastname" :value (:lastname params)}]
      [:br]
      [:div {:class "logininputlabel"} "Email"]
      [:input {:class "logininput" :type "text" :name "email" :value (:email params)}]
      [:br]
      [:div {:class "logininputlabel"} "Picture"]
      [:input {:class "logininput" :type "text" :name "picture" :value (or (:picture params) "profile1.png")}]
      [:br]
      [:div {:class "logininputlabel"} "Password"]
      [:input {:class "logininput" :type "text" :name "password" :value (:password params)}]
      [:br]
      [:input {:class "center submitbtn" :type "submit" :value "Submit"}]
      [:br][:br]
      [:div {:class "ibutton center"} [:a {:href "/"} "Back To Login"]]]
     [:br][:br]]]))


(defn menuview [session]
  (let [admin? (is-admin? session)]
    [:div {:class "menubar"} 
     [:div {:class "menubutton col5"} [:a {:href "/schedules"} "Schedules"]]
     (if-not admin? [:div {:class "menubutton col6"} [:a {:href "/offers"} "Your Offers"]])
     (if-not admin? [:div {:class "menubutton col5"} [:a {:href "/requests"} "Your Requests"]])
     (if admin? [:div {:class "menubutton col6"} [:a {:href "/admin"} "Users"]])
     [:div {:class "menubutton col7"} [:a {:href "/logout"} "Logout"]]
     [:div {:class "namebutton"} (str "Logged in as : " (:firstname (:identity session)) (:lastname (:identity session)))]]))
    

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
  (let [user (get-in schedule [:helper :lastname])
        helpee (get-in schedule [:helpee :lastname])
        username (str (get-in schedule [:helper :firstname]) " " user)
        helpeename (str (get-in schedule [:helpee :firstname]) " " helpee)
        userurl (str "images/" (get-in schedule [:helper :picture]))
        helpeeurl (str "images/" (get-in schedule [:helpee :picture]))
        from (.format (java.text.SimpleDateFormat. "MM/dd/yyyy") (:fromdate schedule))
        to  (.format (java.text.SimpleDateFormat. "MM/dd/yyyy") (:todate schedule))
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
     [:div {:class (if (even? index) "cell col1" "cell col3")}
      [:div (if user [:img {:class "avatar" :src userurl}])]
      [:div username]]
     [:div {:class (if (even? index) "cell col2" "cell col4")}
      (if helpee [:img {:class "avatar" :src helpeeurl}])
      helpeename]
     [:div {:class (if (even? index) "cell col1" "cell col3")} service]
     [:div {:class (if (even? index) "cell col1" "cell col3")} city]
     [:div {:class (if (even? index) "cell col2" "cell col4")} district]
     (if showbutton
       [:div {:class (if (even? index) "itembutton boldtext col1" "itembutton boldtext col6")
              :onclick (str "getConfirmation(\"" popup "\",\"" url "\");")
              } label]
       [:div {:class (if (even? index) "itembutton col1" "itembutton col6")}])]))


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
       [:p {:class "menubutton"} "Active Offers"]
       (scheduleheader)
       [:div
        (map (fn [[index schedule]] (scheduleitem index schedule true)) (map-indexed vector offers))]
       [:p {:class "menubutton"} "Active Requests"]
       (scheduleheader)
       [:div
        (map (fn [[index schedule]] (scheduleitem index schedule true)) (map-indexed vector requests))]
       [:p {:class "menubutton"} "Already Reserved"]
       (scheduleheader)
       [:divx
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
       [:br]
       [:div [:a {:class "menubutton col1" :href "/addoffer"} "Add Offer"]]
       [:p {:class "menubutton"} "Active Offers"]
       (scheduleheader)
       [:div
        (map (fn [[index schedule]] (scheduleitem index schedule false)) (map-indexed vector offers))]
       [:p {:class "menubutton"} "Already Reserved"]
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
       [:br]
       [:div [:a {:class "menubutton col1" :href "/addrequest"} "Add Request"]]
       [:p {:class "menubutton"} "Active Requests"]
       (scheduleheader)
       [:div
        (map (fn [[index schedule]] (scheduleitem index schedule false)) (map-indexed vector requests))]
       [:p {:class "menubutton"} "Already Reserved"]
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
   [:div {:class "itembutton col4"} "Action"]
   [:div {:class "itembutton col3"} "Action"]])


(defn useritem [index {:keys [userid firstname lastname username email role picture]}]
  [:div {:class "scheduleitem"}
   [:div {:class (if (even? index) "cell col1" "cell col3")}
    (if picture [:img {:class "avatar" :src (str "images/" picture)}])
    firstname]
   [:div {:class (if (even? index) "cell col2" "cell col4")} lastname]
   [:div {:class (if (even? index) "cell col1" "cell col3")} username]
   [:div {:class (if (even? index) "cell col2" "cell col4")} email]
   [:div {:class (if (even? index) "cell col1" "cell col3")} role]
   [:a {:class (if (even? index) "itembutton col2" "itembutton col4") :href (str "/edituser?userid=" userid)} "Edit"]
   [:div {:class (if (even? index) "itembutton col2" "itembutton col4")
          :onclick (str "getConfirmation(\"Do you really want to remove user?\",\"removeuser?userid=" userid "\");")}
    "Delete"]])


(defn admin [session]
  (let [users (db/get-all-users)]
  (html
   [:head
    [:title "Covid Care Admin"]
    (include-css "style.css")
    (include-js "popup.js")
    [:div {:class "mainpanel"}
     (menuview session)
     [:br]
     [:div [:a {:class "menubutton col1" :href "/adduser"} "Add User"]]
     [:br]
     (userheader)
     [:div
      (map (fn [[index user]] (useritem index user)) (map-indexed vector users))]
     ]])))


(defn adduser [error params]
  (html
   [:head
    [:title "Add/Edit User"]
    (include-css "style.css")
    [:div {:class "loginpanel"}
     [:h2 "Add/Edit User"]
     (if error [:e1 error])
     [:form {:method "post" :action (if params "/updateuser" "/adduser")}
      (if params [:input {:type "hidden" :name "userid" :value (:userid params)}])
      [:br]
      [:div {:class "logininputlabel"} "Username"]
      [:input {:class "logininput" :type "text" :name "username" :value (:username params)}]
      [:br]
      [:div {:class "logininputlabel"} "First Name"]
      [:input {:class "logininput" :type "text" :name "firstname" :value (:firstname params)}]
      [:br]
      [:div {:class "logininputlabel"} "Last Name"]
      [:input {:class "logininput" :type "text" :name "lastname" :value (:lastname params)}]
      [:br]
      [:div {:class "logininputlabel"} "Email"]
      [:input {:class "logininput" :type "text" :name "email" :value (:email params)}]
      [:br]
      [:div {:class "logininputlabel"} "Role"]
      [:input {:class "logininput" :type "text" :name "role" :value (:role params)}]
      [:br]
      [:div {:class "logininputlabel"} "Picture"]
      [:input {:class "logininput" :type "text" :name "picture" :value (or (:picture params) "profile1.png")}]
      [:br]
      [:div {:class "logininputlabel"} "Password"]
      [:input {:class "logininput" :type "text" :name "password" :value (:password params)}]
      [:br]
      [:input {:class "center" :type "submit" :value "Submit"}]
      [:br][:br]
      [:div {:class "ibutton center"} [:a {:href "/admin"} "Back to Users"]]]
     [:br][:br]]]))


(defn addoffer [error params]
  (html
   [:head
    [:title "Add New Offer"]
    (include-css "style.css")
    [:div {:class "loginpanel"}
     [:h2 "Add Offer"]
     (if error [:e1 error])
     [:form {:method "post" :action "/addoffer"}
      [:br]
      [:div {:class "logininputlabel"} "From Date/Time"]
      [:input {:class "logininput" :type "text" :name "fromdate" :value (or (:fromdate params) "2020-05-01")}]
      [:br]
      [:div {:class "logininputlabel"} "Until Date/Time"]
      [:input {:class "logininput" :type "text" :name "todate" :value (or (:todate params) "2020-05-01")}]
      [:br]
      [:div {:class "logininputlabel"} "City"]
      [:input {:class "logininput" :type "text" :name "city" :value (:city params)}]
      [:br]
      [:div {:class "logininputlabel"} "District"]
      [:input {:class "logininput" :type "text" :name "district" :value (:district params)}]
      [:br]
      [:select {:class "center" :id "service" :name "service" :value (or (:service params) "Groceries")}
       [:option {:value "Groceries"} "Groceries"]
       [:option {:value "Walk" } "Walk"]
       [:option {:value "Walking the dog"} "Walking the dog"]
       [:option {:value "Cleaning"} "Cleaning"]
       [:option {:value "Cut grass"} "Cut grass"]]
      [:br]
      [:br]
      [:input {:class "center" :type "submit" :value "Submit"}]
      [:br][:br]
      [:div {:class "ibutton center"} [:a {:href "/offers"} "Cancel"]]]
     [:br][:br]
     ]]))


(defn addrequest [error params]
  (html
   [:head
    [:title "Add New Request"]
    (include-css "style.css")
    [:div {:class "loginpanel"}
     [:h2 "Add Request"]
     (if error [:e1 error])
     [:form {:method "post" :action "/addrequest"}
      [:br]
      [:div {:class "logininputlabel"} "From Date/Time"]
      [:input {:class "logininput" :type "text" :name "fromdate" :value (or (:fromdate params) "2020-05-01")}]
      [:br]
      [:div {:class "logininputlabel"} "Until Date/Time"]
      [:input {:class "logininput" :type "text" :name "todate" :value (or (:todate params) "2020-05-01")}]
      [:br]
      [:div {:class "logininputlabel"} "City"]
      [:input {:class "logininput" :type "text" :name "city" :value (:city params)}]
      [:br]
      [:div {:class "logininputlabel"} "District"]
      [:input {:class "logininput" :type "text" :name "district" :value (:district params)}]
      [:br]
      [:select {:class "center" :id "service" :name "service" :value (or (:service params) "Groceries")}
       [:option {:value "Groceries"} "Groceries"]
       [:option {:value "Walk" } "Walk"]
       [:option {:value "Walking the dog"} "Walking the dog"]
       [:option {:value "Cleaning"} "Cleaning"]
       [:option {:value "Cut grass"} "Cut grass"]]
      [:br]
      [:br]
      [:input {:class "center" :type "submit" :value "Submit"}]
      [:br][:br]
      [:div {:class "ibutton center"} [:a {:href "/requests"} "Cancel"]]]
     [:br][:br]
     ]]))
