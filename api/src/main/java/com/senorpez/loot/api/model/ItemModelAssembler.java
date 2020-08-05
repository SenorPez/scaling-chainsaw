package com.senorpez.loot.api.model;

import com.senorpez.loot.api.controller.ItemController;
import com.senorpez.loot.api.controller.RootController;
import com.senorpez.loot.api.entity.ItemEntity;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.lang.NonNull;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

public class ItemModelAssembler extends RepresentationModelAssemblerSupport<ItemEntity, ItemModel> {
    public ItemModelAssembler(Class<?> controllerClass, Class<ItemModel> resourceType) {
        super(controllerClass, resourceType);
    }

    @Override
    @NonNull
    public ItemModel toModel(@NonNull ItemEntity entity) {
        return createModelWithId(entity.getId(), entity)
                .setId(entity.getId())
                .setName(entity.getName())
                .setWeight(entity.getWeight())
                .add(linkTo(RootController.class).withRel(IanaLinkRelations.INDEX))
                .add(linkTo(ItemController.class).withRel("lootitems"));
    }
}
