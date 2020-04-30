package com.senorpez.loottrack.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.hateoas.LinkRelation;
import org.springframework.hateoas.UriTemplate;
import org.springframework.hateoas.mediatype.MessageResolver;
import org.springframework.hateoas.mediatype.hal.DefaultCurieProvider;
import org.springframework.hateoas.mediatype.hal.Jackson2HalModule;
import org.springframework.hateoas.server.LinkRelationProvider;
import org.springframework.hateoas.server.core.DefaultLinkRelationProvider;
import org.springframework.hateoas.server.core.Relation;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.lang.NonNull;

import java.util.List;

class HalMessageConverter {
    static HttpMessageConverter<Object> getConverter(final List<MediaType> mediaTypes) {
        final ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new Jackson2HalModule());

        final DefaultCurieProvider curieProvider = new DefaultCurieProvider("loottable-api", UriTemplate.of("/docs/reference.html#resources-trident-{rel}"));
        final RelProvider relProvider = new RelProvider();

        objectMapper.setHandlerInstantiator(new Jackson2HalModule.HalHandlerInstantiator(relProvider, curieProvider, MessageResolver.DEFAULTS_ONLY));

        final MappingJackson2HttpMessageConverter halConverter = new MappingJackson2HttpMessageConverter(objectMapper);
        halConverter.setObjectMapper(objectMapper);
        halConverter.setSupportedMediaTypes(mediaTypes);

        return halConverter;
    }

    private static class RelProvider implements LinkRelationProvider {
        private static final DefaultLinkRelationProvider defaultRelProvider = new DefaultLinkRelationProvider();
        @Override
        @NonNull
        public LinkRelation getItemResourceRelFor(Class<?> type) {
            final Relation[] relations = type.getAnnotationsByType(Relation.class);
            return relations.length > 0 ? () -> relations[0].value() : defaultRelProvider.getItemResourceRelFor(type);
        }

        @Override
        @NonNull
        public LinkRelation getCollectionResourceRelFor(Class<?> type) {
            final Relation[] relations = type.getAnnotationsByType(Relation.class);
            return relations.length > 0 ? () -> relations[0].collectionRelation() : defaultRelProvider.getCollectionResourceRelFor(type);
        }

        @Override
        public boolean supports(@NonNull LookupContext delimiter) {
            return defaultRelProvider.supports(delimiter);
        }
    }
}