package com.github.wxiaoqi.messages;

import com.ctrip.framework.apollo.spring.annotation.EnableApolloConfig;
import com.github.wxiaoqi.messages.customannotation.OperateAspect;
import com.spring4all.swagger.EnableSwagger2Doc;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import java.util.List;


@SpringBootApplication
@EnableTransactionManagement
@EnableSwagger2Doc
@EnableEurekaClient
@EnableFeignClients
@EnableAutoConfiguration
//@EnableApolloConfig
@EnableAspectJAutoProxy(exposeProxy = true)
public class AceMessagesApplication  {

	public static void main(String[] args) {
		SpringApplication.run(AceMessagesApplication.class, args);
	}

//    @Override
//    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
//	    argumentResolvers.add(new OperateAspect());
//	    super.addArgumentResolvers(argumentResolvers);
//	}


}

