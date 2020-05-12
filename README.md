# Covid Care Site For Offering Service / Requesting Service For Elders

## Prerequisites

Clojure, Leiningen, MySQL

## Setup

In MySQL create a database called *covidcare*

```
CREATE DATABASE covidcare DEFAULT CHARACTER SET utf8 DEFAULT COLLATE utf8_general_ci;
```

Change user/pass in db-config.edn and in migratus.conf.edn

Create & Fill up database with

```
lein migratus migrate
```

## Startup

```
lein ring server
```

## Used Libraries

- Leiningen: Leiningen is a build automation and dependency management tool for the simple configuration of software projects written in the Clojure programming language
- Ring:  It's a lower-level framework to handle HTTP requests, with a focus on traditional web development. 
- Migratus: A general migration framework, with implementations for migrations as SQL scripts or general Clojure code.
- Compojure: Compojure is a small routing library for Ring that allows web applications to be composed of small, independent parts.
- Korma: Korma is a domain specific language for Clojure. It is used for creating queries and communicating with db.
- Hiccup: Library for representing HTML in Clojure 
- Buddy auth: It is used to provide Authentication and Authorization facilities.

## Project description

Covid Care is a website where users can offer services and accept services.
There are two types of users : admins and simple users.
Admins can add new users and delete existing users.
Simple users can create new offers and new requests and can reserve services, apply for requests, create new offers and create new requests.
For simple users the available menu items are : Schedules/Your Offers/Your Requests/Logout
For amdins available menu items are : Schedules/Users/Logout