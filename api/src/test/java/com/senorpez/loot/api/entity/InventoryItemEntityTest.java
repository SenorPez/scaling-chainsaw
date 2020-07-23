package com.senorpez.loot.api.entity;

import com.senorpez.loot.api.pojo.Character;
import com.senorpez.loot.api.pojo.InventoryItem;
import com.senorpez.loot.api.pojo.Item;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class InventoryItemEntityTest {
    private final Random RANDOM = new Random();
    private static CharacterEntity expectedCharacter;
    private static ItemEntity expectedItem;
    private static Integer expectedCharges;
    private static String expectedDetails;

    @BeforeEach
    void setUp() {
        expectedCharges = RANDOM.nextInt();
        expectedDetails = "Special Item";
        expectedCharacter = new CharacterEntity(
                new Character("Aethelwuf"),
                new CampaignEntity()
        );
        expectedItem = new ItemEntity(
                new Item("Gold Piece", null)
        );
    }

    @Test
    void create_withCharges_withDetails_withCharacter_withItem() {
        final InventoryItem inventoryItem = new InventoryItem(RANDOM.nextInt(), expectedCharges, expectedDetails);
        final InventoryItemEntity inventoryItemEntity = new InventoryItemEntity(inventoryItem, expectedCharacter, expectedItem);

        assertThat(inventoryItemEntity.getId(), is(nullValue()));
        assertThat(inventoryItemEntity.getCharges(), is(expectedCharges));
        assertThat(inventoryItemEntity.getDetails(), is(expectedDetails));
        assertThat(inventoryItemEntity.getCharacterEntity(), is(expectedCharacter));
        assertThat(inventoryItemEntity.getItemEntity(), is(expectedItem));
    }

    @Test
    void create_nullCharges_withDetails_withCharacter_withItem() {
        final InventoryItem inventoryItem = new InventoryItem(RANDOM.nextInt(), null, expectedDetails);
        final InventoryItemEntity inventoryItemEntity = new InventoryItemEntity(inventoryItem, expectedCharacter, expectedItem);

        assertThat(inventoryItemEntity.getId(), is(nullValue()));
        assertThat(inventoryItemEntity.getCharges(), is(nullValue()));
        assertThat(inventoryItemEntity.getDetails(), is(expectedDetails));
        assertThat(inventoryItemEntity.getCharacterEntity(), is(expectedCharacter));
        assertThat(inventoryItemEntity.getItemEntity(), is(expectedItem));
    }

    @Test
    void create_withCharges_nullDetails_withCharacter_withItem() {
        final InventoryItem inventoryItem = new InventoryItem(RANDOM.nextInt(), expectedCharges, null);
        final InventoryItemEntity inventoryItemEntity = new InventoryItemEntity(inventoryItem, expectedCharacter, expectedItem);

        assertThat(inventoryItemEntity.getId(), is(nullValue()));
        assertThat(inventoryItemEntity.getCharges(), is(expectedCharges));
        assertThat(inventoryItemEntity.getDetails(), is(nullValue()));
        assertThat(inventoryItemEntity.getCharacterEntity(), is(expectedCharacter));
        assertThat(inventoryItemEntity.getItemEntity(), is(expectedItem));
    }

    @Test
    void create_withCharges_withDetails_nullCharacter_withItem() {
        final InventoryItem inventoryItem = new InventoryItem(RANDOM.nextInt(), expectedCharges, expectedDetails);
        assertThrows(
                IllegalArgumentException.class,
                () -> new InventoryItemEntity(inventoryItem, null, expectedItem)
        );
    }

    @Test
    void create_withCharges_withDetails_withCharacter_nullItem() {
        final InventoryItem inventoryItem = new InventoryItem(RANDOM.nextInt(), expectedCharges, expectedDetails);
        assertThrows(
                IllegalArgumentException.class,
                () -> new InventoryItemEntity(inventoryItem, expectedCharacter, null)
        );
    }
}