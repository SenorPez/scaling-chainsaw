package com.senorpez.loot.api.entity;

import com.senorpez.loot.api.pojo.Character;
import com.senorpez.loot.api.pojo.InventoryItem;
import com.senorpez.loot.api.pojo.Item;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class InventoryItemEntityTest {
    private final Random random = new Random();
    private static final CharacterEntity expectedCharacter = new CharacterEntity(
            new Character("Aethelwuf"),
            new CampaignEntity()
    );
    private static final ItemEntity expectedItem = new ItemEntity(
            new Item("Gold Piece", null)
    );

    @Test
    void create_withCharges_withDetails_withCharacter_withItem() {
        final Integer expectedCharges = random.nextInt();
        final String expectedDetails = "Special Item";
        final InventoryItem inventoryItem = new InventoryItem("Gold Piece", null, expectedCharges, expectedDetails);
        final InventoryItemEntity inventoryItemEntity = new InventoryItemEntity(inventoryItem, expectedCharacter, expectedItem);

        assertThat(inventoryItemEntity.getId(), nullValue());
        assertThat(inventoryItemEntity.getCharges(), is(expectedCharges));
        assertThat(inventoryItemEntity.getDetails(), is(expectedDetails));
        assertThat(inventoryItemEntity.getCharacterEntity(), is(expectedCharacter));
        assertThat(inventoryItemEntity.getItemEntity(), is(expectedItem));
    }

    @Test
    void create_nullCharges_withDetails_withCharacter_withItem() {
        final String expectedDetails = "Special Item";
        final InventoryItem inventoryItem = new InventoryItem("Gold Piece", null, null, expectedDetails);
        final InventoryItemEntity inventoryItemEntity = new InventoryItemEntity(inventoryItem, expectedCharacter, expectedItem);

        assertThat(inventoryItemEntity.getId(), nullValue());
        assertThat(inventoryItemEntity.getCharges(), nullValue());
        assertThat(inventoryItemEntity.getDetails(), is(expectedDetails));
        assertThat(inventoryItemEntity.getCharacterEntity(), is(expectedCharacter));
        assertThat(inventoryItemEntity.getItemEntity(), is(expectedItem));
    }

    @Test
    void create_withCharges_nullDetails_withCharacter_withItem() {
        final Integer expectedCharges = random.nextInt();
        final InventoryItem inventoryItem = new InventoryItem("Gold Piece", null, expectedCharges, null);
        final InventoryItemEntity inventoryItemEntity = new InventoryItemEntity(inventoryItem, expectedCharacter, expectedItem);

        assertThat(inventoryItemEntity.getId(), nullValue());
        assertThat(inventoryItemEntity.getCharges(), is(expectedCharges));
        assertThat(inventoryItemEntity.getDetails(), nullValue());
        assertThat(inventoryItemEntity.getCharacterEntity(), is(expectedCharacter));
        assertThat(inventoryItemEntity.getItemEntity(), is(expectedItem));
    }

    @Test
    void create_withCharges_withDetails_nullCharacter_withItem() {
        final Integer expectedCharges = random.nextInt();
        final String expectedDetails = "Special Item";
        final InventoryItem inventoryItem = new InventoryItem("Gold Piece", null, expectedCharges, expectedDetails);

        assertThrows(
                IllegalArgumentException.class,
                () -> new InventoryItemEntity(inventoryItem, null, expectedItem)
        );
    }

    @Test
    void create_withCharges_withDetails_withCharacter_nullItem() {
        final Integer expectedCharges = random.nextInt();
        final String expectedDetails = "Special Item";
        final InventoryItem inventoryItem = new InventoryItem("Gold Piece", null, expectedCharges, expectedDetails);

        assertThrows(
                IllegalArgumentException.class,
                () -> new InventoryItemEntity(inventoryItem, expectedCharacter, null)
        );
    }
}