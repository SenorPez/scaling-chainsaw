package com.senorpez.loot.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;
import java.util.List;
import java.util.stream.Collectors;

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

    private final CharacterModelAssembler assembler = new CharacterModelAssembler(CharacterController.class, CharacterModel.class);

    @GetMapping
    ResponseEntity<CollectionModel<CharacterModel>> characters(@PathVariable final int campaignId) {
        Campaign campaign = campaignRepository.findById(campaignId).orElseThrow(() -> new CampaignNotFoundException(campaignId));
        CollectionModel<CharacterModel> characterModels = new CollectionModel<>(
                characterRepository.findByCampaign(campaign).stream()
                        .map(assembler::toModel)
                        .collect(Collectors.toList())
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
        List<Object[]> inventory = itemTransactionRepository.getInventory(characterId, campaignId);

        CharacterModel characterModel = assembler.toModel(character, inventory);
        characterModel.add(linkTo(CharacterController.class, campaignId).withRel("characters"));
        characterModel.add(linkTo(RootController.class).withRel("index"));

        return ResponseEntity.ok(characterModel);
    }

    @GetMapping(value = "/{characterId}", produces = {TEXT_HTML_VALUE})
    String characters(@PathVariable final int campaignId, @PathVariable final int characterId, Model model) {
        Campaign campaign = campaignRepository.findById(campaignId).orElseThrow(() -> new CampaignNotFoundException(campaignId));
        Character character = characterRepository.findByCampaignAndId(campaign, characterId).orElseThrow(() -> new CharacterNotFoundException(characterId));
        List<Object[]> inventory = itemTransactionRepository.getInventory(characterId, campaignId);

        CharacterModel characterModel = assembler.toModel(character, inventory);
        model.addAttribute(characterModel);
        return "character";
    }

    @PostMapping(consumes = {HAL_JSON_VALUE})
    @RolesAllowed("user")
    ResponseEntity<CharacterModel> addCharacter(@RequestHeader String Authorization, @RequestBody Character newCharacter, @PathVariable final int campaignId) {
        Campaign campaign = campaignRepository.findById(campaignId).orElseThrow(() -> new CampaignNotFoundException(campaignId));
        Character character = characterRepository.save(newCharacter.setCampaign(campaign));

        CharacterModel characterModel = assembler.toModel(character);
        characterModel.add(linkTo(CharacterController.class, campaignId).withRel("characters"));
        characterModel.add(linkTo(RootController.class).withRel("index"));

        return ResponseEntity.created(characterModel.getRequiredLink("self").toUri()).body(characterModel);
    }
}
