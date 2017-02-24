# Demo App showing a quick WebDriver build for one app. 

This builds on the work that Pete Hodgson did for [TodoBackend.com](http://todobackend.com)
and the implementation for for that using the interesting Jooby web-framework for 
Java (see forked from above).

Note: Needs ChromeDriver and Maven (and Java8) installed.

```
git clone git@github.com:paul-hammant/todobackend-jooby.git
cd todobackend-jooby
mvn test
```

Watch WebDriver+Chrome do their thing in about 16 seconds (not first time as 
Maven downloads lots of dependencies).

Note: mvn test runs all tests, but it runs the unit test (and potentially stops there if there is a failure), before the
integration tests (and potentially stops there if there is a failure), before the functional tests. It is crudely 
matching on Unit, Integration and WebDriver in the class names, but it could be more sophisticated.
