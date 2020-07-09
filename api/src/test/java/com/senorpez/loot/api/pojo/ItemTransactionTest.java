package com.senorpez.loot.api.pojo;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import org.hamcrest.Matcher;
import org.junit.jupiter.api.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ItemTransactionTest {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Test
    void serialize_StringItemId() throws JsonProcessingException {
        final String json = "{\"item_id\": \"1\", \"quantity\": 42, \"remark\": \"Test Item\"}";
        final ItemTransaction itemTransaction = OBJECT_MAPPER.readValue(json, ItemTransaction.class);
        final Integer expectedItemId = 1;
        final Integer expectedQuantity = 42;
        final String expectedRemark = "Test Item";
        assertThat(itemTransaction.getItem_id(), is(expectedItemId));
        assertThat(itemTransaction.getQuantity(), is(expectedQuantity));
        assertThat(itemTransaction.getRemark(), is(expectedRemark));
    }

    @Test
    void serialize_NumberItemId() throws JsonProcessingException {
        final String json = "{\"item_id\": 1, \"quantity\": 42, \"remark\": \"Test Item\"}";
        final ItemTransaction itemTransaction = OBJECT_MAPPER.readValue(json, ItemTransaction.class);
        final Integer expectedItemId = 1;
        final Integer expectedQuantity = 42;
        final String expectedRemark = "Test Item";
        assertThat(itemTransaction.getItem_id(), is(expectedItemId));
        assertThat(itemTransaction.getQuantity(), is(expectedQuantity));
        assertThat(itemTransaction.getRemark(), is(expectedRemark));
    }

    @Test
    void serialize_ObjectItemId() {
        final String json = "{\"item_id\": {\"key\": \"value\"}, \"quantity\": 42, \"remark\": \"Test Item\"}";
        Exception exception = assertThrows(
                MismatchedInputException.class,
                () -> OBJECT_MAPPER.readValue(json, ItemTransaction.class));
        final Class<MismatchedInputException> expectedValue = MismatchedInputException.class;
        assertThat(exception.getClass(), is(expectedValue));
    }

    @Test
    void serialize_ArrayItemId() {
        final String json = "{\"item_id\": [1, 2, 3], \"quantity\": 42, \"remark\": \"Test Item\"}";
        Exception exception = assertThrows(
                MismatchedInputException.class,
                () -> OBJECT_MAPPER.readValue(json, ItemTransaction.class));
        final Class<MismatchedInputException> expectedValue = MismatchedInputException.class;
        assertThat(exception.getClass(), is(expectedValue));
    }

    @Test
    void serialize_BooleanItemId() {
        final String json = "{\"item_id\": true, \"quantity\": 42, \"remark\": \"Test Item\"}";
        Exception exception = assertThrows(
                MismatchedInputException.class,
                () -> OBJECT_MAPPER.readValue(json, ItemTransaction.class));
        final Class<MismatchedInputException> expectedValue = MismatchedInputException.class;
        assertThat(exception.getClass(), is(expectedValue));
    }

    @Test
    void serialize_NullItemId() throws JsonProcessingException {
        final String json = "{\"item_id\": null, \"quantity\": 42, \"remark\": \"Test Item\"}";
        final ItemTransaction itemTransaction = OBJECT_MAPPER.readValue(json, ItemTransaction.class);
        final Matcher<Object> expectedItemId = nullValue();
        final Integer expectedQuantity = 42;
        final String expectedRemark = "Test Item";
        assertThat(itemTransaction.getItem_id(), is(expectedItemId));
        assertThat(itemTransaction.getQuantity(), is(expectedQuantity));
        assertThat(itemTransaction.getRemark(), is(expectedRemark));
    }

    @Test
    void serialize_StringQuantity() throws JsonProcessingException {
        final String json = "{\"item_id\": 1, \"quantity\": \"42\", \"remark\": \"Test Item\"}";
        final ItemTransaction itemTransaction = OBJECT_MAPPER.readValue(json, ItemTransaction.class);
        final Integer expectedItemId = 1;
        final Integer expectedQuantity = 42;
        final String expectedRemark = "Test Item";
        assertThat(itemTransaction.getItem_id(), is(expectedItemId));
        assertThat(itemTransaction.getQuantity(), is(expectedQuantity));
        assertThat(itemTransaction.getRemark(), is(expectedRemark));
    }

    @Test
    void serialize_NumberQuantity() throws JsonProcessingException {
        final String json = "{\"item_id\": 1, \"quantity\": 42, \"remark\": \"Test Item\"}";
        final ItemTransaction itemTransaction = OBJECT_MAPPER.readValue(json, ItemTransaction.class);
        final Integer expectedItemId = 1;
        final Integer expectedQuantity = 42;
        final String expectedRemark = "Test Item";
        assertThat(itemTransaction.getItem_id(), is(expectedItemId));
        assertThat(itemTransaction.getQuantity(), is(expectedQuantity));
        assertThat(itemTransaction.getRemark(), is(expectedRemark));
    }

    @Test
    void serialize_ObjectQuantity() {
        final String json = "{\"item_id\": 1, \"quantity\": {\"key\": \"value\"}, \"remark\": \"Test Item\"}";
        Exception exception = assertThrows(
                MismatchedInputException.class,
                () -> OBJECT_MAPPER.readValue(json, ItemTransaction.class));
        final Class<MismatchedInputException> expectedValue = MismatchedInputException.class;
        assertThat(exception.getClass(), is(expectedValue));
    }

    @Test
    void serialize_ArrayQuantity() {
        final String json = "{\"item_id\": 1, \"quantity\": [1, 2, 3], \"remark\": \"Test Item\"}";
        Exception exception = assertThrows(
                MismatchedInputException.class,
                () -> OBJECT_MAPPER.readValue(json, ItemTransaction.class));
        final Class<MismatchedInputException> expectedValue = MismatchedInputException.class;
        assertThat(exception.getClass(), is(expectedValue));
    }

    @Test
    void serialize_BooleanQuantity() {
        final String json = "{\"item_id\": 1, \"quantity\": true, \"remark\": \"Test Item\"}";
        Exception exception = assertThrows(
                MismatchedInputException.class,
                () -> OBJECT_MAPPER.readValue(json, ItemTransaction.class));
        final Class<MismatchedInputException> expectedValue = MismatchedInputException.class;
        assertThat(exception.getClass(), is(expectedValue));
    }

    @Test
    void serialize_NullQuantity() throws JsonProcessingException {
        final String json = "{\"item_id\": 1, \"quantity\": null, \"remark\": \"Test Item\"}";
        final ItemTransaction itemTransaction = OBJECT_MAPPER.readValue(json, ItemTransaction.class);
        final Integer expectedItemId = 1;
        final Matcher<Object> expectedQuantity = nullValue();
        final String expectedRemark = "Test Item";
        assertThat(itemTransaction.getItem_id(), is(expectedItemId));
        assertThat(itemTransaction.getQuantity(), is(expectedQuantity));
        assertThat(itemTransaction.getRemark(), is(expectedRemark));
    }

    @Test
    void serialize_StringRemark() throws JsonProcessingException {
        final String json = "{\"item_id\": 1, \"quantity\": 42, \"remark\": \"Test Item\"}";
        final ItemTransaction itemTransaction = OBJECT_MAPPER.readValue(json, ItemTransaction.class);
        final Integer expectedItemId = 1;
        final Integer expectedQuantity = 42;
        final String expectedRemark = "Test Item";
        assertThat(itemTransaction.getItem_id(), is(expectedItemId));
        assertThat(itemTransaction.getQuantity(), is(expectedQuantity));
        assertThat(itemTransaction.getRemark(), is(expectedRemark));
    }

    @Test
    void serialize_NumberRemark() throws JsonProcessingException {
        final String json = "{\"item_id\": 1, \"quantity\": 42, \"remark\": 8675309}";
        final ItemTransaction itemTransaction = OBJECT_MAPPER.readValue(json, ItemTransaction.class);
        final Integer expectedItemId = 1;
        final Integer expectedQuantity = 42;
        final String expectedRemark = "8675309";
        assertThat(itemTransaction.getItem_id(), is(expectedItemId));
        assertThat(itemTransaction.getQuantity(), is(expectedQuantity));
        assertThat(itemTransaction.getRemark(), is(expectedRemark));
    }

    @Test
    void serialize_ObjectRemark() {
        final String json = "{\"item_id\": 1, \"quantity\": 42, \"remark\": {\"key\": \"value\"}}";
        Exception exception = assertThrows(
                MismatchedInputException.class,
                () -> OBJECT_MAPPER.readValue(json, ItemTransaction.class));
        final Class<MismatchedInputException> expectedValue = MismatchedInputException.class;
        assertThat(exception.getClass(), is(expectedValue));
    }

    @Test
    void serialize_ArrayRemark() {
        final String json = "{\"item_id\": 1, \"quantity\": 42, \"remark\": [1, 2, 3]}";
        Exception exception = assertThrows(
                MismatchedInputException.class,
                () -> OBJECT_MAPPER.readValue(json, ItemTransaction.class));
        final Class<MismatchedInputException> expectedValue = MismatchedInputException.class;
        assertThat(exception.getClass(), is(expectedValue));
    }

    @Test
    void serialize_BooleanRemark() throws JsonProcessingException {
        final String json = "{\"item_id\": 1, \"quantity\": 42, \"remark\": true}";
        final ItemTransaction itemTransaction = OBJECT_MAPPER.readValue(json, ItemTransaction.class);
        final Integer expectedItemId = 1;
        final Integer expectedQuantity = 42;
        final String expectedRemark = "true";
        assertThat(itemTransaction.getItem_id(), is(expectedItemId));
        assertThat(itemTransaction.getQuantity(), is(expectedQuantity));
        assertThat(itemTransaction.getRemark(), is(expectedRemark));
    }

    @Test
    void serialize_NullRemark() throws JsonProcessingException {
        final String json = "{\"item_id\": 1, \"quantity\": 42, \"remark\": null}";
        final ItemTransaction itemTransaction = OBJECT_MAPPER.readValue(json, ItemTransaction.class);
        final Integer expectedItemId = 1;
        final Integer expectedQuantity = 42;
        final Matcher<Object> expectedRemark = nullValue();
        assertThat(itemTransaction.getItem_id(), is(expectedItemId));
        assertThat(itemTransaction.getQuantity(), is(expectedQuantity));
        assertThat(itemTransaction.getRemark(), is(expectedRemark));
    }


    @Test
    void serialize_InvalidJSON() {
        final String json = "{\"name:\" \"Invalid";
        Exception exception = assertThrows(
                JsonParseException.class,
                () -> OBJECT_MAPPER.readValue(json, ItemTransaction.class));
        final Class<JsonParseException> expectedValue = JsonParseException.class;
        assertThat(exception.getClass(), is(expectedValue));
    }

    @Test
    void serialize_ExtraFields() throws JsonProcessingException {
        final String json = "{\"item_id\": 1, \"quantity\": 42, \"remark\": \"Test Item\", \"extra\": \"field\"}";
        final ItemTransaction itemTransaction = OBJECT_MAPPER.readValue(json, ItemTransaction.class);
        final Integer expectedItemId = 1;
        final Integer expectedQuantity = 42;
        final String expectedRemark = "Test Item";
        assertThat(itemTransaction.getItem_id(), is(expectedItemId));
        assertThat(itemTransaction.getQuantity(), is(expectedQuantity));
        assertThat(itemTransaction.getRemark(), is(expectedRemark));
    }

    @Test
    void serialize_NoMatchingField() throws JsonProcessingException {
        final String json = "{\"incorrect\": \"field\", \"extra\": \"field\"}";
        final ItemTransaction itemTransaction = OBJECT_MAPPER.readValue(json, ItemTransaction.class);
        final Matcher<Object> expectedItemId = nullValue();
        final Matcher<Object> expectedQuantity = nullValue();
        final Matcher<Object> expectedRemark = nullValue();
        assertThat(itemTransaction.getItem_id(), is(expectedItemId));
        assertThat(itemTransaction.getQuantity(), is(expectedQuantity));
        assertThat(itemTransaction.getRemark(), is(expectedRemark));
    }
}