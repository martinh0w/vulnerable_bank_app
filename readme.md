# Vulnerable Bank App

This is a vulnerable banking app that is built with vulnerabilities to exploit.

## Getting Started

These instructions will get you a copy of the project up and running on your local machine for development and testing purposes. See deployment for notes on how to deploy the project on a live system.

### Prerequisites

* Java SE Development Kit (JDK)
* Apache Maven (Build tool)
* Javascript Node.js
* Node Package Manager (NPM)

### Installing

Clone the repository at:

```
https://syewai@bitbucket.org/runefeather/app-dev-intern.git
```


## Deployment

### Database

Database configurations for springboot is located at:
```
/Bank-App/Bank-App/src/main/resources/application.properties
```
Configure your database's name, username and password. 

The configuration setting `spring.jpa.hibernate.ddl-auto=create` automatically creates all tables in given database name upon compilation.

Subsequently, you might want to change the configuration to `spring.jpa.hibernate.ddl-auto=none` so that data in the database is not wiped and recreated upon compilation. 


### Backend

Navigate to :
```
/Bank-App/Bank-App/script/
```
Run:
```
startup.sh
```
The script file executes the compiled  jar file in the target folder. Recompile with `mvn clean package` in the Bank-App folder before running script if any changes are made, such as database configurations.

Alternatively, you can run the project with `mvn spring-boot:run` in the Bank-App folder.

### Frontend

Configure the domain at:
```
/BankApp_Frontend/public/app/assets/app/js/apiUrl.js
```
Replace the API domain where the API service is hosted, and hosting domain where the webpages are hosted respectively. Default API is set to `localhost:9443` and default web page is set to `localhost:8081`

Navigate to:
```
/BankApp_Frontend/script
```
Run:
```
startup.sh
```
The script file executes the *server.js* file in the BankApp-Frontend folder. It is served by Express JS which is installed via NPM in the nodes_modules folder. 





## Built With
Backend

* Spring Boot Framework
* Hibernate/ Java Persistence API (database)
* Maven Build Tool

Database

* MySQL

Frontend

* Javascript (no frameworks)
* JQuery
* NPM Express JS (to serve webpages)
* Metronic - Responsive Admin Dashboard Template

## Documentation
Documentation of the project is stored in the *documentation* folder.

#### Postman collection:
Contains the full set of API with request parameters.

[![Run in Postman](https://run.pstmn.io/button.svg)](https://app.getpostman.com/run-collection/e81b9391d31af2ea5d0f)


## AWS Server Domains
Jira (Port 8080)
https://jira.skeleton-key.ninja/jira

Confluence (Port 8090)
https://jira.skeleton-key.ninja/confluence

Backend API (Port 9443)
https://jira.skeleton-key.ninja/api

Frontend (Port 8081)
https://jira.skeleton-key.ninja/app
