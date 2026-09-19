package com.peak.gaming;

import com.peak.gaming.config.AppProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(AppProperties.class)
public class GamingApplication {

	public static void main(String[] args) {
		SpringApplication.run(GamingApplication.class, args);
	}

}
