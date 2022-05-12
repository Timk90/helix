# Demo Helix
Demo application allowing CRUD operations with embedded H2 DB.
Some users populated by batch operation (Spring batch) during the app start
App secured by JWT authentication/authorization

In order to build the app maven should be used:
```shell
mvn clean install -U
```
Additionally, h2 console can be used with the following url from browser:
```shell
https://localhost:8080/h2-console
```
All the requests for DB data manipulations are collected in the corresponding
postman collection.
Before starting interactions with API one should authenticate using `Get token` request from the 
collection
```shell
POST https://localhost:8080/authenticate
```
```json
{
  "username": "user/admin",
  "password": "password"
}
```
to start the application one should execute:
```
java -jar helix-demo-0.0.1-SNAPSHOT.jar
```
health check endpoint is available as following:
```shell
POST https://localhost:8080/actuator/health
```
