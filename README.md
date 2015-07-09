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

... or use the existing Cassandra instance
```
mvn -Disthari.cassandra.test.embedded=false instll
```

### Running the demo server
There's a demo server with the project. To run you must have an instance of Cassandra server started and listening on localhost. On startup the application create the needed keyspace and tables

```
cd demo-server
mvn spring-boot:run
```

If you have want to use Cassandra on another host, or need to include credentials please modify the file src/main/resources/application.properties

### Populating tables
To populate the tables with initial configuration to execute the demo

```
cd demo-server
cqlsh -f src/main/resources/populate.cql
```

### Testing the server
curl localhost:8080/app1/devel/master2 | python -m json.tool
curl localhost:8080/app1/devel/master | jq '.'

## License

The module is released under the non-restrictive Apache 2.0 License

## Contributors

The module has been developed by [Isthari](http://www.isthari.net)
