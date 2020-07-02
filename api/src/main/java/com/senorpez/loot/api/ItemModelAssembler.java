package com.senorpez.loot.api;

import com.senorpez.loot.api.entity.Item;
import com.senorpez.loot.api.model.ItemModel;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.lang.NonNull;

public class ItemModelAssembler extends RepresentationModelAssemblerSupport<Item, ItemModel> {
    public ItemModelAssembler(Class<?> controllerClass, Class<ItemModel> resourceType) {
        super(controllerClass, resourceType);
    }

    @Override
    @NonNull
    public ItemModel toModel(@NonNull final Item entity) {
        return createModelWithId(entity.getId(), entity)
                .setId(entity.getId())
                .setName(entity.getName())
                .setWeight(entity.getWeight());
//                .setDetails(entity.getDetails())
//                .setCharges(entity.getCharges());
    }
}
