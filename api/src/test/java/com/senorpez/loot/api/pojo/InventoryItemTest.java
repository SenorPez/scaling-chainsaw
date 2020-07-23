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


class InventoryItemTest {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final Random RANDOM = new Random();
    private static Integer expectedId;
    private static Integer expectedCharges;
    private static String expectedDetails;

    @BeforeEach
    void setUp() {
        expectedId = RANDOM.nextInt();
        expectedCharges = RANDOM.nextInt();
        expectedDetails = "Defiance in Phlan";
    }

    @Test
    void serialize_StringItemId() throws JsonProcessingException {
        final String json = String.format(
                "{\"item_id\": \"%s\", \"charges\": %d, \"details\": \"%s\"}",
                expectedId.toString(),
                expectedCharges,
                expectedDetails
        );
        final InventoryItem item = OBJECT_MAPPER.readValue(json, InventoryItem.class);
        assertThat(item.getItemId(), is(expectedId));
        assertThat(item.getCharges(), is(expectedCharges));
        assertThat(item.getDetails(), is(expectedDetails));
    }

    @Test
    void serialize_StringCharges() throws JsonProcessingException {
        final String json = String.format(
                "{\"item_id\": %d, \"charges\": \"%s\", \"details\": \"%s\"}",
                expectedId,
                expectedCharges.toString(),
                expectedDetails
        );
        final InventoryItem item = OBJECT_MAPPER.readValue(json, InventoryItem.class);
        assertThat(item.getItemId(), is(expectedId));
        assertThat(item.getCharges(), is(expectedCharges));
        assertThat(item.getDetails(), is(expectedDetails));
    }

    @Test
    void serialize_StringDetails() throws JsonProcessingException {
        final String json = String.format(
                "{\"item_id\": %d, \"charges\": %d, \"details\": \"%s\"}",
                expectedId,
                expectedCharges,
                expectedDetails
        );
        final InventoryItem item = OBJECT_MAPPER.readValue(json, InventoryItem.class);
        assertThat(item.getItemId(), is(expectedId));
        assertThat(item.getCharges(), is(expectedCharges));
        assertThat(item.getDetails(), is(expectedDetails));
    }

    @Test
    void serialize_NumberId() throws JsonProcessingException {
        final String json = String.format(
                "{\"item_id\": %d, \"charges\": %d, \"details\": \"%s\"}",
                expectedId,
                expectedCharges,
                expectedDetails
        );
        final InventoryItem item = OBJECT_MAPPER.readValue(json, InventoryItem.class);
        assertThat(item.getItemId(), is(expectedId));
        assertThat(item.getCharges(), is(expectedCharges));
        assertThat(item.getDetails(), is(expectedDetails));
    }

    @Test
    void serialize_NumberCharges() throws JsonProcessingException {
        final String json = String.format(
                "{\"item_id\": %d, \"charges\": %d, \"details\": \"%s\"}",
                expectedId,
                expectedCharges,
                expectedDetails
        );
        final InventoryItem item = OBJECT_MAPPER.readValue(json, InventoryItem.class);
        assertThat(item.getItemId(), is(expectedId));
        assertThat(item.getCharges(), is(expectedCharges));
        assertThat(item.getDetails(), is(expectedDetails));
    }

    @Test
    void serialize_NumberDetails() throws JsonProcessingException {
        final String expectedDetails = String.valueOf(RANDOM.nextInt());
        final String json = String.format(
                "{\"item_id\": %d, \"charges\": %d, \"details\": %d}",
                expectedId,
                expectedCharges,
                Integer.valueOf(expectedDetails)
        );
        final InventoryItem item = OBJECT_MAPPER.readValue(json, InventoryItem.class);
        assertThat(item.getItemId(), is(expectedId));
        assertThat(item.getCharges(), is(expectedCharges));
        assertThat(item.getDetails(), is(expectedDetails));
    }

    @Test
    void serialize_ObjectId() {
        final String expectedId = "{\"key\": \"value\"}";
        final String json = String.format(
                "{\"item_id\": %s, \"charges\": %d, \"details\": \"%s\"}",
                expectedId,
                expectedCharges,
                expectedDetails
        );
        assertThrows(
                MismatchedInputException.class,
                () -> OBJECT_MAPPER.readValue(json, InventoryItem.class)
        );
    }

    @Test
    void serialize_ObjectCharges() {
        final String expectedCharges = "{\"key\": \"value\"}";
        final String json = String.format(
                "{\"item_id\": %d, \"charges\": %s, \"details\": \"%s\"}",
                expectedId,
                expectedCharges,
                expectedDetails
        );
        assertThrows(
                MismatchedInputException.class,
                () -> OBJECT_MAPPER.readValue(json, InventoryItem.class)
        );
    }

    @Test
    void serialize_ObjectDetails() {
        final String expectedDetails = "{\"key\": \"value\"}";
        final String json = String.format(
                "{\"item_id\": %d, \"charges\": %d, \"details\": %s}",
                expectedId,
                expectedCharges,
                expectedDetails
        );
        assertThrows(
                MismatchedInputException.class,
                () -> OBJECT_MAPPER.readValue(json, InventoryItem.class)
        );
    }

    @Test
    void serialize_ArrayId() {
        final String expectedId = "[1, 2, 3, 4, 5]";
        final String json = String.format(
                "{\"item_id\": %s, \"charges\": %d, \"details\": \"%s\"}",
                expectedId,
                expectedCharges,
                expectedDetails
        );
        assertThrows(
                MismatchedInputException.class,
                () -> OBJECT_MAPPER.readValue(json, InventoryItem.class)
        );
    }

    @Test
    void serialize_ArrayCharges() {
        final String expectedCharges = "[1, 2, 3, 4, 5]";
        final String json = String.format(
                "{\"item_id\": %d, \"charges\": %s, \"details\": \"%s\"}",
                expectedId,
                expectedCharges,
                expectedDetails
        );
        assertThrows(
                MismatchedInputException.class,
                () -> OBJECT_MAPPER.readValue(json, InventoryItem.class)
        );
    }

    @Test
    void serialize_ArrayDetails() {
        final String expectedDetails = "[1, 2, 3, 4, 5]";
        final String json = String.format(
                "{\"item_id\": %d, \"charges\": %d, \"details\": %s}",
                expectedId,
                expectedCharges,
                expectedDetails
        );
        assertThrows(
                MismatchedInputException.class,
                () -> OBJECT_MAPPER.readValue(json, InventoryItem.class)
        );
    }

    @Test
    void serialize_BooleanId() {
        final Boolean expectedId = RANDOM.nextBoolean();
        final String json = String.format(
                "{\"item_id\": %s, \"charges\": %d, \"details\": \"%s\"}",
                expectedId,
                expectedCharges,
                expectedDetails
        );
        assertThrows(
                MismatchedInputException.class,
                () -> OBJECT_MAPPER.readValue(json, InventoryItem.class)
        );
    }

    @Test
    void serialize_BooleanCharges() {
        final Boolean expectedCharges = RANDOM.nextBoolean();
        final String json = String.format(
                "{\"item_id\": %d, \"charges\": %s, \"details\": \"%s\"}",
                expectedId,
                expectedCharges,
                expectedDetails
        );
        assertThrows(
                MismatchedInputException.class,
                () -> OBJECT_MAPPER.readValue(json, InventoryItem.class)
        );
    }

    @Test
    void serialize_BooleanDetails() throws JsonProcessingException {
        final String expectedDetails = String.valueOf(RANDOM.nextBoolean());
        final String json = String.format(
                "{\"item_id\": %d, \"charges\": %d, \"details\": %s}",
                expectedId,
                expectedCharges,
                Boolean.valueOf(expectedDetails)
        );
        final InventoryItem item = OBJECT_MAPPER.readValue(json, InventoryItem.class);
        assertThat(item.getItemId(), is(expectedId));
        assertThat(item.getCharges(), is(expectedCharges));
        assertThat(item.getDetails(), is(expectedDetails));
    }

    @Test
    void serialize_NullId() throws JsonProcessingException {
        final String json = String.format(
                "{\"item_id\": %s, \"charges\": %d, \"details\": \"%s\"}",
                null,
                expectedCharges,
                expectedDetails
        );
        final InventoryItem item = OBJECT_MAPPER.readValue(json, InventoryItem.class);
        assertThat(item.getItemId(), is(nullValue()));
        assertThat(item.getCharges(), is(expectedCharges));
        assertThat(item.getDetails(), is(expectedDetails));
    }

    @Test
    void serialize_NullCharges() throws JsonProcessingException {
        final String json = String.format(
                "{\"item_id\": %d, \"charges\": %s, \"details\": \"%s\"}",
                expectedId,
                null,
                expectedDetails
        );
        final InventoryItem item = OBJECT_MAPPER.readValue(json, InventoryItem.class);
        assertThat(item.getItemId(), is(expectedId));
        assertThat(item.getCharges(), is(nullValue()));
        assertThat(item.getDetails(), is(expectedDetails));
    }

    @Test
    void serialize_NullDetails() throws JsonProcessingException {
        final String json = String.format(
                "{\"item_id\": %d, \"charges\": %d, \"details\": %s}",
                expectedId,
                expectedCharges,
                null
        );
        final InventoryItem item = OBJECT_MAPPER.readValue(json, InventoryItem.class);
        assertThat(item.getItemId(), is(expectedId));
        assertThat(item.getCharges(), is(expectedCharges));
        assertThat(item.getDetails(), is(nullValue()));
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
        final String json = String.format(
                "{\"item_id\": %d, \"charges\": %d, \"details\": \"%s\", \"extra\": \"field\"}",
                expectedId,
                expectedCharges,
                expectedDetails
        );
        final InventoryItem item = OBJECT_MAPPER.readValue(json, InventoryItem.class);
        assertThat(item.getItemId(), is(expectedId));
        assertThat(item.getCharges(), is(expectedCharges));
        assertThat(item.getDetails(), is(expectedDetails));
    }

    @Test
    void serialize_NoMatchingField() throws JsonProcessingException {
        final String json = "{\"key\": \"value\", \"extra\": \"field\"}";
        final InventoryItem item = OBJECT_MAPPER.readValue(json, InventoryItem.class);
        assertThat(item.getItemId(), is(nullValue()));
        assertThat(item.getCharges(), is(nullValue()));
        assertThat(item.getDetails(), is(nullValue()));
    }
}
