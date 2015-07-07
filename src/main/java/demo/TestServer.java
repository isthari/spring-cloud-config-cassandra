package demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Configuration
@EnableAutoConfiguration
@RestController
//@ComponentScan
public class TestServer {
	@RequestMapping("/")
	public String home(){
		return "hola mundo";
	}
	
	public static void mains(String[] args) {
		SpringApplication.run(TestServer.class, args);
	}
}
