package com.senorpez.loottrack.api;

import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;

public class ItemTransactionAssembler extends RepresentationModelAssemblerSupport<ItemTransaction, ItemTransactionModel> {
    public ItemTransactionAssembler(Class<?> controllerClass, Class<ItemTransactionModel> resourceType) {
        super(controllerClass, resourceType);
    }

    @Override
    public ItemTransactionModel toModel(ItemTransaction entity) {
        return createModelWithId(entity.getId(), entity, entity.getCampaignId(), entity.getPlayerId())
                .setId(entity.getId())
                .setItemName(entity.getItemName());
    }
}
