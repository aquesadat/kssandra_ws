package com.kssandra.ksd_ws;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan({ "com.kssandra" })
public class KsdWsApplication {

	public static void main(String[] args) {
		SpringApplication.run(KsdWsApplication.class, args);
	}

}
