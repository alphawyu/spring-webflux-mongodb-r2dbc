# ![RealWorld Example App](logo.png)

> ### Spring boot + WebFlux (Kotlin) + reactive mongodb and r2dbc (H2) codebase containing real world examples (CRUD, auth, advanced patterns, etc) that adheres to the [RealWorld](https://github.com/gothinkster/realworld) spec and API.


This codebase was created to demonstrate a backend of a fully fledged fullstack application built with **Spring boot + WebFlux (Kotlin) ** including CRUD operations, authentication, and etc.
This code base uses an unusual setup of the persistenance tier with both reactive mongodb, which represents a suite of non rdbms including cassandra, or gcp firestore and etc., 
and H2 a traditional relation database that supports reactive operation with r2dbc (reactive to database connector).
This setup demostrates that relational data base can work side by side with NoSql datastores in a single project. 
This codebase also demostrates a thing data service layer can detach the project from the details, or differences between these two types of tha data store interface
with spring data, and allows a project make a delayed decision on which data store solution.  

We've gone to great lengths to adhere to the **Spring boot + WebFlux (Kotlin) ** community style guides & best practices.

For more information on how to this works with other frontends/backends, head over to the [RealWorld](https://github.com/gothinkster/realworld) repo.


# How it works
It uses Kotlin 1.7.20, and Spring Reactive Stack: WebFlux + Spring Data Reactive MongoDB + Spring Data R2DBC (H2).  
It provides ability to handle concurrency with a small number of threads and scale with fewer hardware resources, with functional developemnt approach.
- [WebFlux](https://docs.spring.io/spring-framework/docs/current/reference/html/web-reactive.html) \
- [MongoDB Reactive](https://docs.spring.io/spring-data/mongodb/docs/current/reference/html/#mongo.reactive) 
  - (embedded) mongodb: 3.5.5
- [r2dbc-h2](https://spring.io/projects/spring-data-r2dbc) 0.9.1
  - h2: 2.1.210


## Database
It uses embedded MongoDB database, and H2 database for demonstration purposes. 


## Basic approach
The quality & architecture of this Conduit implementation reflect something similar to an early stage startup's MVP: functionally complete & stable, but not unnecessarily over-engineered.


## Project structure
```
- api - web layer which contains router function (AppRounter) and handlers (note: spock unit tests).
- dto - non-persistence tier data structures
- persistence - includes entities, repositories and a support classes
- exceptions - exceptions and exception handlers.
- security - security settings.
- service - contains the business logics (note: spock unit tests).
- validation - custom validators and validation settings.
```
## Tests
1. Integration tests covers followings, 
- End to End api tests using test harness covers all the happy paths.
- Repository test on customized respository impl
- Security
2. Unit tests utilize spock framework to mock the scenarios that not easily repeatable by Integration test
- api handlers
- services


# Getting started
You need JAVA 17 installed.
* to build `./gradlew clean build`
* to run `./gradlew booRun`
  * run in debug mode `./gradlew bootRun --debug-JVM`, and attach the debugger to the spring boot process

To test that it works, open a browser tab at http://localhost:8080/api/tags .  
Alternatively, you can run
```
curl http://localhost:8080/api/tags
```

# Run test

The repository contains a lot of test cases to cover both api test and repository test.

```
./gradlew test
```

# Help

Please fork and PR to improve the project.

# Credits

Thanks to project [Spring Boot + WebFlux + MongoDB](https://github.com/a-mountain/realworld-spring-webflux) from which 
this project adopted its code bases of the integration test.

