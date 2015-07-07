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
		ResultSet rs = session.execute(statement.bind(application, label, profile));
		for (Row row : rs.all()){
			Map<Object,Object> objs = row.getMap("properties", Object.class, Object.class);
			PropertySource ps = new PropertySource(profile+"-"+label, objs);
			environment.add(ps);
		}
		
		// luego con application		
		profile="";
		rs = session.execute(statement.bind(application, label, profile));
		for (Row row : rs.all()){
			Map<Object,Object> objs = row.getMap("properties", Object.class, Object.class);
			PropertySource ps = new PropertySource(profile+"-"+label, objs);
			environment.add(ps);
		}
		
		// por ultimo con global-application
		application="application";
		rs = session.execute(statement.bind(application, label, profile));
		for (Row row : rs.all()){
			Map<Object,Object> objs = row.getMap("properties", Object.class, Object.class);
			PropertySource ps = new PropertySource(profile+"-"+label, objs);
			environment.add(ps);
		}
		
		profile="development";
		rs = session.execute(statement.bind(application, label, profile));
		for (Row row : rs.all()){
			Map<Object,Object> objs = row.getMap("properties", Object.class, Object.class);
			PropertySource ps = new PropertySource(profile+"-"+label, objs);
			environment.add(ps);
		}

		return environment;
	}

}
