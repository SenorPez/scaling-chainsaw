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

class EmbeddedInventoryItemModelAssemblerTest {
    private final static Random RANDOM = new Random();
    private final static ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private static Integer expectedId;
    private static String expectedName;
    private static BigDecimal expectedWeight;
    private static Integer expectedCharges;
    private static String expectedDetails;
    private static Integer expectedQuantity;

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
        expectedCharges = RANDOM.nextInt();
        expectedDetails = "Curly's Lost Gold";
        expectedQuantity = RANDOM.nextInt();

        MockHttpServletRequest request = new MockHttpServletRequest();
        ServletRequestAttributes requestAttributes = new ServletRequestAttributes(request);
        RequestContextHolder.setRequestAttributes(requestAttributes);
    }

    @Test
    void serialize_model() throws JsonProcessingException, JSONException {
        InventoryItem inventoryItem = new InventoryItem(expectedId, expectedCharges, expectedDetails);
        CharacterEntity character = new CharacterEntity(new Character("Aethelwuf"), new CampaignEntity(new Campaign("Defiance in Phlan")));
        ItemEntity item = new ItemEntity(new Item(expectedName, expectedWeight));

        InventoryItemEntity entity = spy(new InventoryItemEntity(inventoryItem, character, item));
        when(entity.getId()).thenReturn(expectedId);

        EmbeddedInventoryItemModelAssembler assembler = new EmbeddedInventoryItemModelAssembler(CharacterController.class, EmbeddedInventoryItemModel.class);
        EmbeddedInventoryItemModel model = assembler.toModel(entity, expectedQuantity);
        String json = OBJECT_MAPPER.writeValueAsString(model);

        final String expectedJson = String.format(
                "{id: %d, name: \"%s\", weight: %.2f, charges: %d, details: \"%s\", quantity: %d}",
                expectedId, expectedName, expectedWeight, expectedCharges, expectedDetails, expectedQuantity
        );
        System.out.println(json);
        JSONAssert.assertEquals(expectedJson, json, NON_EXTENSIBLE);
    }
}