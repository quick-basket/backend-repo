package com.grocery.quickbasket;

import com.grocery.quickbasket.config.RsaConfigProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;

@EnableCaching
@EnableConfigurationProperties(RsaConfigProperties.class)
@SpringBootApplication
public class BackendRepoApplication {

    public static void main(String[] args) {
        SpringApplication.run(BackendRepoApplication.class, args);
    }

}
