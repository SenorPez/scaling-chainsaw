package com.senorpez.loot.api.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.senorpez.loot.api.Application;
import com.senorpez.loot.api.controller.CampaignController;
import com.senorpez.loot.api.entity.CampaignEntity;
import com.senorpez.loot.api.pojo.Campaign;
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


class EmbeddedCampaignModelAssemblerTest {
    private final static Random RANDOM = new Random();
    private final static ObjectMapper objectMapper = new ObjectMapper();

    private static Integer expectedId;
    private static String expectedName;
    private static String expectedLink;

    @BeforeAll
    static void beforeAll() {
        objectMapper.registerModule(new Jackson2HalModule());
        objectMapper.setHandlerInstantiator(
                new Jackson2HalModule.HalHandlerInstantiator(
                        Application.LINK_RELATION_PROVIDER,
                        Application.CURIE_PROVIDER,
                        MessageResolver.DEFAULTS_ONLY)
        );
    }

    @BeforeEach
    void setUp() {
        expectedId = RANDOM.nextInt();
        expectedName = "Defiance in Phlan";
        expectedLink = String.format("http://localhost/campaigns/%d", expectedId);

        MockHttpServletRequest request = new MockHttpServletRequest();
        ServletRequestAttributes requestAttributes = new ServletRequestAttributes(request);
        RequestContextHolder.setRequestAttributes(requestAttributes);
    }

    @Test
    void serialize_model() throws JsonProcessingException, JSONException {
        EmbeddedCampaignModelAssembler assembler = new EmbeddedCampaignModelAssembler(CampaignController.class, EmbeddedCampaignModel.class);
        CampaignEntity entity = spy(new CampaignEntity(new Campaign(expectedName)));
        when(entity.getId()).thenReturn(expectedId); // Need to spy since constructors leave ID null.
        EmbeddedCampaignModel model = assembler.toModel(entity);
        String json = objectMapper.writeValueAsString(model);

        final String expectedJson = String.format(
                "{id: %d, name: \"%s\", _links: {self: {href: \"%s\"}}}",
                expectedId, expectedName, expectedLink
        );
        JSONAssert.assertEquals(expectedJson, json, NON_EXTENSIBLE);
    }

    @Test
    void serialize_collection_empty() throws JsonProcessingException, JSONException {
        EmbeddedCampaignModelAssembler assembler = new EmbeddedCampaignModelAssembler(CampaignController.class, EmbeddedCampaignModel.class);
        List<CampaignEntity> entities = Collections.emptyList();
        CollectionModel<EmbeddedCampaignModel> models = assembler.toCollectionModel(entities);
        String json = objectMapper.writeValueAsString(models);

        String expectedIndexLink = "http://localhost";
        String expectedSelfLink = "http://localhost/campaigns";

        final String expectedJson = String.format(
                "{_links: {index: {href: \"%s\"}, self: {href: \"%s\"}}}",
                expectedIndexLink, expectedSelfLink
        );
        JSONAssert.assertEquals(expectedJson, json, NON_EXTENSIBLE);
    }

    @Test
    void serialize_collection() throws JsonProcessingException, JSONException {
        EmbeddedCampaignModelAssembler assembler = new EmbeddedCampaignModelAssembler(CampaignController.class, EmbeddedCampaignModel.class);
        CampaignEntity entity = spy(new CampaignEntity(new Campaign(expectedName)));
        when(entity.getId()).thenReturn(expectedId); // Need to spy since constructors leave ID null.

        List<CampaignEntity> entities = Collections.singletonList(entity);
        CollectionModel<EmbeddedCampaignModel> models = assembler.toCollectionModel(entities);
        String json = objectMapper.writeValueAsString(models);

        final String expectedEntityJson = String.format(
                "{id: %d, name: \"%s\", _links: {self: {href: \"%s\"}}}",
                expectedId, expectedName, expectedLink
        );
        String expectedIndexLink = "http://localhost";
        String expectedSelfLink = "http://localhost/campaigns";
        String expectedCuriesLink = "http://localhost/docs/reference.html#resources-loot-{rel}";

        final String expectedJson = String.format(
                "{_embedded: {\"loot-api:campaign\": [%s]}," +
                        "_links: {index: {href: \"%s\"}, self: {href: \"%s\"}, curies: [{href: \"%s\", name: \"loot-api\", templated: true}]}}",
                expectedEntityJson, expectedIndexLink, expectedSelfLink, expectedCuriesLink
        );
        JSONAssert.assertEquals(expectedJson, json, NON_EXTENSIBLE);
    }
}