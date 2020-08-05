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

class EmbeddedCharacterModelAssemblerTest {
    private final static Random RANDOM = new Random();
    private final static ObjectMapper objectMapper = new ObjectMapper();

    private static Integer expectedCampaignId;
    private static Integer expectedCharacterId;
    private static String expectedName;

    private static String expectedSelfLink;
    private final static String expectedIndexLink = "http://localhost";
    private static String expectedCampaignLink;
    private static String expectedCharacterLink;
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
        expectedCampaignId = RANDOM.nextInt();
        expectedCharacterId = RANDOM.nextInt();
        expectedName = "Aethelwuf";

        expectedSelfLink = String.format("http://localhost/campaigns/%d/characters", expectedCampaignId);
        expectedCampaignLink = String.format("http://localhost/campaigns/%d", expectedCampaignId);
        expectedCharacterLink = String.format("http://localhost/campaigns/%d/characters/%d", expectedCampaignId, expectedCharacterId);

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

        EmbeddedCharacterModelAssembler assembler = new EmbeddedCharacterModelAssembler(CharacterController.class, EmbeddedCharacterModel.class);
        EmbeddedCharacterModel model = assembler.toModel(characterEntity);
        String json = objectMapper.writeValueAsString(model);

        final String expectedJson = String.format(
                "{id: %d, name: \"%s\", _links: {self: {href: \"%s\"}}}",
                expectedCharacterId, expectedName, expectedCharacterLink
        );
        JSONAssert.assertEquals(expectedJson, json, NON_EXTENSIBLE);
    }

    @Test
    void serialize_collection_empty() throws JsonProcessingException, JSONException {
        List<CharacterEntity> entities = Collections.emptyList();

        EmbeddedCharacterModelAssembler assembler = new EmbeddedCharacterModelAssembler(CharacterController.class, EmbeddedCharacterModel.class);
        CollectionModel<EmbeddedCharacterModel> models = assembler.toCollectionModel(entities, expectedCampaignId);
        String json = objectMapper.writeValueAsString(models);

        final String expectedJson = String.format(
                "{_links: {index: {href: \"%s\"}, self: {href: \"%s\"}, \"loot-api:campaign\": {href: \"%s\"}, curies: [{href: \"%s\", name: \"loot-api\", templated: true}]}}",
                expectedIndexLink, expectedSelfLink, expectedCampaignLink, expectedCuriesLink
        );
        JSONAssert.assertEquals(expectedJson, json, NON_EXTENSIBLE);
    }

    @Test
    void serialize_collection() throws JsonProcessingException, JSONException {
        CampaignEntity campaignEntity = spy(new CampaignEntity(new Campaign("Defiance in Phlan")));
        when(campaignEntity.getId()).thenReturn(expectedCampaignId);

        CharacterEntity characterEntity = spy(new CharacterEntity(new Character(expectedName), campaignEntity));
        when(characterEntity.getId()).thenReturn(expectedCharacterId);
        List<CharacterEntity> entities = Collections.singletonList(characterEntity);

        EmbeddedCharacterModelAssembler assembler = new EmbeddedCharacterModelAssembler(CharacterController.class, EmbeddedCharacterModel.class);
        CollectionModel<EmbeddedCharacterModel> models = assembler.toCollectionModel(entities, expectedCampaignId);
        String json = objectMapper.writeValueAsString(models);

        final String expectedJson = String.format(
                "{_embedded: {\"loot-api:character\": [{id: %d, name: \"%s\", _links: {self: {href: \"%s\"}}}]}, " +
                        "_links: {index: {href: \"%s\"}, self: {href: \"%s\"}, \"loot-api:campaign\": {href: \"%s\"}, curies: [{href: \"%s\", name: \"loot-api\", templated: true}]}}",
                expectedCharacterId, expectedName, expectedCharacterLink, expectedIndexLink, expectedSelfLink, expectedCampaignLink, expectedCuriesLink
        );
        JSONAssert.assertEquals(expectedJson, json, NON_EXTENSIBLE);
    }
}