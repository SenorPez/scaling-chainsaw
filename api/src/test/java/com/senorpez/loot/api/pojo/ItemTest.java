package com.senorpez.loot.api.pojo;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import org.hamcrest.Matcher;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.closeTo;
import static org.junit.jupiter.api.Assertions.assertThrows;


class ItemTest {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Test
    void serialize_StringName() throws JsonProcessingException {
        final String json = "{\"name\": \"Gold Piece\", \"weight\": 3.14}";
        final Item item = OBJECT_MAPPER.readValue(json, Item.class);
        final String expectedNameValue = "Gold Piece";
        final BigDecimal expectedWeightValue = BigDecimal.valueOf(3.14);
        assertThat(item.getName(), is(expectedNameValue));
        assertThat(item.getWeight(), closeTo(expectedWeightValue, BigDecimal.valueOf(0.01)));
    }

    @Test
    void serialize_StringWeight() throws JsonProcessingException {
        final String json = "{\"name\": \"Gold Piece\", \"weight\": \"3.14\"}";
        final Item item = OBJECT_MAPPER.readValue(json, Item.class);
        final String expectedNameValue = "Gold Piece";
        final BigDecimal expectedWeightValue = BigDecimal.valueOf(3.14);
        assertThat(item.getName(), is(expectedNameValue));
        assertThat(item.getWeight(), closeTo(expectedWeightValue, BigDecimal.valueOf(0.01)));
    }

    @Test
    void serialize_NumberName() throws JsonProcessingException {
        final String json = "{\"name\": 8675309, \"weight\": 3.14}";
        final Item item = OBJECT_MAPPER.readValue(json, Item.class);
        final String expectedNameValue = "8675309";
        final BigDecimal expectedWeightValue = BigDecimal.valueOf(3.14);
        assertThat(item.getName(), is(expectedNameValue));
        assertThat(item.getWeight(), closeTo(expectedWeightValue, BigDecimal.valueOf(0.01)));
    }

    @Test
    void serialize_NumberWeight() throws JsonProcessingException {
        final String json = "{\"name\": \"Gold Piece\", \"weight\": 3.14}";
        final Item item = OBJECT_MAPPER.readValue(json, Item.class);
        final String expectedNameValue = "Gold Piece";
        final BigDecimal expectedWeightValue = BigDecimal.valueOf(3.14);
        assertThat(item.getName(), is(expectedNameValue));
        assertThat(item.getWeight(), closeTo(expectedWeightValue, BigDecimal.valueOf(0.01)));
    }

    @Test
    void serialize_ObjectName() {
        final String json = "{\"name\": {\"key\": \"value\"}, \"weight\": 3.14}";
        Exception exception = assertThrows(
                MismatchedInputException.class,
                () -> OBJECT_MAPPER.readValue(json, Item.class));
        final Class<MismatchedInputException> expectedValue = MismatchedInputException.class;
        assertThat(exception.getClass(), is(expectedValue));
    }

    @Test
    void serialize_ObjectWeight() {
        final String json = "{\"name\": \"Gold Piece\", \"weight\": {\"key\": \"value\"}}";
        Exception exception = assertThrows(
                MismatchedInputException.class,
                () -> OBJECT_MAPPER.readValue(json, Item.class));
        final Class<MismatchedInputException> expectedValue = MismatchedInputException.class;
        assertThat(exception.getClass(), is(expectedValue));
    }

    @Test
    void serialize_ArrayName() {
        final String json = "{\"name\": [1, 2, 3, 4, 5], \"weight\": 3.14}";
        Exception exception = assertThrows(
                MismatchedInputException.class,
                () -> OBJECT_MAPPER.readValue(json, Item.class));
        final Class<MismatchedInputException> expectedValue = MismatchedInputException.class;
        assertThat(exception.getClass(), is(expectedValue));
    }

    @Test
    void serialize_ArrayWeight() {
        final String json = "{\"name\": \"Gold Piece\", \"weight\": [1, 2, 3, 4, 5]}";
        Exception exception = assertThrows(
                MismatchedInputException.class,
                () -> OBJECT_MAPPER.readValue(json, Item.class));
        final Class<MismatchedInputException> expectedValue = MismatchedInputException.class;
        assertThat(exception.getClass(), is(expectedValue));
    }

    @Test
    void serialize_BooleanName() throws JsonProcessingException {
        final String json = "{\"name\": true, \"weight\": 3.14}";
        final Item item = OBJECT_MAPPER.readValue(json, Item.class);
        final String expectedNameValue = "true";
        final BigDecimal expectedWeightValue = BigDecimal.valueOf(3.14);
        assertThat(item.getName(), is(expectedNameValue));
        assertThat(item.getWeight(), closeTo(expectedWeightValue, BigDecimal.valueOf(0.01)));
    }

    @Test
    void serialize_BooleanWeight() {
        final String json = "{\"name\": \"Gold Piece\", \"weight\": true}";
        Exception exception = assertThrows(
                MismatchedInputException.class,
                () -> OBJECT_MAPPER.readValue(json, Item.class));
        final Class<MismatchedInputException> expectedValue = MismatchedInputException.class;
        assertThat(exception.getClass(), is(expectedValue));
    }

    @Test
    void serialize_NullName() throws JsonProcessingException {
        final String json = "{\"name\": null, \"weight\": 3.14}";
        final Item item = OBJECT_MAPPER.readValue(json, Item.class);
        final Matcher<Object> expectedNameValue = nullValue();
        final BigDecimal expectedWeightValue = BigDecimal.valueOf(3.14);
        assertThat(item.getName(), is(expectedNameValue));
        assertThat(item.getWeight(), closeTo(expectedWeightValue, BigDecimal.valueOf(0.01)));
    }

    @Test
    void serialize_NullWeight() throws JsonProcessingException {
        final String json = "{\"name\": \"Gold Piece\", \"weight\": null}";
        final Item item = OBJECT_MAPPER.readValue(json, Item.class);
        final String expectedNameValue = "Gold Piece";
        final Matcher<Object> expectedWeightValue = nullValue();
        assertThat(item.getName(), is(expectedNameValue));
        assertThat(item.getWeight(), is(expectedWeightValue));
    }

    @Test
    void serialize_InvalidJSON() {
        final String json = "{\"name:\" \"Invalid";
        Exception exception = assertThrows(
                JsonParseException.class,
                () -> OBJECT_MAPPER.readValue(json, Item.class));
        final Class<JsonParseException> expectedValue = JsonParseException.class;
        assertThat(exception.getClass(), is(expectedValue));
    }

    @Test
    void serialize_ExtraFields() throws JsonProcessingException {
        final String json = "{\"name\": \"Gold Piece\", \"weight\": 3.14, \"extra\": \"field\"}";
        final Item item = OBJECT_MAPPER.readValue(json, Item.class);
        final String expectedNameValue = "Gold Piece";
        final BigDecimal expectedWeightValue = BigDecimal.valueOf(3.14);
        assertThat(item.getName(), is(expectedNameValue));
        assertThat(item.getWeight(), closeTo(expectedWeightValue, BigDecimal.valueOf(0.01)));
    }

    @Test
    void serialize_NoMatchingField() throws JsonProcessingException {
        final String json = "{\"incorrect\": \"field\", \"extra\": \"field\"}";
        final Item item = OBJECT_MAPPER.readValue(json, Item.class);
        final Matcher<Object> expectedNameValue = nullValue();
        final Matcher<Object> expectedWeightValue = nullValue();
        assertThat(item.getName(), is(expectedNameValue));
        assertThat(item.getWeight(), is(expectedWeightValue));
    }
}