package com.way2learnonline;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.sleuth.sampler.AlwaysSampler;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@EnableDiscoveryClient
public class QuotesApplication {
	

	public static void main(String[] args) {
		SpringApplication.run(QuotesApplication.class, args);
	}
	
	// TODO-1 Uncomment the below bean such that 100% samples are sent to zipkin
	
/*	@Bean
	public AlwaysSampler defaultSampler() {
	  return new AlwaysSampler();
	}*/
	
	
}

