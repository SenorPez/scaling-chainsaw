package com.senorpez.loot.api.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.senorpez.loot.api.Application;
import com.senorpez.loot.api.controller.CharacterController;
import com.senorpez.loot.api.entity.CampaignEntity;
import com.senorpez.loot.api.entity.CharacterEntity;
import com.senorpez.loot.api.pojo.Campaign;
import com.senorpez.loot.api.pojo.Character;
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

class CharacterModelAssemblerTest {
    private final static Random RANDOM = new Random();
    private final static ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private static Integer expectedCampaignId;
    private static Integer expectedCharacterId;
    private static String expectedName;

    private static String expectedSelfLink;
    private final static String expectedIndexLink = "http://localhost";
    private static String expectedCharactersLink;
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
        expectedCampaignId = RANDOM.nextInt();
        expectedCharacterId = RANDOM.nextInt();
        expectedName = "Aethelwuf";

        expectedSelfLink = String.format("http://localhost/campaigns/%d/characters/%d", expectedCampaignId, expectedCharacterId);
        expectedCharactersLink = String.format("http://localhost/campaigns/%d/characters", expectedCampaignId);

        MockHttpServletRequest request = new MockHttpServletRequest();
        ServletRequestAttributes requestAttributes = new ServletRequestAttributes(request);
        RequestContextHolder.setRequestAttributes(requestAttributes);
    }

    @Test
    void serialize_model() throws JsonProcessingException, JSONException {
        CampaignEntity campaignEntity = spy(new CampaignEntity(new Campaign("Defiance in Phlan")));
        when(campaignEntity.getId()).thenReturn(expectedCampaignId);

        CharacterEntity characterEntity = spy(new CharacterEntity(new Character(expectedName), campaignEntity));
        when(characterEntity.getId()).thenReturn(expectedCharacterId);

        CharacterModelAssembler assembler = new CharacterModelAssembler(CharacterController.class, CharacterModel.class);
        CharacterModel model = assembler.toModel(characterEntity);
        String json = OBJECT_MAPPER.writeValueAsString(model);

        final String expectedJson = String.format(
                "{id: %d, name: \"%s\"," +
                        "_links: {self: {href: \"%s\"}, index: {href: \"%s\"}, \"loot-api:characters\": {href: \"%s\"}, curies: [{href: \"%s\", name: \"loot-api\", templated: true}]}}",
                expectedCharacterId, expectedName, expectedSelfLink, expectedIndexLink, expectedCharactersLink, expectedCuriesLink
        );
        JSONAssert.assertEquals(expectedJson, json, NON_EXTENSIBLE);
    }
}