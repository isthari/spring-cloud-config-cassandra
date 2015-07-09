# spring-cloud-config-cassandra

## Introduction
Cassandra data provider for Spring Cloud http://cloud.spring.io/spring-cloud-config/

## Tutorial
After download the code you must compile, package and install. This script start an EmbeddedCassandraService to run the JUnit tests.

```
mvn install
```

If you have already a Cassandra instance in your machine, you must disable test ...

```
mvn -Dmaven.test.skip install
```

... or use the existint Cassandra instance
```
mvn -Disthari.cassandra.test.embedded=false instll
```

### Running the demo server
There's a demo server with the project. To run you must have an instance of Cassandra server started and listening on localhost. On startup the application create the needed keyspace and tables

```
cd demo-server
mvn spring-boot:run
```

### Populating tables
To populate the tables with initial configuration to execute the demo

```
cd demo-server
cqlsh -f src/main/resources/populate.cql
```
