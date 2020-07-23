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


class InventoryItemTest {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Test
    void serialize_StringCharges() throws JsonProcessingException {
        final String json = "{\"name\": \"Gold Piece\", \"weight\": 3.14, \"charges\": \"1\", \"details\": \"Special Pieces\"}";
        final InventoryItem item = OBJECT_MAPPER.readValue(json, InventoryItem.class);
        final Integer expectedChargesValue = 1;
        final String expectedDetailsValue = "Special Pieces";
        assertThat(item.getCharges(), is(expectedChargesValue));
        assertThat(item.getDetails(), is(expectedDetailsValue));
    }

    @Test
    void serialize_StringDetails() throws JsonProcessingException {
        final String json = "{\"name\": \"Gold Piece\", \"weight\": 3.14, \"charges\": 1, \"details\": \"Special Pieces\"}";
        final InventoryItem item = OBJECT_MAPPER.readValue(json, InventoryItem.class);
        final Integer expectedChargesValue = 1;
        final String expectedDetailsValue = "Special Pieces";
        assertThat(item.getCharges(), is(expectedChargesValue));
        assertThat(item.getDetails(), is(expectedDetailsValue));
    }

    @Test
    void serialize_NumberCharges() throws JsonProcessingException {
        final String json = "{\"name\": \"Gold Piece\", \"weight\": 3.14, \"charges\": 1, \"details\": \"Special Pieces\"}";
        final InventoryItem item = OBJECT_MAPPER.readValue(json, InventoryItem.class);
        final Integer expectedChargesValue = 1;
        final String expectedDetailsValue = "Special Pieces";
        assertThat(item.getCharges(), is(expectedChargesValue));
        assertThat(item.getDetails(), is(expectedDetailsValue));
    }

    @Test
    void serialize_NumberDetails() throws JsonProcessingException {
        final String json = "{\"name\": \"Gold Piece\", \"weight\": 3.14, \"charges\": 1, \"details\": 42}";
        final InventoryItem item = OBJECT_MAPPER.readValue(json, InventoryItem.class);
        final Integer expectedChargesValue = 1;
        final String expectedDetailsValue = "42";
        assertThat(item.getCharges(), is(expectedChargesValue));
        assertThat(item.getDetails(), is(expectedDetailsValue));
    }

    @Test
    void serialize_ObjectCharges() {
        final String json = "{\"name\": \"Gold Piece\", \"weight\": 3.14, \"charges\": {\"key\": \"value\"}, \"details\": \"Special Pieces\"}";
        assertThrows(
                MismatchedInputException.class,
                () -> OBJECT_MAPPER.readValue(json, InventoryItem.class)
        );
    }

    @Test
    void serialize_ObjectDetails() {
        final String json = "{\"name\": \"Gold Piece\", \"weight\": 3.14, \"charges\": 1, \"details\": {\"key\": \"value\"}";
        assertThrows(
                MismatchedInputException.class,
                () -> OBJECT_MAPPER.readValue(json, InventoryItem.class)
        );
    }

    @Test
    void serialize_ArrayCharges() {
        final String json = "{\"name\": \"Gold Piece\", \"weight\": 3.14, \"charges\": [1, 2, 3, 4, 5], \"details\": \"Special Pieces\"}";
        assertThrows(
                MismatchedInputException.class,
                () -> OBJECT_MAPPER.readValue(json, InventoryItem.class)
        );
    }

    @Test
    void serialize_ArrayDetails() {
        final String json = "{\"name\": \"Gold Piece\", \"weight\": 3.14, \"charges\": 1, \"details\": [1, 2, 3, 4, 5]}";
        assertThrows(
                MismatchedInputException.class,
                () -> OBJECT_MAPPER.readValue(json, InventoryItem.class)
        );
    }

    @Test
    void serialize_BooleanCharges() {
        final String json = "{\"name\": \"Gold Piece\", \"weight\": 3.14, \"charges\": true, \"details\": \"Special Pieces\"}";
        assertThrows(
                MismatchedInputException.class,
                () -> OBJECT_MAPPER.readValue(json, InventoryItem.class)
        );
    }

    @Test
    void serialize_BooleanDetails() throws JsonProcessingException {
        final String json = "{\"name\": \"Gold Piece\", \"weight\": 3.14, \"charges\": 1, \"details\": true}";
        final InventoryItem item = OBJECT_MAPPER.readValue(json, InventoryItem.class);
        final Integer expectedChargesValue = 1;
        final String expectedDetailsValue = "true";
        assertThat(item.getCharges(), is(expectedChargesValue));
        assertThat(item.getDetails(), is(expectedDetailsValue));
    }

    @Test
    void serialize_NullCharges() throws JsonProcessingException {
        final String json = "{\"name\": \"Gold Piece\", \"weight\": 3.14, \"charges\": null, \"details\": \"Special Pieces\"}";
        final InventoryItem item = OBJECT_MAPPER.readValue(json, InventoryItem.class);
        final Matcher<Object> expectedChargesValue = nullValue();
        final String expectedDetailsValue = "Special Pieces";
        assertThat(item.getCharges(), is(expectedChargesValue));
        assertThat(item.getDetails(), is(expectedDetailsValue));
    }

    @Test
    void serialize_NullDetails() throws JsonProcessingException {
        final String json = "{\"name\": \"Gold Piece\", \"weight\": 3.14, \"charges\": 1, \"details\": null}";
        final InventoryItem item = OBJECT_MAPPER.readValue(json, InventoryItem.class);
        final Integer expectedChargesValue = 1;
        final Matcher<Object> expectedDetailsValue = nullValue();
        assertThat(item.getCharges(), is(expectedChargesValue));
        assertThat(item.getDetails(), is(expectedDetailsValue));
    }

    @Test
    void serialize_InvalidJSON() {
        final String json = "{\"name:\" \"Invalid";
        assertThrows(
                JsonParseException.class,
                () -> OBJECT_MAPPER.readValue(json, Item.class)
        );
    }

    @Test
    void serialize_ExtraFields() throws JsonProcessingException {
        final String json = "{\"name\": \"Gold Piece\", \"weight\": 3.14, \"charges\": 1, \"details\": \"Special Pieces\", \"key\": \"value\"}";
        final InventoryItem item = OBJECT_MAPPER.readValue(json, InventoryItem.class);
        final Integer expectedChargesValue = 1;
        final String expectedDetailsValue = "Special Pieces";
        assertThat(item.getCharges(), is(expectedChargesValue));
        assertThat(item.getDetails(), is(expectedDetailsValue));
    }

    @Test
    void serialize_NoMatchingField() throws JsonProcessingException {
        final String json = "{\"incorrect\": \"field\", \"extra\": \"field\"}";
        final InventoryItem item = OBJECT_MAPPER.readValue(json, InventoryItem.class);
        final Matcher<Object> expectedChargesValue = nullValue();
        final Matcher<Object> expectedDetailsValue = nullValue();
        assertThat(item.getCharges(), is(expectedChargesValue));
        assertThat(item.getDetails(), is(expectedDetailsValue));
    }
}
