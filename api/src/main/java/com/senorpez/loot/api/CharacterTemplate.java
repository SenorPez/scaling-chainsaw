package com.senorpez.loot.api;

import com.senorpez.loot.api.entity.Character;

import java.util.Collections;
import java.util.List;

class CharacterTemplate {
    public final String name;
    public final List<InventoryItemTemplate> inventory = Collections.emptyList();

    CharacterTemplate(Character character, List<Object[]> inventory) {
        this.name = character.getName();
//        this.inventory = inventory
//                .stream()
//                .map(InventoryItem::new)
//                .map(InventoryItemTemplate::new)
//                .sorted()
//                .collect(Collectors.toList());
    }
}
