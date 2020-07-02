package com.senorpez.loot.api;

import com.senorpez.loot.api.entity.Character;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.lang.NonNull;

class EmbeddedCharacterModelAssembler extends RepresentationModelAssemblerSupport<Character, EmbeddedCharacterModel> {
    public EmbeddedCharacterModelAssembler() {
        super(CharacterController.class, EmbeddedCharacterModel.class);
    }

    @Override
    @NonNull
    public EmbeddedCharacterModel toModel(@NonNull final Character entity) {
        return new EmbeddedCharacterModel();
//        return createModelWithId(entity.getId(), entity, entity.getCampaign().getId())
//                .setId(entity.getId())
//                .setName(entity.getName());
    }

    @Override
    @NonNull
    public CollectionModel<EmbeddedCharacterModel> toCollectionModel(@NonNull final Iterable<? extends Character> entities) {
        return super.toCollectionModel(entities);
    }
}
