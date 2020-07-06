package com.senorpez.loot.api.model;

import com.senorpez.loot.api.controller.ItemController;
import com.senorpez.loot.api.controller.RootController;
import com.senorpez.loot.api.entity.Item;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.lang.NonNull;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

public class EmbeddedItemModelAssembler extends RepresentationModelAssemblerSupport<Item, EmbeddedItemModel> {
    public EmbeddedItemModelAssembler(Class<?> controllerClass, Class<EmbeddedItemModel> resourceType) {
        super(controllerClass, resourceType);
    }

    @Override
    @NonNull
    public EmbeddedItemModel toModel(@NonNull Item entity) {
        return createModelWithId(entity.getId(), entity)
                .setId(entity.getId())
                .setName(entity.getName());
    }

    @Override
    @NonNull
    public CollectionModel<EmbeddedItemModel> toCollectionModel(@NonNull Iterable<? extends Item> entities) {
        return super.toCollectionModel(entities)
                .add(linkTo(ItemController.class).withSelfRel())
                .add(linkTo(RootController.class).withRel(IanaLinkRelations.INDEX));
    }
}
