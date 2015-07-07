package demo;

import org.springframework.beans.factory.annotation.Autowired;
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

@Configuration
@EnableAutoConfiguration
//@EnableDiscoveryClient
@EnableConfigServer

//@ConditionalOnMissingBean(EnvironmentRepository.class)
@EnableConfigurationProperties(ConfigServerProperties.class)
public class ConfigServerApplication {
//	ConfigServerConfiguration
	public static void main(String[] args) {
		SpringApplication.run(ConfigServerApplication.class, args);		
	}
	
	@Configuration	
	protected static class CassandraRepositoryConfiguration {
		@Autowired
		private ConfigurableEnvironment environment;		
		
		@Bean
		public EnvironmentRepository environmentRepository() {
			return new CassandraEnvironmentRepository(environment);
		}

	}
}
