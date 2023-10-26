package ru.practicum;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.util.Collections;

@SpringBootApplication
@EnableJpaRepositories("ru.practicum.*")
@ComponentScan(basePackages = { "ru.practicum.*" })
@EntityScan("ru.practicum.*")
public class MainService {
    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(MainService.class);
        app.setDefaultProperties(Collections
                .singletonMap("server.port", "8080"));
        app.run(args);
    }
}