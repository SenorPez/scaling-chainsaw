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

class CharacterTest {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    
    @Test
    void serialize_StringName() throws JsonProcessingException {
        final String json = "{\"name\": \"Aethelwuf\"}";
        final Character character = OBJECT_MAPPER.readValue(json, Character.class);
        final String expectedValue = "Aethelwuf";
        assertThat(character.getName(), is(expectedValue));
    }
    
    @Test
    void serialize_NumberName() throws JsonProcessingException {
        final String json = "{\"name\": 8675309}";
        final Character character = OBJECT_MAPPER.readValue(json, Character.class);
        final String expectedValue = "8675309";
        assertThat(character.getName(), is(expectedValue));
    }

    @Test
    void serialize_ObjectName() {
        final String json = "{\"name\": {\"key\": \"value\"}}";
        Exception exception = assertThrows(
                MismatchedInputException.class,
                () -> OBJECT_MAPPER.readValue(json, Character.class));
        final Class<MismatchedInputException> expectedValue = MismatchedInputException.class;
        assertThat(exception.getClass(), is(expectedValue));
    }

    @Test
    void serialize_ArrayName() {
        final String json = "{\"name\": [1, 2, 3, 4, 5]}";
        Exception exception = assertThrows(
                MismatchedInputException.class,
                () -> OBJECT_MAPPER.readValue(json, Character.class));
        final Class<MismatchedInputException> expectedValue = MismatchedInputException.class;
        assertThat(exception.getClass(), is(expectedValue));
    }

    @Test
    void serialize_BooleanName() throws JsonProcessingException {
        final String json = "{\"name\": true}";
        final Character character = OBJECT_MAPPER.readValue(json, Character.class);
        final String expectedValue = "true";
        assertThat(character.getName(), is(expectedValue));
    }

    @Test
    void serialize_NullName() throws JsonProcessingException {
        final String json = "{\"name\": null}";
        final Character character = OBJECT_MAPPER.readValue(json, Character.class);
        final Matcher<Object> expectedValue = nullValue();
        assertThat(character.getName(), is(expectedValue));
    }

    @Test
    void serialize_InvalidJSON() {
        final String json = "{\"name:\" \"Invalid";
        Exception exception = assertThrows(
                JsonParseException.class,
                () -> OBJECT_MAPPER.readValue(json, Character.class));
        final Class<JsonParseException> expectedValue = JsonParseException.class;
        assertThat(exception.getClass(), is(expectedValue));
    }

    @Test
    void serialize_ExtraFields() throws JsonProcessingException {
        final String json = "{\"name\": \"Aethelwuf\", \"extra\": \"field\"}";
        final Character character = OBJECT_MAPPER.readValue(json, Character.class);
        final String expectedValue = "Aethelwuf";
        assertThat(character.getName(), is(expectedValue));
    }

    @Test
    void serialize_NoMatchingField() throws JsonProcessingException {
        final String json = "{\"incorrect\": \"field\", \"extra\": \"field\"}";
        final Character character = OBJECT_MAPPER.readValue(json, Character.class);
        final Matcher<Object> expectedValue = nullValue();
        assertThat(character.getName(), is(expectedValue));
    }
}