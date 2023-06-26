package com.xuecheng;

import com.spring4all.swagger.EnableSwagger2Doc;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients(basePackages={"com.xuecheng.content.feignclient"})
@EnableSwagger2Doc
@SpringBootApplication
public class ContentApplicationApi {
    public static void main(String[] args) {
        SpringApplication.run(ContentApplicationApi.class);
    }
}
