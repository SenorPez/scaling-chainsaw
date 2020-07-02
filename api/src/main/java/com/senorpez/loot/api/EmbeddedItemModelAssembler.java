package com.senorpez.loot.api;

import com.senorpez.loot.api.entity.Item;
import com.senorpez.loot.api.model.EmbeddedItemModel;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.lang.NonNull;

class EmbeddedItemModelAssembler extends RepresentationModelAssemblerSupport<Item, EmbeddedItemModel> {
    EmbeddedItemModelAssembler() {
        super(ItemController.class, EmbeddedItemModel.class);
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
