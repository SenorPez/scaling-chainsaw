package com.senorpez.loot.api;

import com.ulisesbocchio.jasyptspringboot.annotation.EnableEncryptableProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.hateoas.UriTemplate;
import org.springframework.hateoas.mediatype.hal.CurieProvider;
import org.springframework.hateoas.mediatype.hal.DefaultCurieProvider;

@SpringBootApplication
@EnableEncryptableProperties
public class Application {
    static final DefaultCurieProvider CURIE_PROVIDER = new DefaultCurieProvider("loot-api", UriTemplate.of("/docs/reference.html#resources-loot-{rel}"));

    public static void main(final String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public CurieProvider curieProvider() {
        return CURIE_PROVIDER;
    }
}
