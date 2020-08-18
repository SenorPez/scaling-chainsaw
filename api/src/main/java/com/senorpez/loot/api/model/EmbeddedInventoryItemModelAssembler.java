package com.senorpez.loot.api.model;

import com.senorpez.loot.api.entity.InventoryItemEntity;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.lang.NonNull;

class EmbeddedInventoryItemModelAssembler extends RepresentationModelAssemblerSupport<InventoryItemEntity, EmbeddedInventoryItemModel> {
    EmbeddedInventoryItemModelAssembler(Class<?> controllerClass, Class<EmbeddedInventoryItemModel> resourceType) {
        super(controllerClass, resourceType);
    }

    @Override
    @NonNull
    public EmbeddedInventoryItemModel toModel(@NonNull InventoryItemEntity entity) {
        return toModel(entity, null);
    }

    @NonNull
    public EmbeddedInventoryItemModel toModel(@NonNull InventoryItemEntity entity, Integer quantity) {
        return instantiateModel(entity)
                .setId(entity.getId())
                .setName(entity.getItemEntity().getName())
                .setWeight(entity.getItemEntity().getWeight())
                .setCharges(entity.getCharges())
                .setDetails(entity.getDetails())
                .setQuantity(quantity);
    }

    @Override
    @NonNull
    public CollectionModel<EmbeddedInventoryItemModel> toCollectionModel(@NonNull Iterable<? extends InventoryItemEntity> entities) {
        return super.toCollectionModel(entities);
    }
}
