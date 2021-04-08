package com.senorpez.loot.api;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;

class FoundryItemDeserializer extends StdDeserializer<FoundryItem> {
    public FoundryItemDeserializer() {
        this(null);
    }

    public FoundryItemDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public FoundryItem deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        final JsonNode node = p.getCodec().readTree(p);
        final String actorId = node.get("actorId").asText();
        final String itemId = node.get("itemData").get("_id").asText();
        return new FoundryItem(actorId, itemId);
    }
}
