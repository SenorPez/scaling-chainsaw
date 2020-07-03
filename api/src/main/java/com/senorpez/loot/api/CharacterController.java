package com.senorpez.loot.api;

import com.senorpez.loot.api.entity.Campaign;
import com.senorpez.loot.api.entity.Character;
import com.senorpez.loot.api.model.CharacterModel;
import com.senorpez.loot.api.model.CharacterModelAssembler;
import com.senorpez.loot.api.model.EmbeddedCharacterModel;
import com.senorpez.loot.api.model.EmbeddedCharacterModelAssembler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;

import static org.springframework.hateoas.MediaTypes.HAL_JSON_VALUE;

@RequestMapping(
        value = "/campaigns/{campaignId}/characters",
        produces = {HAL_JSON_VALUE}
)
@Controller
public class CharacterController {
    @Autowired
    private CampaignRepository campaignRepository;

    @Autowired
    private CharacterRepository characterRepository;

    @Autowired
    private ItemTransactionRepository itemTransactionRepository;

    private final EmbeddedCharacterModelAssembler collectionAssembler = new EmbeddedCharacterModelAssembler(CharacterController.class, EmbeddedCharacterModel.class);
    private final CharacterModelAssembler assembler = new CharacterModelAssembler(CharacterController.class, CharacterModel.class);

    @GetMapping
    ResponseEntity<CollectionModel<EmbeddedCharacterModel>> characters(@PathVariable final int campaignId) {
        final Campaign campaign = campaignRepository.findById(campaignId).orElseThrow(() -> new CampaignNotFoundException(campaignId));
        final CollectionModel<EmbeddedCharacterModel> characterModels = collectionAssembler.toCollectionModel(campaign.getCharacters(), campaignId);
        return ResponseEntity.ok(characterModels);
    }

    @GetMapping(value = "/{characterId}", produces = {HAL_JSON_VALUE})
    ResponseEntity<CharacterModel> characters(@PathVariable final int campaignId, @PathVariable final int characterId) {
        final Campaign campaign = campaignRepository.findById(campaignId).orElseThrow(() -> new CampaignNotFoundException(campaignId));
        final Character character = characterRepository.findByCampaignAndId(campaign, characterId).orElseThrow(() -> new CharacterNotFoundException(characterId));
        final CharacterModel characterModel = assembler.toModel(character, itemTransactionRepository);
        return ResponseEntity.ok(characterModel);
    }

    @PostMapping(consumes = {HAL_JSON_VALUE})
    @RolesAllowed("user")
    ResponseEntity<CharacterModel> addCharacter(@RequestHeader String Authorization, @RequestBody Character newCharacter, @PathVariable final int campaignId) {
        final Campaign campaign = campaignRepository.findById(campaignId).orElseThrow(() -> new CampaignNotFoundException(campaignId));
        final Character character = characterRepository.save(newCharacter.setCampaign(campaign));
        final CharacterModel characterModel = assembler.toModel(character, itemTransactionRepository);
        return ResponseEntity.created(characterModel.getRequiredLink(IanaLinkRelations.SELF).toUri()).body(characterModel);
    }
}
