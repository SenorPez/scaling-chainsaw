package com.senorpez.loottrack.api;

import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;

public class ItemModelAssembler extends RepresentationModelAssemblerSupport<Item, ItemModel> {
    /**
     * Creates a new {@link RepresentationModelAssemblerSupport} using the given controller class and resource type.
     *
     * @param controllerClass must not be {@literal null}.
     * @param resourceType    must not be {@literal null}.
     */
    public ItemModelAssembler(Class<?> controllerClass, Class<ItemModel> resourceType) {
        super(controllerClass, resourceType);
    }

    @Override
    public ItemModel toModel(Item entity) {
        return createModelWithId(entity.getId(), entity)
                .setId(entity.getId())
                .setName(entity.getName())
                .setCharges(entity.getCharges());
    }
}
