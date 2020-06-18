package com.senorpez.loot.api;

import org.springframework.lang.NonNull;

class InventoryItemModelAssembler {
    static InventoryItemModel toModel(@NonNull InventoryItem entity) {
        return new InventoryItemModel()
                .setCharges(entity.getCharges())
                .setDetails(entity.getDetails())
                .setName(entity.getName())
                .setQuantity(entity.getQuantity())
                .setWeight(entity.getWeight());
    }
}

