package com.senorpez.loot.api.entity;

import com.senorpez.loot.api.pojo.Item;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Random;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ItemEntityTest {
    private final Random random = new Random();

    @Test
    void create_withName_withWeight() {
        final String expectedName = "Gold Piece";
        final BigDecimal expectedWeight = BigDecimal.valueOf(random.nextDouble() * 10);
        final Item item = new Item(expectedName, expectedWeight);
        final ItemEntity itemEntity = new ItemEntity(item);

        assertThat(itemEntity.getId(), nullValue());
        assertThat(itemEntity.getName(), is(expectedName));
        assertThat(itemEntity.getWeight(), is(expectedWeight));
    }

    @Test
    void create_nullName_withWeight() {
        final BigDecimal expectedWeight = BigDecimal.valueOf(random.nextDouble() * 10);
        final Item item = new Item(null, expectedWeight);
        Exception exception = assertThrows(
                IllegalArgumentException.class,
                () -> new ItemEntity(item)
        );
        final Class<IllegalArgumentException> expectedException = IllegalArgumentException.class;
        assertThat(exception.getClass(), is(expectedException));
    }

    @Test
    void create_withName_nullWeight() {
        final String expectedName = "Gold Piece";
        final Item item = new Item(expectedName, null);
        final ItemEntity itemEntity = new ItemEntity(item);

        assertThat(itemEntity.getId(), nullValue());
        assertThat(itemEntity.getName(), is(expectedName));
        assertThat(itemEntity.getWeight(), nullValue());
    }
}