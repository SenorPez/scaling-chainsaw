package com.senorpez.loot.api;

import com.senorpez.loot.api.entity.InventoryItem;
import com.senorpez.loot.api.model.InventoryItemModel;
import org.springframework.lang.NonNull;

class InventoryItemModelAssembler {
    static InventoryItemModel toModel(@NonNull InventoryItem entity) {
        return new InventoryItemModel()
                .setCharges(entity.getCharges())
                .setDetails(entity.getDetails())
                .setId(entity.getId())
                .setName(entity.getItem().getName())
                .setQuantity(entity.getQuantity())
                .setWeight(entity.getItem().getWeight());
    }
}

