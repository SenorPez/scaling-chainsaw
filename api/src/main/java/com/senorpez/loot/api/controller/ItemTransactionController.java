package com.senorpez.loot.api.controller;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.senorpez.loot.api.entity.CampaignEntity;
import com.senorpez.loot.api.entity.ItemEntity;
import com.senorpez.loot.api.entity.ItemTransactionEntity;
import com.senorpez.loot.api.exception.CampaignNotFoundException;
import com.senorpez.loot.api.exception.ItemNotFoundException;
import com.senorpez.loot.api.model.CharacterModel;
import com.senorpez.loot.api.model.CharacterModelAssembler;
import com.senorpez.loot.api.repository.CampaignRepository;
import com.senorpez.loot.api.repository.CharacterRepository;
import com.senorpez.loot.api.repository.ItemRepository;
import com.senorpez.loot.api.repository.ItemTransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;

import static org.springframework.hateoas.MediaTypes.HAL_JSON_VALUE;

@RequestMapping(
        value = "/campaigns/{campaignId}/characters/{characterId}/itemtransactions",
        produces = {HAL_JSON_VALUE}
)
@RestController
public class ItemTransactionController {
    @Autowired
    private CampaignRepository campaignRepository;

    @Autowired
    private CharacterRepository characterRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private ItemTransactionRepository itemTransactionRepository;

    private final CharacterModelAssembler assembler = new CharacterModelAssembler(CharacterController.class, CharacterModel.class);

    @PostMapping(consumes = {HAL_JSON_VALUE})
    @RolesAllowed("user")
    ResponseEntity<CharacterModel> addItemTransaction(@RequestHeader String Authorization, @RequestBody IncomingTransaction incomingValue, @PathVariable final int campaignId, @PathVariable final int characterId) {
        final CampaignEntity campaignEntity = campaignRepository.findById(campaignId).orElseThrow(() -> new CampaignNotFoundException(campaignId));
//        final CharacterEntity characterEntity = characterRepository.findByCampaignAndId(campaignEntity, characterId).orElseThrow(() -> new CharacterNotFoundException(characterId));
        final ItemEntity itemEntity = itemRepository.findById(incomingValue.getItem_id()).orElseThrow(() -> new ItemNotFoundException(incomingValue.getItem_id()));
//        final ItemTransactionEntity newItemTransactionEntity = new ItemTransactionEntity(
////                new InventoryItem(itemEntity),
//                null,
//                incomingValue.getQuantity(),
//                incomingValue.getRemark()
//        );
        final ItemTransactionEntity newItemTransactionEntity = null;
        itemTransactionRepository.save(newItemTransactionEntity);

//        final CharacterEntity updatedCharacterEntity = characterRepository.findByCampaignAndId(campaignEntity, characterId).orElseThrow(() -> new CharacterNotFoundException(characterId));
        final CharacterModel characterModel = null;
        return ResponseEntity.created(characterModel.getRequiredLink(IanaLinkRelations.SELF).toUri()).body(characterModel);
    }

    private static class IncomingTransaction {
        @JsonProperty
        private int item_id;
        @JsonProperty
        private int quantity;
        @JsonProperty
        private String remark;

        public IncomingTransaction() {
        }

        public int getItem_id() {
            return item_id;
        }

        public int getQuantity() {
            return quantity;
        }

        public String getRemark() {
            return remark;
        }
    }
}
