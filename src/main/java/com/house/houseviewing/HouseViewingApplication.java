package com.house.houseviewing;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class HouseViewingApplication {

	public static void main(String[] args) {
		SpringApplication.run(HouseViewingApplication.class, args);
	}

}
