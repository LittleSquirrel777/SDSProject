package com.iman.sds;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Date;

@SpringBootApplication
@MapperScan("com.iman.sds.mapper")
public class SdsApplication {

	public static void main(String[] args) {
//		System.out.println(new Date());
		SpringApplication.run(SdsApplication.class, args);
	}

}
