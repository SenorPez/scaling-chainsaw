package com.senorpez.loot.api;

import com.fasterxml.jackson.core.JsonParser;
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

        final JsonNode itemNode = node.get("item");

        final String baseItemId = itemNode.get("_id").asText();
        final String name = itemNode.get("name").asText();
        final Integer quantity = itemNode.get("data").get("quantity").asInt();
        final Double weight = itemNode.get("data").get("weight").asDouble();
        final Double price = itemNode.get("data").get("price").asDouble();

        return new FoundryItem(actorId, baseItemId, name, quantity, weight, price);
    }
}
