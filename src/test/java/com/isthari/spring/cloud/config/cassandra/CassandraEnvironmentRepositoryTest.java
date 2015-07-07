package com.isthari.spring.cloud.config.cassandra;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.cassandra.config.CFMetaData;
import org.apache.cassandra.config.KSMetaData;
import org.apache.cassandra.exceptions.ConfigurationException;
import org.apache.cassandra.service.EmbeddedCassandraService;
import org.apache.cassandra.service.MigrationManager;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class CassandraEnvironmentRepositoryTest {

	@BeforeClass
	public static void startCassandra() throws IOException, ConfigurationException {		
		cleanUp();		
		
		EmbeddedCassandraService cassandra = new EmbeddedCassandraService();
		cassandra.start();
		
		// Create keyspace
		String keyspaceName = "cloud_config_test";
		Map<String, String> options = new HashMap<String, String>();
		options.put("replication_factor", "1");		
		KSMetaData ksMetaData = KSMetaData.newKeyspace(keyspaceName, "SimpleStrategy", options, false);
		MigrationManager.announceNewKeyspace(ksMetaData, true);
		
		// Create table
		CFMetaData cfMetadata = CFMetaData.compile("CREATE TABLE foofoo ("
                + "bar text, "
                + "baz text, "
                + "qux text, "
                + "quz text, "
                + "foo text, "
                + "PRIMARY KEY((bar, baz), qux, quz) ) "
                + "WITH COMPACT STORAGE", keyspaceName);
		MigrationManager.announceNewColumnFamily(cfMetadata);				
	}

	@AfterClass
	public static void cleanUp() throws IOException {
		CassandraServiceDataCleaner cleaner = new CassandraServiceDataCleaner();
		cleaner.cleanupDataDirectories();
	}

	@Test
	public void test() throws InterruptedException {		
		while(true){
			Thread.sleep(100000);
		}
	}

}
