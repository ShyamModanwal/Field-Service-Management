package com.fieldservicemanagement.fieldservicemanagement;

import org.springframework.scheduling.annotation.EnableScheduling;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableScheduling
public class FieldservicemanagementApplication {

	public static void main(String[] args) {
		SpringApplication.run(FieldservicemanagementApplication.class, args);
	}

}
