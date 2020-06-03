package com.senorpez.loot.api;

import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.lang.NonNull;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class CharacterModelAssembler extends RepresentationModelAssemblerSupport<Character, CharacterModel> {
    public CharacterModelAssembler(Class<?> controllerClass, Class<CharacterModel> resourceType) {
        super(controllerClass, resourceType);
    }

    @Override
    @NonNull
    public CharacterModel toModel(@NonNull Character entity) {
        return createModelWithId(entity.getId(), entity, entity.getCampaign().getId())
                .setId(entity.getId())
                .setName(entity.getName())
                .setInventory(Collections.emptyList());
    }

    public CharacterModel toModel(@NonNull Character entity, List<Object[]> inventory) {
        List<InventoryItem> characterInventory = inventory
                .stream()
                .map(objects -> new InventoryItem()
                        .setQuantity(((Number) objects[0]).intValue())
                        .setName((String) objects[1])
                        .setWeight(objects[2] == null ? null : BigDecimal.valueOf(((Number) objects[2]).doubleValue()))
                        .setDetails(objects[3] == null ? null : (String) objects[3])
                        .setCharges(objects[4] == null ? null : ((Number) objects[4]).intValue())
                )
                .collect(Collectors.toList());

        return createModelWithId(entity.getId(), entity, entity.getCampaign().getId())
                .setId(entity.getId())
                .setName(entity.getName())
                .setInventory(characterInventory);
    }
}
