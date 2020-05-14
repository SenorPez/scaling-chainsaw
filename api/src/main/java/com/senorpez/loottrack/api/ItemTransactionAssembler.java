package com.senorpez.loottrack.api;

import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;

public class ItemTransactionAssembler extends RepresentationModelAssemblerSupport<ItemTransaction, ItemTransactionModel> {
    public ItemTransactionAssembler(Class<?> controllerClass, Class<ItemTransactionModel> resourceType) {
        super(controllerClass, resourceType);
    }

    @Override
    public ItemTransactionModel toModel(ItemTransaction entity) {
        return createModelWithId(entity.getId(), entity, entity.getPlayer().getCampaign().getId(), entity.getPlayer().getId())
                .setId(entity.getId())
                .setItemName(entity.getItem().getName());
    }
}
