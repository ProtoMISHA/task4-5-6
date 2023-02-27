package com.example.task4;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@SpringBootApplication
@EnableWebMvc
@EnableElasticsearchRepositories
public class Task4Application {

    public static void main(String[] args) {
        SpringApplication.run(Task4Application.class, args);
    }

}
