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

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.springframework.cloud.config.environment.Environment;
import org.springframework.cloud.config.environment.PropertySource;
import org.springframework.cloud.config.server.EnvironmentRepository;
import org.springframework.core.env.ConfigurableEnvironment;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;

/**
 * 
 * @author jose.hernandez@isthari.com
 *
 */
public class CassandraEnvironmentRepository implements EnvironmentRepository {
	private static final String GLOBAL_APPLICATION="application";
	
	private Session session;
	
	private PreparedStatement stmtGetVersion;
	private PreparedStatement stmtGetSnapshot;
	
	private ThreadPoolExecutor executor;

	public CassandraEnvironmentRepository(ConfigurableEnvironment environment) {
		Cluster cluster = Cluster.builder().addContactPoint("127.0.0.1").build();
		
		// Create the schema and tables
		try (Session ddlSession = cluster.connect()){			
			ddlSession.execute("CREATE KEYSPACE if NOT EXISTS cloud_config WITH replication = {'class': 'SimpleStrategy', 'replication_factor': 1}");			
			ddlSession.execute("CREATE TYPE if not exists cloud_config.mutation (value text , operation text, user text)");
			
			ddlSession.execute("CREATE TABLE if NOT EXISTS cloud_config.application_label_version "
					+ "(application text, label text, profile text, version timeuuid , "
					+ "primary KEY (application,label, profile, version)) "
					+ "with clustering order by (label asc, profile asc, version desc)");
			
			ddlSession.execute("CREATE table if NOT EXISTS cloud_config.configuration_changes (application text, label text, version timeuuid, base_version timeuuid, changes map<text,frozen<map<text,mutation>>>, primary key (application, label, version)) with clustering order by (label asc, version desc)");
			
			ddlSession.execute("create table if not exists cloud_config.configuration_snapshot "
					+ "(application text, "
					+ "version timeuuid, "
//					+ "profile text, "
					//+ "parameters map<text,frozen<map<text,text>>>, "
					+ "parameters map<text,text>, "
					+ "primary KEY (application,version"
//					+ ",profile"
					+ ")) "
					+ "with clustering order by (version desc)");									
		}
		
		session = cluster.connect("cloud_config");
		this.stmtGetVersion = session.prepare("select version from application_label_version where application=? and label=? and profile=? limit 1");
		this.stmtGetSnapshot = session.prepare("select parameters from configuration_snapshot where application=? and version=?");
	
		BlockingQueue<Runnable> workQueue = new LinkedBlockingQueue<Runnable>(10);
		executor = new ThreadPoolExecutor(4, 10, 1, TimeUnit.DAYS, workQueue);
	}

	@Override
	public String getDefaultLabel() {
		return "master";
	}

	@Override
	public Environment findOne(String application, String profile, String label) {
		Environment environment = new Environment(application, profile);
		
		Future<PropertySource> futureApplicationProfile = this.findOneAsync(application, profile, label);
		Future<PropertySource> futureApplication = this.findOneAsync(application, "", label);
		Future<PropertySource> futureGlobalProfile = this.findOneAsync(GLOBAL_APPLICATION, profile, label);
		Future<PropertySource> futureGlobal = this.findOneAsync(GLOBAL_APPLICATION, "", label);
				
		this.addToEnvironment(futureApplicationProfile, environment);
		this.addToEnvironment(futureApplication, environment);
		this.addToEnvironment(futureGlobalProfile, environment);
		this.addToEnvironment(futureGlobal, environment);
		
		return environment;
	}
	
	private void addToEnvironment(Future<PropertySource> future, Environment environment){
		PropertySource propertySource;
		try {
			propertySource = future.get(1000, TimeUnit.MILLISECONDS);
			if (propertySource!=null){
				environment.add(propertySource);
			}
		} catch (InterruptedException | ExecutionException | TimeoutException  e) {
			e.printStackTrace();
		}		
	}
			
	private Future<PropertySource> findOneAsync(final String application, final String profile, final String label){
		return executor.submit(new Callable<PropertySource>() {
			@Override
			public PropertySource call() throws Exception {
				PropertySource result = null;
				ResultSet rsVersion = session.execute(stmtGetVersion.bind(application, label, profile));
				Row rowVersion = rsVersion.one();
				if (rowVersion!=null){
					UUID version = rowVersion.getUUID("version");
					Row rowSnapshot = session.execute(stmtGetSnapshot.bind(application, version)).one();
					if (rowSnapshot!=null){
						Map<Object, Object> parameters = rowSnapshot.getMap("parameters", Object.class, Object.class); 
								result = new PropertySource("cassandra-"+application+"-"+profile, parameters);
					}
				}
				return result;
			}
		});
	}

}
