package com.grocery.quickbasket;

import com.grocery.quickbasket.config.RsaConfigProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@EnableCaching
@EnableConfigurationProperties(RsaConfigProperties.class)
@EnableSpringDataWebSupport(pageSerializationMode = EnableSpringDataWebSupport.PageSerializationMode.VIA_DTO)
@SpringBootApplication
public class BackendRepoApplication {

    public static void main(String[] args) {
        SpringApplication.run(BackendRepoApplication.class, args);
    }

}
