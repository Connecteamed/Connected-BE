package com.connecteamed.server;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ConnecteamedApplication {

	public static void main(String[] args) {
		Dotenv.load();
		SpringApplication.run(ConnecteamedApplication.class, args);
	}

}

