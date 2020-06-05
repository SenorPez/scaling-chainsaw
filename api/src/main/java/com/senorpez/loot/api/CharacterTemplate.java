package com.senorpez.loot.api;

import java.util.List;
import java.util.stream.Collectors;

class CharacterTemplate {
    public String name;
    public List<InventoryItemTemplate> inventory;

    CharacterTemplate(Character character, List<Object[]> inventory) {
        this.name = character.getName();
        this.inventory = inventory
                .stream()
                .map(InventoryItem::new)
                .map(InventoryItemTemplate::new)
                .sorted()
                .collect(Collectors.toList());
    }
}
