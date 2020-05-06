(ns covidcare.database
  (:require [clojure.java.jdbc :as sql]
            [korma.core :as k]
            [korma.db :refer [defdb mysql]])
  (:import java.sql.DriverManager))

(def db-config(clojure.edn/read-string (slurp "database/migratus-conf.edn")))

(defdb db (mysql db-config))

(k/defentity user
             (k/table :user))

(k/defentity schedule
             (k/table :schedule))

(defn get-all-users[]
  (k/select user))

(defn get-all-schedules[]
  (k/select schedule))

(defn get-user-by-params [params]
  (k/select user (k/where params)))
