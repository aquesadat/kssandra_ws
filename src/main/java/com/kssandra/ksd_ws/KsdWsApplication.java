package com.kssandra.ksd_ws;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@ComponentScan({ "com.kssandra" })
@EntityScan("com.kssandra.ksd_persistence.domain")
@EnableJpaRepositories("com.kssandra.ksd_persistence.repository")
public class KsdWsApplication {

	public static void main(String[] args) {
		SpringApplication.run(KsdWsApplication.class, args);
	}

}
