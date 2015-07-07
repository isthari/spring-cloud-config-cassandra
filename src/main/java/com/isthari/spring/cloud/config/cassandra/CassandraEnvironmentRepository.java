package com.isthari.spring.cloud.config.cassandra;

import java.util.Map;

import org.springframework.cloud.config.environment.Environment;
import org.springframework.cloud.config.environment.PropertySource;
import org.springframework.cloud.config.server.EnvironmentRepository;
import org.springframework.core.env.ConfigurableEnvironment;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;

public class CassandraEnvironmentRepository implements EnvironmentRepository {
	private Session session;
	private PreparedStatement statement;

	public CassandraEnvironmentRepository(ConfigurableEnvironment environment) {
		Cluster cluster = Cluster.builder().addContactPoint("127.0.0.1").build();
		session = cluster.connect("config");
		statement = session.prepare("select * from configuration where application=? and label=? and profile=?");
	}

	@Override
	public String getDefaultLabel() {
		return "master";
	}

	@Override
	public Environment findOne(String application, String profile, String label) {
		Environment environment = new Environment(application, profile);
						
		// Primero con application-profile
		this.process(application, profile, label, environment);		
		
		// luego con application		
		this.process(application, "", label, environment);		
		
		// con global-application
		this.process("application", "", label, environment);
		
		// globa-application proffile
		this.process("application", profile, label, environment);		

		return environment;
	}
	
	private void process(String application, String profile, String label, Environment environment){
		ResultSet rs = session.execute(statement.bind(application, label, profile));
		Row row = rs.one();
		if (row!=null){		
			Map<Object,Object> objs = row.getMap("properties", Object.class, Object.class);
			PropertySource ps = new PropertySource(profile+"-"+label, objs);
			environment.add(ps);
		}
	}

}
