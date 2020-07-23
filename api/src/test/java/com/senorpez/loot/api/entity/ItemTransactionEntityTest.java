package com.senorpez.loot.api.entity;

import com.senorpez.loot.api.pojo.InventoryItem;
import com.senorpez.loot.api.pojo.ItemTransaction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.Random;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ItemTransactionEntityTest {
    private final Random RANDOM = new Random();
    private static InventoryItemEntity expectedInventoryItem;
    private static Integer expectedQuantity;
    private static String expectedRemark;

    @BeforeEach
    void setUp() {
        expectedQuantity = RANDOM.nextInt();
        expectedRemark = String.format("Session %d Loot", RANDOM.nextInt());
        expectedInventoryItem = new InventoryItemEntity(
                new InventoryItem(
                        RANDOM.nextInt(),
                        RANDOM.nextInt(),
                        "Special Item"
                ),
                new CharacterEntity(),
                new ItemEntity()
        );
    }

    @Test
    void create_withQuantity_withRemark_withInvItem() {
        final ItemTransaction itemTransaction = new ItemTransaction(RANDOM.nextInt(), expectedQuantity, expectedRemark);
        final ItemTransactionEntity itemTransactionEntity = new ItemTransactionEntity(itemTransaction, expectedInventoryItem);

        assertThat(itemTransactionEntity.getId(), is(nullValue()));
        assertThat(itemTransactionEntity.getQuantity(), is(expectedQuantity));
        assertThat(itemTransactionEntity.getRemark(), is(expectedRemark));
        assertThat(itemTransactionEntity.getDatetime(), isA(Date.class));
        assertThat(itemTransactionEntity.getInventoryItemEntity(), is(expectedInventoryItem));
    }

    @Test
    void create_nullQuantity_withRemark_withInvItem() {
        final ItemTransaction itemTransaction = new ItemTransaction(RANDOM.nextInt(), null, expectedRemark);
        assertThrows(
                IllegalArgumentException.class,
                () -> new ItemTransactionEntity(itemTransaction, expectedInventoryItem)
        );
    }

    @Test
    void create_withQuantity_nullRemark_withInvItem() {
        final ItemTransaction itemTransaction = new ItemTransaction(RANDOM.nextInt(), expectedQuantity, null);
        final ItemTransactionEntity itemTransactionEntity = new ItemTransactionEntity(itemTransaction, expectedInventoryItem);

        assertThat(itemTransactionEntity.getId(), is(nullValue()));
        assertThat(itemTransactionEntity.getQuantity(), is(expectedQuantity));
        assertThat(itemTransactionEntity.getRemark(), is(nullValue()));
        assertThat(itemTransactionEntity.getDatetime(), isA(Date.class));
        assertThat(itemTransactionEntity.getInventoryItemEntity(), is(expectedInventoryItem));
    }

    @Test
    void create_withQuantity_withRemark_nullInvItem() {
        final ItemTransaction itemTransaction = new ItemTransaction(RANDOM.nextInt(), expectedQuantity, expectedRemark);
        assertThrows(
                IllegalArgumentException.class,
                () -> new ItemTransactionEntity(itemTransaction, null)
        );
    }
}