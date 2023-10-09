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
public class StatsServer {
    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(StatsServer.class);
        app.setDefaultProperties(Collections
                .singletonMap("server.port", "9080"));
        app.run(args);
    }
}