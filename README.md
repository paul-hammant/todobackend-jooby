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

Watch WebDriver+Chrome do it's thing in about 16 seconds (not first time as 
Maven downloads lots of dependencies).

Note: mvn test runs all tests.

If you were doing this in a CI loop, you'd do this ...

```
mvn test -Punit-tests
mvn test -Pintegration-tests
mvn test -Pfunctional-tests
```

... to get a pipeline effect.

