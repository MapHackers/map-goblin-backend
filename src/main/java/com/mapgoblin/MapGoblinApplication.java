package com.mapgoblin;

import com.mapgoblin.config.FileUploadConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableConfigurationProperties(
        {FileUploadConfig.class})
@SpringBootApplication
public class MapGoblinApplication {

    public static void main(String[] args) {
        SpringApplication.run(MapGoblinApplication.class, args);
    }

}
