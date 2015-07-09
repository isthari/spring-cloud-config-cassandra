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

The following command connect to the demo server asking for the configuration of the application 'app1' deployed in the environment 'devel

```
curl localhost:8080/app1/devel
```

TIP: Use one of the the following command to get a more humand friendly output

```
curl localhost:8080/app1/devel | python -m json.tool
curl localhost:8080/app1/devel | jq '.'
```

The output for the above command:
``` json
{
  "propertySources": [
    {
      "source": {
        "param2": "value2",
        "param1": "value1"
      },
      "name": "cassandra-app1-devel"
    },
    {
      "source": {
        "param3": "value3"
      },
      "name": "cassandra-app1"
    },
    {
      "source": {
        "param4": "value4"
      },
      "name": "cassandra-application"
    }
  ],
  "label": "master",
  "profiles": [
    "devel"
  ],
  "name": "app1"
}
```

## License

Copyright (C) 2015 Isthari Ltd.
The module is released under the non-restrictive Apache 2.0 License

## Contributors

The module has been developed by [Isthari](http://www.isthari.net)
