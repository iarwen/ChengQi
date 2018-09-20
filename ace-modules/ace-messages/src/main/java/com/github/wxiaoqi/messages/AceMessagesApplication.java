package com.github.wxiaoqi.messages;

import com.ctrip.framework.apollo.spring.annotation.EnableApolloConfig;
import com.spring4all.swagger.EnableSwagger2Doc;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement
@EnableSwagger2Doc
@EnableEurekaClient
@EnableFeignClients
//@EnableApolloConfig
public class AceMessagesApplication {
	public static void main(String[] args) {
		SpringApplication.run(AceMessagesApplication.class, args);
	}
}
