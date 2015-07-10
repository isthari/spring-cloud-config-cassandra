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

package demo.server;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.config.server.ConfigServerProperties;
import org.springframework.cloud.config.server.EnableConfigServer;
import org.springframework.cloud.config.server.EnvironmentRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.ConfigurableEnvironment;

import com.isthari.spring.cloud.config.cassandra.CassandraEnvironmentRepository;


/**
 * Demo project for a spring cloud config server application to store the configuration on Cassandra
 * 
 * @author jose.hernandez@isthari.com
 *
 */
@Configuration
@EnableAutoConfiguration
@EnableConfigServer
@EnableConfigurationProperties(ConfigServerProperties.class)
public class DemoServer {
	
	public DemoServer(){		
	}
	
	public static void main(String[] args) {
		SpringApplication.run(DemoServer.class, args);		
	}
	
	@Configuration	
	protected static class CassandraRepositoryConfiguration {
		@Autowired
		private ConfigurableEnvironment environment;
	
		@Value("${isthari.cassandra.hostname}")
		private String hostname;
		
		@Value("${isthari.cassandra.username}")
		private String username;
		
		@Value("${isthari.cassandra.password}")
		private String password;
		
		@Value("${isthari.cassandra.create_schema}")
		private Boolean createSchema=true;
		
		@Bean
		public EnvironmentRepository environmentRepository() {			
			return new CassandraEnvironmentRepository(environment, hostname, username, password, createSchema);
		}

	}
}
