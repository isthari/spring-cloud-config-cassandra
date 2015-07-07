package com.isthari.spring.cloud.config.cassandra;

import java.io.IOException;

import org.apache.cassandra.service.EmbeddedCassandraService;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class CassandraEnvironmentRepositoryTest {

	@BeforeClass
	public static void startCassandra() throws IOException {		
		cleanUp();
		EmbeddedCassandraService cassandra = new EmbeddedCassandraService();
		cassandra.start();
	}

	@AfterClass
	public static void cleanUp() throws IOException {
		CassandraServiceDataCleaner cleaner = new CassandraServiceDataCleaner();
		cleaner.cleanupDataDirectories();
	}

	@Test
	public void test() throws InterruptedException {		
	}

}
