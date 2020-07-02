package com.senorpez.loot.api;

import com.senorpez.loot.api.entity.Campaign;
import com.senorpez.loot.api.entity.Character;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;
import java.util.Collections;
import java.util.List;

import static org.springframework.hateoas.MediaTypes.HAL_JSON_VALUE;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
import static org.springframework.http.MediaType.TEXT_HTML_VALUE;

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

    private final EmbeddedCharacterModelAssembler collectionAssembler = new EmbeddedCharacterModelAssembler();
    private final CharacterModelAssembler assembler = new CharacterModelAssembler(CharacterController.class, CharacterModel.class);

    @GetMapping
    ResponseEntity<CollectionModel<EmbeddedCharacterModel>> characters(@PathVariable final int campaignId) {
        Campaign campaign = campaignRepository.findById(campaignId).orElseThrow(() -> new CampaignNotFoundException(campaignId));
        CollectionModel<EmbeddedCharacterModel> characterModels = new CollectionModel<>(
                collectionAssembler.toCollectionModel(characterRepository.findByCampaign(campaign))
        );
        characterModels.add(linkTo(CharacterController.class, campaignId).withSelfRel());
        characterModels.add(linkTo(RootController.class).withRel("index"));
        characterModels.add(linkTo(methodOn(CampaignController.class).campaigns(campaignId)).withRel("campaign"));

        return ResponseEntity.ok(characterModels);
    }

    @GetMapping(value = "/{characterId}", produces = {HAL_JSON_VALUE})
    ResponseEntity<CharacterModel> characters(@PathVariable final int campaignId, @PathVariable final int characterId) {
        Campaign campaign = campaignRepository.findById(campaignId).orElseThrow(() -> new CampaignNotFoundException(campaignId));
        Character character = characterRepository.findByCampaignAndId(campaign, characterId).orElseThrow(() -> new CharacterNotFoundException(characterId));
//        List<Object[]> inventory = itemTransactionRepository.getInventory(characterId, campaignId);
        List<Object[]> inventory = Collections.emptyList();

        CharacterModel characterModel = assembler.toModel(character, inventory);
        characterModel.add(linkTo(CharacterController.class, campaignId).withRel("characters"));
        characterModel.add(linkTo(RootController.class).withRel("index"));

        return ResponseEntity.ok(characterModel);
    }

    @GetMapping(value = "/{characterId}", produces = {TEXT_HTML_VALUE})
    String characters(@PathVariable final int campaignId, @PathVariable final int characterId, Model model) {
        Campaign campaign = campaignRepository.findById(campaignId).orElseThrow(() -> new CampaignNotFoundException(campaignId));
        Character character = characterRepository.findByCampaignAndId(campaign, characterId).orElseThrow(() -> new CharacterNotFoundException(characterId));
//        List<Object[]> inventory = itemTransactionRepository.getInventory(characterId, campaignId);
        List<Object[]> inventory = Collections.emptyList();

        CharacterTemplate characterTemplate = new CharacterTemplate(character, inventory);
        model.addAttribute("character", characterTemplate);
        return "character";
    }

    @PostMapping(consumes = {HAL_JSON_VALUE})
    @RolesAllowed("user")
    ResponseEntity<CharacterModel> addCharacter(@RequestHeader String Authorization, @RequestBody Character newCharacter, @PathVariable final int campaignId) {
        Campaign campaign = campaignRepository.findById(campaignId).orElseThrow(() -> new CampaignNotFoundException(campaignId));
//        Character character = characterRepository.save(newCharacter.setCampaign(campaign));
        Character character = new Character();

        CharacterModel characterModel = assembler.toModel(character);
        characterModel.add(linkTo(CharacterController.class, campaignId).withRel("characters"));
        characterModel.add(linkTo(RootController.class).withRel("index"));

        return ResponseEntity.created(characterModel.getRequiredLink("self").toUri()).body(characterModel);
    }
}
