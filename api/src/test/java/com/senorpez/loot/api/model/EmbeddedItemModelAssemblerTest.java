package com.senorpez.loot.api.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.senorpez.loot.api.Application;
import com.senorpez.loot.api.controller.ItemController;
import com.senorpez.loot.api.entity.ItemEntity;
import com.senorpez.loot.api.pojo.Item;
import org.json.JSONException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.mediatype.MessageResolver;
import org.springframework.hateoas.mediatype.hal.Jackson2HalModule;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Collections;
import java.util.List;
import java.util.Random;

import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;
import static org.skyscreamer.jsonassert.JSONCompareMode.NON_EXTENSIBLE;

class EmbeddedItemModelAssemblerTest {
    private final static Random RANDOM = new Random();
    private final static ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private static Integer expectedId;
    private static String expectedName;
    private static final String expectedIndexLink = "http://localhost";
    private static final String expectedSelfLink = "http://localhost/items";
    private static String expectedItemLink;
    private static final String expectedCuriesLink = "http://localhost/docs/reference.html#resources-loot-{rel}";

    @BeforeAll
    static void beforeAll() {
        OBJECT_MAPPER.registerModule(new Jackson2HalModule());
        OBJECT_MAPPER.setHandlerInstantiator(
                new Jackson2HalModule.HalHandlerInstantiator(
                        Application.LINK_RELATION_PROVIDER,
                        Application.CURIE_PROVIDER,
                        MessageResolver.DEFAULTS_ONLY
                )
        );
    }

    @BeforeEach
    void setUp() {
        expectedId = RANDOM.nextInt();
        expectedName = "Gold Piece";
        expectedItemLink = String.format("http://localhost/items/%d", expectedId);

        MockHttpServletRequest request = new MockHttpServletRequest();
        ServletRequestAttributes requestAttributes = new ServletRequestAttributes(request);
        RequestContextHolder.setRequestAttributes(requestAttributes);
    }

    @Test
    void serialize_model() throws JsonProcessingException, JSONException {
        ItemEntity entity = spy(new ItemEntity(new Item("Gold Piece", null)));
        when(entity.getId()).thenReturn(expectedId);

        EmbeddedItemModelAssembler assembler = new EmbeddedItemModelAssembler(ItemController.class, EmbeddedItemModel.class);
        EmbeddedItemModel model = assembler.toModel(entity);
        String json = OBJECT_MAPPER.writeValueAsString(model);

        final String expectedJson = String.format(
                "{id: %d, name: \"%s\", _links: {self: {href: \"%s\"}}}",
                expectedId, expectedName, expectedItemLink
        );
        JSONAssert.assertEquals(expectedJson, json, NON_EXTENSIBLE);
    }

    @Test
    void serialize_collection_empty() throws JsonProcessingException, JSONException {
        List<ItemEntity> entities = Collections.emptyList();
        EmbeddedItemModelAssembler assembler = new EmbeddedItemModelAssembler(ItemController.class, EmbeddedItemModel.class);
        CollectionModel<EmbeddedItemModel> models = assembler.toCollectionModel(entities);
        String json = OBJECT_MAPPER.writeValueAsString(models);

        final String expectedJson = String.format(
                "{_links: {index: {href: \"%s\"}, self: {href: \"%s\"}}}",
                expectedIndexLink, expectedSelfLink
        );
        JSONAssert.assertEquals(expectedJson, json, NON_EXTENSIBLE);
    }

    @Test
    void serialize_collection() throws JsonProcessingException, JSONException {
        ItemEntity entity = spy(new ItemEntity(new Item("Gold Piece", null)));
        when(entity.getId()).thenReturn(expectedId);
        List<ItemEntity> entities = Collections.singletonList(entity);

        EmbeddedItemModelAssembler assembler = new EmbeddedItemModelAssembler(ItemController.class, EmbeddedItemModel.class);
        CollectionModel<EmbeddedItemModel> models = assembler.toCollectionModel(entities);
        String json = OBJECT_MAPPER.writeValueAsString(models);

        final String expectedJson = String.format(
                "{_embedded: {\"loot-api:lootitem\": [{id: %d, name: \"%s\", _links: {self: {href: \"%s\"}}}]}," +
                        "_links: {index: {href: \"%s\"}, self: {href: \"%s\"}, curies: [{href: \"%s\", name: \"loot-api\", templated: true}]}}",
                expectedId, expectedName, expectedItemLink, expectedIndexLink, expectedSelfLink, expectedCuriesLink
        );
        JSONAssert.assertEquals(expectedJson, json, NON_EXTENSIBLE);
    }
}