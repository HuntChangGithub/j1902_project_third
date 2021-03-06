package com.qf.j1902;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableDiscoveryClient
@EnableFeignClients
@SpringBootApplication
public class J1902EurekaMemberviewApplication {

	public static void main(String[] args) {
		SpringApplication.run(J1902EurekaMemberviewApplication.class, args);
	}

}
