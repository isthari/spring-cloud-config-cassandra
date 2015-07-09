/*
 * Copyright 2013-2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.isthari.spring.cloud.config.cassandra;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.cassandra.service.EmbeddedCassandraService;
import org.junit.AfterClass;

import static org.junit.Assert.*;

import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.cloud.config.environment.Environment;
import org.springframework.cloud.config.environment.PropertySource;

import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.utils.UUIDs;

public class CassandraEnvironmentRepositoryTest {
	private static Boolean useEmbeddedCassandra=true;
	private static CassandraEnvironmentRepository repository;
	private static Session session;
	private static PreparedStatement stmtApplicationLabelProfile;
	private static PreparedStatement stmtApplicationSnapshot;
	
	@BeforeClass
	public static void startCassandra() throws Exception {		
		String embedded = System.getProperty("isthari.cassandra.test.embedded");
		useEmbeddedCassandra = embedded==null || "true".equals(useEmbeddedCassandra);
		
		if (useEmbeddedCassandra){
			cleanUp();				
			EmbeddedCassandraService cassandra = new EmbeddedCassandraService();
			cassandra.start();
		}
		
		repository = new CassandraEnvironmentRepository(null, "127.0.0.1", null, null, true);
		
		// LOAD TEST DATASET
		Class<?> clazz = repository.getClass();
		Field sessionField = clazz.getDeclaredField("session");
		sessionField.setAccessible(true);
		session = (Session) sessionField.get(repository);
		
		stmtApplicationLabelProfile = session.prepare("insert into application_label_version (application, label , profile, version  ) VALUES (?,?,?,?)");
		stmtApplicationSnapshot = session.prepare("insert into configuration_snapshot (application, version, parameters) values (?,?,?)");
		
		createSnapshot("application", "master", "", new String [] {"param4"}, new String [] {"value4"});
	}		

	@AfterClass
	public static void cleanUp() throws IOException {
		if (useEmbeddedCassandra){
			CassandraServiceDataCleaner cleaner = new CassandraServiceDataCleaner();
			cleaner.cleanupDataDirectories();
		}
		
	}
	
	private static void createSnapshot(String application, String label, String profile, String [] params, String [] values){		
		UUID uuid = UUIDs.timeBased();
		session.execute(stmtApplicationLabelProfile.bind(application, label, profile, uuid));
		
		Map<Object, Object> map = new HashMap<Object, Object>();
		for(int i=0;i<params.length;i++){
			map.put(params[i], values[i]);
		}
		session.execute(stmtApplicationSnapshot.bind(application, uuid, map));
	}

	@Test
	public void applicationLabelProfile() throws Exception {
		createSnapshot("app1", "master", "devel", new String [] {"param1", "param2"}, new String [] {"value1", "value2"});
		createSnapshot("app1", "master", "", new String [] {"param3"}, new String [] {"value3"});		
	
		Environment environment = repository.findOne("app1", "devel", "master");				
		
		assertEquals("app1", environment.getName());
		assertEquals("master", environment.getLabel());
		assertArrayEquals(new String[] {"devel"}, environment.getProfiles());
		
		List<PropertySource> sources = environment.getPropertySources();
		assertEquals(3, sources.size());				
		
		assertEquals("value1", sources.get(0).getSource().get("param1")); 
		assertEquals("value2", sources.get(0).getSource().get("param2"));
		
		assertEquals("value3", sources.get(1).getSource().get("param3"));
		
		assertEquals("value4", sources.get(2).getSource().get("param4"));
	}
	
	@Test 
	public void applicationLabelNoProfile() throws Exception {		
		createSnapshot("app2", "master", "", new String [] {"param3"}, new String [] {"value3"});		
	
		Environment environment = repository.findOne("app2", "devel", "master");				
		
		assertEquals("app2", environment.getName());
		assertEquals("master", environment.getLabel());
		assertArrayEquals(new String[] {"devel"}, environment.getProfiles());
		
		List<PropertySource> sources = environment.getPropertySources();
		assertEquals(2, sources.size());				
						
		assertEquals("value3", sources.get(0).getSource().get("param3"));		
		assertEquals("value4", sources.get(1).getSource().get("param4"));
	}

}
