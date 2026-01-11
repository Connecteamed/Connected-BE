package com.connected.be;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EntityScan(basePackages = "com.connecteamed.server")
@EnableJpaAuditing
public class ConnectedBeApplication {

	public static void main(String[] args) {
		SpringApplication.run(ConnectedBeApplication.class, args);
	}

}
