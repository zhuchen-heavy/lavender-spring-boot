package com.lavender.spring.boot.custom.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;

//@Component
public class TestJsonCommandLineRunner implements CommandLineRunner {

    @Value("${customize.property.message}")
    private String message;

    @Override
    public void run(String... args) throws Exception {
        System.out.println("message is : " + message);
    }

}
