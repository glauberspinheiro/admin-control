package com.revitalize.admincontrol;

import com.revitalize.admincontrol.security.SecurityProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(SecurityProperties.class)
public class AdminControlApplication {

	public static void main(String[] args) {
		SpringApplication.run(AdminControlApplication.class, args);
	}

}
