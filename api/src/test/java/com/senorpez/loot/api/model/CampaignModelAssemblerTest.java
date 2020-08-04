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
import org.springframework.hateoas.mediatype.MessageResolver;
import org.springframework.hateoas.mediatype.hal.Jackson2HalModule;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Random;

import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;
import static org.skyscreamer.jsonassert.JSONCompareMode.NON_EXTENSIBLE;

class CampaignModelAssemblerTest {
    private final static Random RANDOM = new Random();
    private final static ObjectMapper objectMapper = new ObjectMapper();

    private static Integer expectedId;
    private static String expectedName;

    private static String expectedSelfLink;
    private final static String expectedIndexLink = "http://localhost";
    private final static String expectedCampaignsLink = "http://localhost/campaigns";
    private static String expectedCharactersLinks;
    private final static String expectedCuriesLink = "http://localhost/docs/reference.html#resources-loot-{rel}";

    @BeforeAll
    static void beforeAll() {
        objectMapper.registerModule(new Jackson2HalModule());
        objectMapper.setHandlerInstantiator(
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
        expectedName = "Defiance in Phlan";
        expectedSelfLink = String.format("http://localhost/campaigns/%d", expectedId);
        expectedCharactersLinks = String.format("http://localhost/campaigns/%d/characters", expectedId);

        MockHttpServletRequest request = new MockHttpServletRequest();
        ServletRequestAttributes requestAttributes = new ServletRequestAttributes(request);
        RequestContextHolder.setRequestAttributes(requestAttributes);
    }

    @Test
    void serialize_model() throws JsonProcessingException, JSONException {
        CampaignEntity entity = spy(new CampaignEntity(new Campaign(expectedName)));
        when(entity.getId()).thenReturn(expectedId); // Need to spy since constructors leave ID null.

        CampaignModelAssembler assembler = new CampaignModelAssembler(CampaignController.class, CampaignModel.class);
        CampaignModel model = assembler.toModel(entity);
        String json = objectMapper.writeValueAsString(model);

        final String expectedJson = String.format(
                "{id: %d, name: \"%s\", " +
                        "_links: {self: {href: \"%s\"}, index: {href: \"%s\"}, \"loot-api:campaigns\": {href: \"%s\"}, \"loot-api:characters\": {href: \"%s\"}, curies: [{href: \"%s\", name: \"loot-api\", templated: true}]}}",
                expectedId, expectedName, expectedSelfLink, expectedIndexLink, expectedCampaignsLink, expectedCharactersLinks, expectedCuriesLink
        );
        JSONAssert.assertEquals(expectedJson, json, NON_EXTENSIBLE);
    }
}