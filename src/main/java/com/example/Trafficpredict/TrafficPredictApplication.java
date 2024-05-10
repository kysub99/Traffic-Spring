package com.example.Trafficpredict;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(ItApiProperties.class)
public class TrafficPredictApplication {

	public static void main(String[] args) {
		SpringApplication.run(TrafficPredictApplication.class, args);
	}
}
