package com.senorpez.loot.api.pojo;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ItemTransactionTest {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final Random RANDOM = new Random();
    private static Integer expectedId;
    private static Integer expectedQuantity;
    private static String expectedRemark;

    @BeforeEach
    void setUp() {
        expectedId = RANDOM.nextInt();
        expectedQuantity = RANDOM.nextInt();
        expectedRemark = "Session 69 Loot";
    }

    @Test
    void serialize_StringItemId() throws JsonProcessingException {
        final String json = String.format(
                "{\"inv_item_id\": \"%s\", \"quantity\": %d, \"remark\": \"%s\"}",
                expectedId.toString(),
                expectedQuantity,
                expectedRemark
        );
        final ItemTransaction itemTransaction = OBJECT_MAPPER.readValue(json, ItemTransaction.class);
        assertThat(itemTransaction.getInventoryItemId(), is(expectedId));
        assertThat(itemTransaction.getQuantity(), is(expectedQuantity));
        assertThat(itemTransaction.getRemark(), is(expectedRemark));
    }

    @Test
    void serialize_NumberItemId() throws JsonProcessingException {
        final String json = String.format(
                "{\"inv_item_id\": %d, \"quantity\": %d, \"remark\": \"%s\"}",
                expectedId,
                expectedQuantity,
                expectedRemark
        );
        final ItemTransaction itemTransaction = OBJECT_MAPPER.readValue(json, ItemTransaction.class);
        assertThat(itemTransaction.getInventoryItemId(), is(expectedId));
        assertThat(itemTransaction.getQuantity(), is(expectedQuantity));
        assertThat(itemTransaction.getRemark(), is(expectedRemark));
    }

    @Test
    void serialize_ObjectItemId() {
        final String expectedId = "{\"key\": \"value\"}";
        final String json = String.format(
                "{\"inv_item_id\": %s, \"quantity\": %d, \"remark\": \"%s\"}",
                expectedId,
                expectedQuantity,
                expectedRemark
        );
        assertThrows(
                MismatchedInputException.class,
                () -> OBJECT_MAPPER.readValue(json, ItemTransaction.class)
        );
    }

    @Test
    void serialize_ArrayItemId() {
        final String expectedId = "[1, 2, 3, 4, 5]";
        final String json = String.format(
                "{\"inv_item_id\": %s, \"quantity\": %d, \"remark\": \"%s\"}",
                expectedId,
                expectedQuantity,
                expectedRemark
        );
        assertThrows(
                MismatchedInputException.class,
                () -> OBJECT_MAPPER.readValue(json, ItemTransaction.class)
        );
    }

    @Test
    void serialize_BooleanItemId() {
        final Boolean expectedId = RANDOM.nextBoolean();
        final String json = String.format(
                "{\"inv_item_id\": %s, \"quantity\": %d, \"remark\": \"%s\"}",
                expectedId,
                expectedQuantity,
                expectedRemark
        );
        assertThrows(
                MismatchedInputException.class,
                () -> OBJECT_MAPPER.readValue(json, ItemTransaction.class)
        );
    }

    @Test
    void serialize_NullItemId() throws JsonProcessingException {
        final String json = String.format(
                "{\"inv_item_id\": %s, \"quantity\": %d, \"remark\": \"%s\"}",
                null,
                expectedQuantity,
                expectedRemark
        );
        final ItemTransaction itemTransaction = OBJECT_MAPPER.readValue(json, ItemTransaction.class);
        assertThat(itemTransaction.getInventoryItemId(), is(nullValue()));
        assertThat(itemTransaction.getQuantity(), is(expectedQuantity));
        assertThat(itemTransaction.getRemark(), is(expectedRemark));
    }

    @Test
    void serialize_StringQuantity() throws JsonProcessingException {
        final String json = String.format(
                "{\"inv_item_id\": %d, \"quantity\": \"%s\", \"remark\": \"%s\"}",
                expectedId,
                expectedQuantity.toString(),
                expectedRemark
        );
        final ItemTransaction itemTransaction = OBJECT_MAPPER.readValue(json, ItemTransaction.class);
        assertThat(itemTransaction.getInventoryItemId(), is(expectedId));
        assertThat(itemTransaction.getQuantity(), is(expectedQuantity));
        assertThat(itemTransaction.getRemark(), is(expectedRemark));
    }

    @Test
    void serialize_NumberQuantity() throws JsonProcessingException {
        final String json = String.format(
                "{\"inv_item_id\": %d, \"quantity\": %d, \"remark\": \"%s\"}",
                expectedId,
                expectedQuantity,
                expectedRemark
        );
        final ItemTransaction itemTransaction = OBJECT_MAPPER.readValue(json, ItemTransaction.class);
        assertThat(itemTransaction.getInventoryItemId(), is(expectedId));
        assertThat(itemTransaction.getQuantity(), is(expectedQuantity));
        assertThat(itemTransaction.getRemark(), is(expectedRemark));
    }

    @Test
    void serialize_ObjectQuantity() {
        final String expectedQuantity = "{\"key\": \"value\"}";
        final String json = String.format(
                "{\"inv_item_id\": %d, \"quantity\": %s, \"remark\": \"%s\"}",
                expectedId,
                expectedQuantity,
                expectedRemark
        );
        assertThrows(
                MismatchedInputException.class,
                () -> OBJECT_MAPPER.readValue(json, ItemTransaction.class)
        );
    }

    @Test
    void serialize_ArrayQuantity() {
        final String expectedQuantity = "[1, 2, 3, 4, 5]";
        final String json = String.format(
                "{\"inv_item_id\": %d, \"quantity\": %s, \"remark\": \"%s\"}",
                expectedId,
                expectedQuantity,
                expectedRemark
        );
        assertThrows(
                MismatchedInputException.class,
                () -> OBJECT_MAPPER.readValue(json, ItemTransaction.class)
        );
    }

    @Test
    void serialize_BooleanQuantity() {
        final Boolean expectedQuantity = RANDOM.nextBoolean();
        final String json = String.format(
                "{\"inv_item_id\": %d, \"quantity\": %s, \"remark\": \"%s\"}",
                expectedId,
                expectedQuantity,
                expectedRemark
        );
        assertThrows(
                MismatchedInputException.class,
                () -> OBJECT_MAPPER.readValue(json, ItemTransaction.class)
        );
    }

    @Test
    void serialize_NullQuantity() throws JsonProcessingException {
        final String json = String.format(
                "{\"inv_item_id\": %d, \"quantity\": %s, \"remark\": \"%s\"}",
                expectedId,
                nullValue(),
                expectedRemark
        );
        final ItemTransaction itemTransaction = OBJECT_MAPPER.readValue(json, ItemTransaction.class);
        assertThat(itemTransaction.getInventoryItemId(), is(expectedId));
        assertThat(itemTransaction.getQuantity(), is(nullValue()));
        assertThat(itemTransaction.getRemark(), is(expectedRemark));
    }

    @Test
    void serialize_StringRemark() throws JsonProcessingException {
        final String json = String.format(
                "{\"inv_item_id\": %d, \"quantity\": %d, \"remark\": \"%s\"}",
                expectedId,
                expectedQuantity,
                expectedRemark
        );
        final ItemTransaction itemTransaction = OBJECT_MAPPER.readValue(json, ItemTransaction.class);
        assertThat(itemTransaction.getInventoryItemId(), is(expectedId));
        assertThat(itemTransaction.getQuantity(), is(expectedQuantity));
        assertThat(itemTransaction.getRemark(), is(expectedRemark));
    }

    @Test
    void serialize_NumberRemark() throws JsonProcessingException {
        final String expectedRemark = String.valueOf(RANDOM.nextInt());
        final String json = String.format(
                "{\"inv_item_id\": %d, \"quantity\": %d, \"remark\": %d}",
                expectedId,
                expectedQuantity,
                Integer.valueOf(expectedRemark)
        );
        final ItemTransaction itemTransaction = OBJECT_MAPPER.readValue(json, ItemTransaction.class);
        assertThat(itemTransaction.getInventoryItemId(), is(expectedId));
        assertThat(itemTransaction.getQuantity(), is(expectedQuantity));
        assertThat(itemTransaction.getRemark(), is(expectedRemark));
    }

    @Test
    void serialize_ObjectRemark() {
        final String expectedRemark = "{\"key\": \"value\"}";
        final String json = String.format(
                "{\"inv_item_id\": %d, \"quantity\": %d, \"remark\": %s}",
                expectedId,
                expectedQuantity,
                expectedRemark
        );
        assertThrows(
                MismatchedInputException.class,
                () -> OBJECT_MAPPER.readValue(json, ItemTransaction.class)
        );
    }

    @Test
    void serialize_ArrayRemark() {
        final String expectedRemark = "[1, 2, 3, 4, 5]";
        final String json = String.format(
                "{\"inv_item_id\": %d, \"quantity\": %d, \"remark\": %s}",
                expectedId,
                expectedQuantity,
                expectedRemark
        );
        assertThrows(
                MismatchedInputException.class,
                () -> OBJECT_MAPPER.readValue(json, ItemTransaction.class)
        );
    }

    @Test
    void serialize_BooleanRemark() throws JsonProcessingException {
        final String expectedRemark = String.valueOf(RANDOM.nextBoolean());
        final String json = String.format(
                "{\"inv_item_id\": %d, \"quantity\": %d, \"remark\": %s}",
                expectedId,
                expectedQuantity,
                Boolean.valueOf(expectedRemark)
        );
        final ItemTransaction itemTransaction = OBJECT_MAPPER.readValue(json, ItemTransaction.class);
        assertThat(itemTransaction.getInventoryItemId(), is(expectedId));
        assertThat(itemTransaction.getQuantity(), is(expectedQuantity));
        assertThat(itemTransaction.getRemark(), is(expectedRemark));
    }

    @Test
    void serialize_NullRemark() throws JsonProcessingException {
        final String json = String.format(
                "{\"inv_item_id\": %d, \"quantity\": %d, \"remark\": %s}",
                expectedId,
                expectedQuantity,
                null
        );
        final ItemTransaction itemTransaction = OBJECT_MAPPER.readValue(json, ItemTransaction.class);
        assertThat(itemTransaction.getInventoryItemId(), is(expectedId));
        assertThat(itemTransaction.getQuantity(), is(expectedQuantity));
        assertThat(itemTransaction.getRemark(), is(nullValue()));
    }


    @Test
    void serialize_InvalidJSON() {
        final String json = "{\"name:\" \"Invalid";
        assertThrows(
                JsonParseException.class,
                () -> OBJECT_MAPPER.readValue(json, ItemTransaction.class)
        );
    }

    @Test
    void serialize_ExtraFields() throws JsonProcessingException {
        final String json = String.format(
                "{\"inv_item_id\": %d, \"quantity\": %d, \"remark\": \"%s\", \"extra\": \"field\"}",
                expectedId,
                expectedQuantity,
                expectedRemark
        );
        final ItemTransaction itemTransaction = OBJECT_MAPPER.readValue(json, ItemTransaction.class);
        assertThat(itemTransaction.getInventoryItemId(), is(expectedId));
        assertThat(itemTransaction.getQuantity(), is(expectedQuantity));
        assertThat(itemTransaction.getRemark(), is(expectedRemark));
    }

    @Test
    void serialize_NoMatchingField() throws JsonProcessingException {
        final String json = "{\"key\": \"value\", \"extra\": \"field\"}";
        final ItemTransaction itemTransaction = OBJECT_MAPPER.readValue(json, ItemTransaction.class);
        assertThat(itemTransaction.getInventoryItemId(), is(nullValue()));
        assertThat(itemTransaction.getQuantity(), is(nullValue()));
        assertThat(itemTransaction.getRemark(), is(nullValue()));
    }
}