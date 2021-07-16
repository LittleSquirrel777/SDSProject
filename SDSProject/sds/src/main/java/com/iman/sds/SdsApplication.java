package com.iman.sds;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.iman.sds.mapper")
public class SdsApplication {

	public static void main(String[] args) {
		SpringApplication.run(SdsApplication.class, args);
	}

}
