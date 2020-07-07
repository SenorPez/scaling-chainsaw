package com.senorpez.loot.api.controller;

import com.senorpez.loot.api.entity.CampaignEntity;
import com.senorpez.loot.api.entity.Character;
import com.senorpez.loot.api.exception.CampaignNotFoundException;
import com.senorpez.loot.api.exception.CharacterNotFoundException;
import com.senorpez.loot.api.model.CharacterModel;
import com.senorpez.loot.api.model.CharacterModelAssembler;
import com.senorpez.loot.api.model.EmbeddedCharacterModel;
import com.senorpez.loot.api.model.EmbeddedCharacterModelAssembler;
import com.senorpez.loot.api.repository.CampaignRepository;
import com.senorpez.loot.api.repository.CharacterRepository;
import com.senorpez.loot.api.repository.ItemTransactionRepository;
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
        final CampaignEntity campaignEntity = campaignRepository.findById(campaignId).orElseThrow(() -> new CampaignNotFoundException(campaignId));
        final CollectionModel<EmbeddedCharacterModel> characterModels = collectionAssembler.toCollectionModel(campaignEntity.getCharacters(), campaignId);
        return ResponseEntity.ok(characterModels);
    }

    @GetMapping(value = "/{characterId}", produces = {HAL_JSON_VALUE})
    ResponseEntity<CharacterModel> characters(@PathVariable final int campaignId, @PathVariable final int characterId) {
        final CampaignEntity campaignEntity = campaignRepository.findById(campaignId).orElseThrow(() -> new CampaignNotFoundException(campaignId));
        final Character character = characterRepository.findByCampaignAndId(campaignEntity, characterId).orElseThrow(() -> new CharacterNotFoundException(characterId));
        final CharacterModel characterModel = assembler.toModel(character, itemTransactionRepository);
        return ResponseEntity.ok(characterModel);
    }

    @PostMapping(consumes = {HAL_JSON_VALUE})
    @RolesAllowed("user")
    ResponseEntity<CharacterModel> addCharacter(@RequestHeader String Authorization, @RequestBody Character newCharacter, @PathVariable final int campaignId) {
        final CampaignEntity campaignEntity = campaignRepository.findById(campaignId).orElseThrow(() -> new CampaignNotFoundException(campaignId));
        final Character character = characterRepository.save(newCharacter.setCampaignEntity(campaignEntity));
        final CharacterModel characterModel = assembler.toModel(character, itemTransactionRepository);
        return ResponseEntity.created(characterModel.getRequiredLink(IanaLinkRelations.SELF).toUri()).body(characterModel);
    }
}
