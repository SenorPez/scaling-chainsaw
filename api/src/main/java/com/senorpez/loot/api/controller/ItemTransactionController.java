package com.senorpez.loot.api.controller;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.senorpez.loot.api.entity.Character;
import com.senorpez.loot.api.entity.*;
import com.senorpez.loot.api.exception.CampaignNotFoundException;
import com.senorpez.loot.api.exception.CharacterNotFoundException;
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
        final Campaign campaign = campaignRepository.findById(campaignId).orElseThrow(() -> new CampaignNotFoundException(campaignId));
        final Character character = characterRepository.findByCampaignAndId(campaign, characterId).orElseThrow(() -> new CharacterNotFoundException(characterId));
        final Item item = itemRepository.findById(incomingValue.getItem_id()).orElseThrow(() -> new ItemNotFoundException(incomingValue.getItem_id()));
        final ItemTransaction newItemTransaction = new ItemTransaction(
                new InventoryItem(item),
                incomingValue.getQuantity(),
                incomingValue.getRemark()
        );
        itemTransactionRepository.save(newItemTransaction);

        final Character updatedCharacter = characterRepository.findByCampaignAndId(campaign, characterId).orElseThrow(() -> new CharacterNotFoundException(characterId));
        final CharacterModel characterModel = assembler.toModel(updatedCharacter, itemTransactionRepository);
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
