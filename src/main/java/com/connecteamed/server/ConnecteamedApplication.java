package com.connecteamed.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class ConnecteamedApplication {

	public static void main(String[] args) {
		SpringApplication.run(ConnecteamedApplication.class, args);
	}

}
