package com.ejercicio8;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import com.ejercicio8.config.JwtProperties;

@SpringBootApplication
@EnableConfigurationProperties(JwtProperties.class)
public class Ejercicio8Application {
    public static void main(String[] args) {
        SpringApplication.run(Ejercicio8Application.class, args);
    }
}
