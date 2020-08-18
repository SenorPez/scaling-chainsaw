package com.senorpez.loot.api;

import com.senorpez.loot.api.repository.ItemTransactionRepository;
import com.ulisesbocchio.jasyptspringboot.annotation.EnableEncryptableProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.hateoas.UriTemplate;
import org.springframework.hateoas.mediatype.hal.CurieProvider;
import org.springframework.hateoas.mediatype.hal.DefaultCurieProvider;
import org.springframework.hateoas.server.LinkRelationProvider;
import org.springframework.hateoas.server.core.AnnotationLinkRelationProvider;

@SpringBootApplication
@EnableEncryptableProperties
public class Application {
    @Autowired
    public static ItemTransactionRepository itemTransactionRepository;

    public static final CurieProvider CURIE_PROVIDER = new DefaultCurieProvider("loot-api", UriTemplate.of("/docs/reference.html#resources-loot-{rel}" ));
    public static final LinkRelationProvider LINK_RELATION_PROVIDER = new AnnotationLinkRelationProvider();

    public static void main(final String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public CurieProvider curieProvider() {
        return CURIE_PROVIDER;
    }

    @Bean
    public LinkRelationProvider linkRelationProvider() {
        return LINK_RELATION_PROVIDER;
    }
}
