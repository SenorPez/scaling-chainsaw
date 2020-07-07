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

class CampaignTest {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Test
    void serialize_StringName() throws JsonProcessingException {
        final String json = "{\"name\": \"Defiance in Phlan\"}";
        final Campaign campaign = OBJECT_MAPPER.readValue(json, Campaign.class);
        final String expectedValue = "Defiance in Phlan";
        assertThat(campaign.getName(), is(expectedValue));
    }

    @Test
    void serialize_NumberName() throws JsonProcessingException {
        final String json = "{\"name\": 8675309}";
        final Campaign campaign = OBJECT_MAPPER.readValue(json, Campaign.class);
        final String expectedValue = "8675309";
        assertThat(campaign.getName(), is(expectedValue));
    }

    @Test
    void serialize_ObjectName() {
        final String json = "{\"name\": {\"key\": \"value\"}}";
        Exception exception = assertThrows(
                MismatchedInputException.class,
                () -> OBJECT_MAPPER.readValue(json, Campaign.class));
        final Class<MismatchedInputException> expectedValue = MismatchedInputException.class;
        assertThat(exception.getClass(), is(expectedValue));
    }

    @Test
    void serialize_ArrayName() {
        final String json = "{\"name\": [1, 2, 3, 4, 5]}";
        Exception exception = assertThrows(
                MismatchedInputException.class,
                () -> OBJECT_MAPPER.readValue(json, Campaign.class));
        final Class<MismatchedInputException> expectedValue = MismatchedInputException.class;
        assertThat(exception.getClass(), is(expectedValue));
    }

    @Test
    void serialize_BooleanName() throws JsonProcessingException {
        final String json = "{\"name\": true}";
        final Campaign campaign = OBJECT_MAPPER.readValue(json, Campaign.class);
        final String expectedValue = "true";
        assertThat(campaign.getName(), is(expectedValue));
    }

    @Test
    void serialize_NullName() throws JsonProcessingException {
        final String json = "{\"name\": null}";
        final Campaign campaign = OBJECT_MAPPER.readValue(json, Campaign.class);
        final Matcher<Object> expectedValue = nullValue();
        assertThat(campaign.getName(), is(expectedValue));
    }

    @Test
    void serialize_invalidJSON() {
        final String json = "{\"name:\" \"Invalid";
        Exception exception = assertThrows(
                JsonParseException.class,
                () -> OBJECT_MAPPER.readValue(json, Campaign.class));
        final Class<JsonParseException> expectedValue = JsonParseException.class;
        assertThat(exception.getClass(), is(expectedValue));
    }

    @Test
    void serialize_extraFields() throws JsonProcessingException {
        final String json = "{\"name\": \"Defiance in Phlan\", \"extra\": \"field\"}";
        final Campaign campaign = OBJECT_MAPPER.readValue(json, Campaign.class);
        final String expectedValue = "Defiance in Phlan";
        assertThat(campaign.getName(), is(expectedValue));
    }

    @Test
    void serialize_noMatchingField() throws JsonProcessingException {
        final String json = "{\"incorrect\": \"field\", \"extra\": \"field\"}";
        final Campaign campaign = OBJECT_MAPPER.readValue(json, Campaign.class);
        final Matcher<Object> expectedValue = nullValue();
        assertThat(campaign.getName(), is(expectedValue));
    }
}