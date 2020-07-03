package com.senorpez.loot.api.model;

import com.senorpez.loot.api.ItemController;
import com.senorpez.loot.api.RootController;
import com.senorpez.loot.api.entity.Item;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.lang.NonNull;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

public class ItemModelAssembler extends RepresentationModelAssemblerSupport<Item, ItemModel> {
    public ItemModelAssembler(Class<?> controllerClass, Class<ItemModel> resourceType) {
        super(controllerClass, resourceType);
    }

    @Override
    @NonNull
    public ItemModel toModel(@NonNull Item entity) {
        return createModelWithId(entity.getId(), entity)
                .setId(entity.getId())
                .setName(entity.getName())
                .setWeight(entity.getWeight())
                .add(linkTo(ItemController.class).withRel("lootitems"))
                .add(linkTo(RootController.class).withRel(IanaLinkRelations.INDEX));
    }
}
