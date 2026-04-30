package com.example.entregaya;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class EntregayaApplication {

	public static void main(String[] args) {
		SpringApplication.run(EntregayaApplication.class, args);
	}

}
