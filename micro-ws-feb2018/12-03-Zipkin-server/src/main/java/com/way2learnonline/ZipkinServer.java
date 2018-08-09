package com.way2learnonline;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import zipkin.server.EnableZipkinServer;

@SpringBootApplication
// TODO-1 Uncomment the below annotation to configure  all the beans required for Zipkin server

//@EnableZipkinServer
public class ZipkinServer {

	public static void main(String[] args) {
		SpringApplication.run(ZipkinServer.class, args);
	}
}
