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
import org.springframework.hateoas.mediatype.MessageResolver;
import org.springframework.hateoas.mediatype.hal.Jackson2HalModule;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Random;

import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;
import static org.skyscreamer.jsonassert.JSONCompareMode.NON_EXTENSIBLE;

class ItemModelAssemblerTest {
    private final static Random RANDOM = new Random();
    private final static ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private static Integer expectedId;
    private static String expectedName;
    private static BigDecimal expectedWeight;

    private static String expectedSelfLink;
    private final static String expectedIndexLink = "http://localhost";
    private final static String expectedItemsLink = "http://localhost/items";
    private final static String expectedCuriesLink = "http://localhost/docs/reference.html#resources-loot-{rel}";

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
        expectedWeight = BigDecimal.valueOf(RANDOM.nextDouble()).setScale(2, RoundingMode.HALF_UP);
        expectedSelfLink = String.format("http://localhost/items/%d", expectedId);

        MockHttpServletRequest request = new MockHttpServletRequest();
        ServletRequestAttributes requestAttributes = new ServletRequestAttributes(request);
        RequestContextHolder.setRequestAttributes(requestAttributes);
    }

    @Test
    void serialize_model() throws JsonProcessingException, JSONException {
        ItemEntity entity = spy(new ItemEntity(new Item(expectedName, expectedWeight)));
        when(entity.getId()).thenReturn(expectedId);

        ItemModelAssembler assembler = new ItemModelAssembler(ItemController.class, ItemModel.class);
        ItemModel model = assembler.toModel(entity);
        String json = OBJECT_MAPPER.writeValueAsString(model);

        final String expectedJson = String.format(
                "{id: %d, name: \"%s\", weight: %.2f," +
                        "_links: {self: {href: \"%s\"}, index: {href: \"%s\"}, \"loot-api:lootitems\": {href: \"%s\"}, curies: [{href: \"%s\", name: \"loot-api\", templated: true}]}}",
                expectedId, expectedName, expectedWeight, expectedSelfLink, expectedIndexLink, expectedItemsLink, expectedCuriesLink
        );
        JSONAssert.assertEquals(expectedJson, json, NON_EXTENSIBLE);
    }
}