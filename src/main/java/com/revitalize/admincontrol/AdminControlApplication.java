package com.revitalize.admincontrol;

import com.revitalize.admincontrol.config.CondicionanteProperties;
import com.revitalize.admincontrol.config.CorsProperties;
import com.revitalize.admincontrol.config.NotificationEmailProperties;
import com.revitalize.admincontrol.security.SecurityProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableAsync
@EnableScheduling
@EnableConfigurationProperties({
		SecurityProperties.class,
		CorsProperties.class,
		CondicionanteProperties.class,
		NotificationEmailProperties.class
})
public class AdminControlApplication {

	public static void main(String[] args) {
		SpringApplication.run(AdminControlApplication.class, args);
	}

}
