package com.senorpez.loottrack.api;

import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.lang.NonNull;

class EmbeddedItemModelAssembler extends RepresentationModelAssemblerSupport<Item, EmbeddedItemModel> {
    EmbeddedItemModelAssembler(Class<?> controllerClass, Class<EmbeddedItemModel> resourceType) {
        super(controllerClass, resourceType);
    }

    @Override
    @NonNull
    public EmbeddedItemModel toModel(@NonNull final Item entity) {
        return createModelWithId(entity.getId(), entity)
                .setId(entity.getId())
                .setName(entity.getName());
    }

    @Override
    @NonNull
    public CollectionModel<EmbeddedItemModel> toCollectionModel(@NonNull final Iterable<? extends Item> entities) {
        return super.toCollectionModel(entities);
    }
}
