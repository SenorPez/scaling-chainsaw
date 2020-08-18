package com.senorpez.loot.api.model;

import com.senorpez.loot.api.entity.InventoryItemEntity;
import org.springframework.lang.NonNull;

class EmbeddedInventoryItemModelAssembler {
    @NonNull
    public EmbeddedInventoryItemModel toModel(@NonNull InventoryItemEntity entity) {
        return toModel(entity, null);
    }

    @NonNull
    public EmbeddedInventoryItemModel toModel(@NonNull InventoryItemEntity entity, Integer quantity) {
        return new EmbeddedInventoryItemModel()
                .setName(entity.getItemEntity().getName())
                .setWeight(entity.getItemEntity().getWeight())
                .setCharges(entity.getCharges())
                .setDetails(entity.getDetails())
                .setQuantity(quantity);
    }
}
