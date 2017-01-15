# slick-akka-http
The Slick Akka Http is a very simple json rest api showing one way of using akka http with [slick 3](https://github.com/slick/slick) library for database access.


It supports the following features:

* Generic Data Access layer, using [slick-repo](https://github.com/gonmarques/slick-repo)
* Cake pattern for DI
* Spray-json to parse json
* Tests for routes

Utils: 

* Typesafe config for property management
* Typesafe Scala Logging (LazyLogging)
* Swagger for api documentation

The project was thought to be used as an activator template.

#Running

The database pre-configured is an h2, so you just have to:


        $ sbt run

#Testing

To run all tests:


        $ sbt test

#Using

With curl:

	curl --request POST localhost:8080/supplier -H "Content-type: application/json" --data "{\"name\" : \"sup1\",\"desc\" : \"low prices\"}"

	curl localhost:8080/supplier/1
		
You can also use swagger:

    http://localhost:8080/swagger/index.html

#Credits

To make this template, I just mixed tutorials and templates, so credits for akka and slick guys, swagger-akka-http and slick-repo owners.
