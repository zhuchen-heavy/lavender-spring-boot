package com.lavender.spring.boot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class LavenderSpringBootApplication {

    public static void main(String[] args) {
        SpringApplication.run(LavenderSpringBootApplication.class, args);

        // 参考 JsonPropertySourceLoader 上的注释。
        //new SpringApplicationBuilder(LavenderSpringBootApplication.class).properties("spring.config.additional-location=classpath:/conf/a.json").run(args);
    }

}
