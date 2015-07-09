# spring-cloud-config-cassandra

## Introduction
Cassandra data provider for Spring Cloud http://cloud.spring.io/spring-cloud-config/

## Tutorial
After download the code you must compile, package and install

```
mvn install
```

### Running the demo server

There's a demo server with the project. To run you must have an instance of Cassandra server started and listening on localhost. On startup the application create the needed keyspaces and tables

```
cd demo-server
mvn spring-boot:run
```


### Populating tables

