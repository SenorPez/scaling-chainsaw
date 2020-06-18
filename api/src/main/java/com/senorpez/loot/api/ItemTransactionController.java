package com.senorpez.loot.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;
import java.util.List;

import static org.springframework.hateoas.MediaTypes.HAL_JSON_VALUE;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

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
    ResponseEntity<CharacterModel> addItemTransaction(@RequestHeader String Authorization, @RequestBody ItemTransaction incomingValue, @PathVariable final int campaignId, @PathVariable final int characterId) {
        Campaign campaign = campaignRepository.findById(campaignId).orElseThrow(() -> new CampaignNotFoundException(campaignId));
        Character character = characterRepository.findByCampaignAndId(campaign, characterId).orElseThrow(() -> new CharacterNotFoundException(characterId));
        Item item = itemRepository.findById(incomingValue.getItem().getId()).orElseThrow(() -> new ItemNotFoundException(incomingValue.getItem().getId()));

        ItemTransaction newItemTransaction = new ItemTransaction(
                character,
                item,
                incomingValue.getQuantity(),
                incomingValue.getRemark()
        );
        itemTransactionRepository.save(newItemTransaction);

        Character updatedCharacter = characterRepository.findByCampaignAndId(campaign, characterId).orElseThrow(() -> new CharacterNotFoundException(characterId));
        List<Object[]> inventory = itemTransactionRepository.getInventory(characterId, campaignId);

        CharacterModel characterModel = assembler.toModel(updatedCharacter, inventory);
        characterModel.add(linkTo(CharacterController.class, campaignId).withRel("characters"));
        characterModel.add(linkTo(RootController.class).withRel("index"));

        return ResponseEntity.created(characterModel.getRequiredLink("self").toUri()).body(characterModel);
    }
}
