Building
--------
You need Maven 3 (at least 3.0.4) and Java 8 or 9 to build Tobago.

In the main directory you can use

  mvn install

to run the install target on all sub projects. This will
put all necessary artifacts into your local repository.

Demo
----

In the directory tobago-example/tobago-example-demo call:

mvn jetty:run
