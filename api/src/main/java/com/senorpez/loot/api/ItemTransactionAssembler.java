package com.senorpez.loot.api;

import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.lang.NonNull;

public class ItemTransactionAssembler extends RepresentationModelAssemblerSupport<ItemTransaction, ItemTransactionModel> {
    public ItemTransactionAssembler(Class<?> controllerClass, Class<ItemTransactionModel> resourceType) {
        super(controllerClass, resourceType);
    }

    @Override
    @NonNull
    public ItemTransactionModel toModel(@NonNull ItemTransaction entity) {
        return createModelWithId(entity.getId(), entity, entity.getCharacter().getCampaign().getId(), entity.getCharacter().getId())
                .setId(entity.getId())
                .setItemName(entity.getItem().getName());
    }
}
