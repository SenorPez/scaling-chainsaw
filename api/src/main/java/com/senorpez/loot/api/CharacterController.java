package com.senorpez.loot.api;

import com.senorpez.loot.api.entity.Campaign;
import com.senorpez.loot.api.entity.Character;
import com.senorpez.loot.api.model.CharacterModel;
import com.senorpez.loot.api.model.CharacterModelAssembler;
import com.senorpez.loot.api.model.EmbeddedCharacterModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
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

    private final CharacterModelAssembler<EmbeddedCharacterModel> collectionAssembler = new CharacterModelAssembler<>(CharacterController.class, EmbeddedCharacterModel.class);
    private final CharacterModelAssembler<CharacterModel> assembler = new CharacterModelAssembler<>(CharacterController.class, CharacterModel.class);

    @GetMapping
    ResponseEntity<CollectionModel<EmbeddedCharacterModel>> characters(@PathVariable final int campaignId) {
        final Campaign campaign = campaignRepository.findById(campaignId).orElseThrow(() -> new CampaignNotFoundException(campaignId));
        final CollectionModel<EmbeddedCharacterModel> characterModels = collectionAssembler.toCollectionModel(campaign.getCharacters(), campaignId);
        return ResponseEntity.ok(characterModels);
    }

    @GetMapping(value = "/{characterId}", produces = {HAL_JSON_VALUE})
    ResponseEntity<CharacterModel> characters(@PathVariable final int campaignId, @PathVariable final int characterId) {
//        Campaign campaign = campaignRepository.findById(campaignId).orElseThrow(() -> new CampaignNotFoundException(campaignId));
//        Character character = characterRepository.findByCampaignAndId(campaign, characterId).orElseThrow(() -> new CharacterNotFoundException(characterId));
////        List<Object[]> inventory = itemTransactionRepository.getInventory(characterId, campaignId);
//        List<Object[]> inventory = Collections.emptyList();
//
//        CharacterModel characterModel = assembler.toModel(character, inventory);
//        characterModel.add(linkTo(CharacterController.class, campaignId).withRel("characters"));
//        characterModel.add(linkTo(RootController.class).withRel("index"));
//
//        return ResponseEntity.ok(characterModel);
        return null;
    }

//    @GetMapping(value = "/{characterId}", produces = {TEXT_HTML_VALUE})
//    String characters(@PathVariable final int campaignId, @PathVariable final int characterId, Model model) {
////        Campaign campaign = campaignRepository.findById(campaignId).orElseThrow(() -> new CampaignNotFoundException(campaignId));
////        Character character = characterRepository.findByCampaignAndId(campaign, characterId).orElseThrow(() -> new CharacterNotFoundException(characterId));
//////        List<Object[]> inventory = itemTransactionRepository.getInventory(characterId, campaignId);
////        List<Object[]> inventory = Collections.emptyList();
////
////        CharacterTemplate characterTemplate = new CharacterTemplate(character, inventory);
////        model.addAttribute("character", characterTemplate);
////        return "character";
//        return null;
//    }

    @PostMapping(consumes = {HAL_JSON_VALUE})
    @RolesAllowed("user")
    ResponseEntity<CharacterModel> addCharacter(@RequestHeader String Authorization, @RequestBody Character newCharacter, @PathVariable final int campaignId) {
//        Campaign campaign = campaignRepository.findById(campaignId).orElseThrow(() -> new CampaignNotFoundException(campaignId));
////        Character character = characterRepository.save(newCharacter.setCampaign(campaign));
//        Character character = new Character();
//
//        CharacterModel characterModel = assembler.toModel(character);
//        characterModel.add(linkTo(CharacterController.class, campaignId).withRel("characters"));
//        characterModel.add(linkTo(RootController.class).withRel("index"));
//
//        return ResponseEntity.created(characterModel.getRequiredLink("self").toUri()).body(characterModel);
        return  null;
    }
}
