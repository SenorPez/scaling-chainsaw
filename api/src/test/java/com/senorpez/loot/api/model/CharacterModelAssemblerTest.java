package com.senorpez.loot.api.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.senorpez.loot.api.Application;
import com.senorpez.loot.api.controller.CharacterController;
import com.senorpez.loot.api.entity.CampaignEntity;
import com.senorpez.loot.api.entity.CharacterEntity;
import com.senorpez.loot.api.entity.InventoryItemEntity;
import com.senorpez.loot.api.entity.ItemEntity;
import com.senorpez.loot.api.pojo.Campaign;
import com.senorpez.loot.api.pojo.Character;
import com.senorpez.loot.api.pojo.InventoryItem;
import com.senorpez.loot.api.pojo.Item;
import com.senorpez.loot.api.repository.ItemTransactionRepository;
import org.json.JSONException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.hateoas.mediatype.MessageResolver;
import org.springframework.hateoas.mediatype.hal.Jackson2HalModule;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;
import static org.skyscreamer.jsonassert.JSONCompareMode.NON_EXTENSIBLE;

class CharacterModelAssemblerTest {
    @InjectMocks
    private Application application;

    @Spy
    private ItemTransactionRepository itemTransactionRepository;

    private final static Random RANDOM = new Random();
    private final static ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private static Integer expectedCampaignId;
    private static Integer expectedCharacterId;
    private static String expectedName;

    private static Integer expectedQuantityGold;
    private static Integer expectedQuantitySword;

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

        expectedQuantityGold = RANDOM.nextInt();
        expectedQuantitySword = RANDOM.nextInt();

        expectedSelfLink = String.format("http://localhost/campaigns/%d/characters/%d", expectedCampaignId, expectedCharacterId);
        expectedCharactersLink = String.format("http://localhost/campaigns/%d/characters", expectedCampaignId);

        MockHttpServletRequest request = new MockHttpServletRequest();
        ServletRequestAttributes requestAttributes = new ServletRequestAttributes(request);
        RequestContextHolder.setRequestAttributes(requestAttributes);

        MockitoAnnotations.initMocks(this);
    }

    @Test
    void serialize_model() throws JsonProcessingException, JSONException {
        Application.itemTransactionRepository = itemTransactionRepository;
        when(itemTransactionRepository.getQuantity(1)).thenReturn(expectedQuantityGold);
        when(itemTransactionRepository.getQuantity(2)).thenReturn(expectedQuantitySword);

        CampaignEntity campaignEntity = spy(new CampaignEntity(new Campaign("Defiance in Phlan")));
        when(campaignEntity.getId()).thenReturn(expectedCampaignId);

        Set<InventoryItemEntity> inventoryItemEntities = new HashSet<>();
        InventoryItemEntity goldPiece = spy(new InventoryItemEntity(
                new InventoryItem(1, null, null), new ItemEntity(new Item("Gold Piece", null))
        ));
        inventoryItemEntities.add(goldPiece);
        when(goldPiece.getId()).thenReturn(1);
        InventoryItemEntity sword = spy(new InventoryItemEntity(
                new InventoryItem(2, null, "Artifact Weapon"), new ItemEntity(new Item("Sword of Justice", BigDecimal.valueOf(10L)))
        ));
        when(sword.getId()).thenReturn(2);
        inventoryItemEntities.add(sword);

        CharacterEntity characterEntity = spy(new CharacterEntity(new Character(expectedName), campaignEntity, inventoryItemEntities));
        when(characterEntity.getId()).thenReturn(expectedCharacterId);

        CharacterModelAssembler assembler = new CharacterModelAssembler(CharacterController.class, CharacterModel.class);
        CharacterModel model = assembler.toModel(characterEntity);
        String json = OBJECT_MAPPER.writeValueAsString(model);

        final String expectedJson = String.format(
                "{id: %d, name: \"%s\"," +
                        "inventory: [{name: \"Gold Piece\", weight: null, charges: null, details: null, quantity: %d}, " +
                        "{name: \"Sword of Justice\", weight: 10.0, charges: null, details: \"Artifact Weapon\", quantity: %d}], " +
                        "_links: {self: {href: \"%s\"}, index: {href: \"%s\"}, \"loot-api:characters\": {href: \"%s\"}, curies: [{href: \"%s\", name: \"loot-api\", templated: true}]}}",
                expectedCharacterId, expectedName, expectedQuantityGold, expectedQuantitySword, expectedSelfLink, expectedIndexLink, expectedCharactersLink, expectedCuriesLink
        );
        JSONAssert.assertEquals(expectedJson, json, NON_EXTENSIBLE);
    }
}