package com.senorpez.loot.api;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

class CharacterTemplate {
    public String name;
    public List<InventoryItemTemplate> inventory;

    CharacterTemplate(Character character, List<Object[]> inventory) {
        this.name = character.getName();
        this.inventory = inventory
                .stream()
                .map(objects -> new InventoryItemTemplate(
                        ((Number) objects[0]).intValue(),
                        (String) objects[1],
                        objects[2] == null ? null : BigDecimal.valueOf(((Number) objects[2]).doubleValue()),
                        objects[3] == null ? null : (String) objects[3],
                        objects[4] == null ? null : ((Number) objects[4]).intValue())
                )
                .collect(Collectors.toList());
    }
}
