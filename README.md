# ![RealWorld Example App](logo.png)

> ### Spring boot + WebFlux (Kotlin) + reactive mongodb and r2dbc (H2) codebase containing real world examples (CRUD, auth, advanced patterns, etc) that adheres to the [RealWorld](https://github.com/gothinkster/realworld) spec and API.


This codebase was created to demonstrate a backend of a fully fledged fullstack application built with 
**Spring boot + WebFlux (Kotlin) ** including CRUD operations, authentication, and etc.

This codebase intentionally uses an "unusual" setup of the persistence tier with both reactive mongodb, and r2dbc over 
H2 for reactive relational database. This setup demonstrates that spring webflux can work well with both 
__reactive nosql datastore__, such as mongodb, cassandra, gcp firestore and etc., and __"traditional" relational database__, 
such as h2, mySql, oracle, with the help of r2dbc (reactive to database connector). _And these two datastore technologies
works "peacefully" side by side in a single project module_.  :grin:

An application of such setup can have a delayed decision on the kind of the datastore solution for the project. The data 
service layer makes the transition to either direction "relatively" painless.  

We've gone to great lengths to adhere to the **Spring boot + WebFlux (Kotlin) ** community style guides & best practices.

For more information on how to this works with other frontends/backends, head over to the [RealWorld](https://github.com/gothinkster/realworld) repo.


# How it works
It uses Kotlin 1.7.20, and Spring Reactive Stack: WebFlux + Spring Data Reactive MongoDB + Spring Data R2DBC (H2).  
It provides ability to handle concurrency with a small number of threads and scale with fewer hardware resources, with 
functional development approach.
- [WebFlux](https://docs.spring.io/spring-framework/docs/current/reference/html/web-reactive.html) spring boot 3.5.6 
- [MongoDB Reactive](https://docs.spring.io/spring-data/mongodb/docs/current/reference/html/#mongo.reactive) 
  - embedded mongodb: 4.21.0 for mongodb 6.0.5+ (see application.yml) 
    + NOTE: new linux distribution, eg. ubuntu 22+, requires mongodb 6.0.4+, due to the libssl dependency
  - the datastore solution for 'user' domain 
- [r2dbc-h2](https://spring.io/projects/spring-data-r2dbc) 1.0.0.RELEASE
  - h2: 2.1.210 (see build.gradle.kt)
  - the datastore solution for 'article' domain

This project Uses a hybrid data store solution to demonstrate the localized the data storage, which is a desirable microservice architecture.


## Basic approach
The quality & architecture of this Conduit implementation reflect something similar to an early stage startup's MVP: functionally complete & stable, but not unnecessarily over-engineered.


## Project structure
```
- api - the router and handler
- dto - non-persistence tier data structures
- persistence - includes entities, repositories and support classes
- exceptions - exceptions and exception handlers.
- security - security settings.
- service - contains the business logics (note: spock unit tests).
- validation - custom validators and validation settings.
```

# Getting started

Prerequisite: java 25+ for gradle 9

NOTE: spring 3 requires gradle 8. So it is possible to run the app with java 21 with some changes

* to build
  - `./gradlew clean build` 
* to run 
  - `./gradlew booRun`
  - run in debug mode `./gradlew bootRun --debug-JVM`, and attach the debugger to the spring boot process

When following appears in th log, you can test run the app
```declarative
 Netty started on port 8080 (http)`
```
and view with a browser at http://localhost:8080/api/tags , or with cli `curl http://localhost:8080/api/tags`

For a more comprehensive tour of the app, use "real world" postman collection, [Conduit.postman_collection.json](src/test/resources/Conduit.postman_collection.json), in the test/resources folder, 
or online at [github.com/gothinkster/realworld](https://github.com/gothinkster/realworld/blob/main/api/Conduit.postman_collection.json)
and view the [api document at docs.realworld.show](https://docs.realworld.show/specifications/backend/api-response-format/)

# Tests (troubleshooting)
In case, the app does not work as expected by [Getting Started](#getting-started) due to any kind of reason. 
You can 
1. check with the test for the troubleshooting, if you don't want wait
2. report the issue to the [project](https://github.com/alphawyu/spring-webflux-mongodb-r2dbc/issues). Normally, I check the issues weekly.
   1. please include a description of the issue
   2. the relevant logs (test logs if possible)
   3. your runtime environment, such as OS version, java version
3. [email alphawy@hotmail.com, with subject=subject=spring-web-mongodb-r2dbc](mailto:alphawy@hotmail.com?subject=spring-web-mongodb-r2dbc). I check the email every couple days. 

There are two types of tests,
* Integration tests covers followings,
    - End to End api tests using test harness covers all the happy paths.
    - Repository test on customized repository impl
    - Security
* Unit tests use junit5 + [mockk](https://mockk.io/) for the scenarios that is not easily repeatable,
    - api handlers
    - services
    - security 

This project uses [kover](https://github.com/Kotlin/kotlinx-kover) for the test coverage

Check the logs from the tests as well as the test result 
  
## Run test 

* `./gradlew test`, generate test report but no coverage report
* to test with coverage `./gradlew koverHtmlReport` 

NOTE: kover updates coverage report only when `/gradlew test` can complete successfully

[see QnA.md for more details](QnA.md#test-reports)

# Help

Please fork and PR to improve the project.
For feedbacks, please open an [issue](https://github.com/alphawyu/spring-webflux-mongodb-r2dbc/issues) to the repo, 
or [email alphawy@hotmail.com](mailto:alphawy@hotmail.com?subject=spring-web-mongodb-r2dbc), with subject=subject=spring-web-mongodb-r2dbc

# Credits

Thanks to project [Spring Boot + WebFlux + MongoDB](https://github.com/a-mountain/realworld-spring-webflux-kt).  
This project adopted its code bases for the integration tests.
