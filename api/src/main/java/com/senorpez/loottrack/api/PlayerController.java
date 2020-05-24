package com.senorpez.loottrack.api;

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
        value = "/campaigns/{campaignId}/players",
        produces = {HAL_JSON_VALUE}
)
@Controller
public class PlayerController {
    @Autowired
    private CampaignRepository campaignRepository;

    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private ItemTransactionRepository itemTransactionRepository;

    private final PlayerModelAssembler assembler = new PlayerModelAssembler(PlayerController.class, PlayerModel.class);

    @GetMapping
    ResponseEntity<CollectionModel<PlayerModel>> players(@PathVariable final int campaignId) {
        Campaign campaign = campaignRepository.findById(campaignId).orElseThrow(() -> new CampaignNotFoundException(campaignId));
        CollectionModel<PlayerModel> playerModels = new CollectionModel<>(
                playerRepository.findByCampaign(campaign).stream()
                        .map(assembler::toModel)
                        .collect(Collectors.toList())
        );

        playerModels.add(linkTo(PlayerController.class, campaignId).withSelfRel());
        playerModels.add(linkTo(RootController.class).withRel("index"));
        playerModels.add(linkTo(methodOn(CampaignController.class).campaigns(campaignId)).withRel("campaign"));

        return ResponseEntity.ok(playerModels);
    }

    @GetMapping(value = "/{playerId}", produces = {HAL_JSON_VALUE})
    ResponseEntity<PlayerModel> players(@PathVariable final int campaignId, @PathVariable final int playerId) {
        Campaign campaign = campaignRepository.findById(campaignId).orElseThrow(() -> new CampaignNotFoundException(campaignId));
        Player player = playerRepository.findByCampaignAndId(campaign, playerId).orElseThrow(() -> new PlayerNotFoundException(playerId));

        PlayerModel playerModel = assembler.toModel(
                player.setInventory(itemTransactionRepository.getInventory(playerId, campaignId))
        );
        playerModel.add(linkTo(PlayerController.class, campaignId).withRel("players"));
        playerModel.add(linkTo(RootController.class).withRel("index"));

        return ResponseEntity.ok(playerModel);
    }

    @GetMapping(value = "/{playerId}", produces = {TEXT_HTML_VALUE})
    String players(@PathVariable final int campaignId, @PathVariable final int playerId, Model model) {
        Campaign campaign = campaignRepository.findById(campaignId).orElseThrow(() -> new CampaignNotFoundException(campaignId));
        Player player = playerRepository.findByCampaignAndId(campaign, playerId).orElseThrow(() -> new PlayerNotFoundException(playerId));
        List<Object[]> inventory = itemTransactionRepository.getInventory(playerId, campaignId);
        player.setInventory(inventory);

        model.addAttribute(player);
        return "player";
    }

    @PostMapping(consumes = {HAL_JSON_VALUE})
    @RolesAllowed("user")
    ResponseEntity<PlayerModel> addPlayer(@RequestHeader String Authorization, @RequestBody Player newPlayer, @PathVariable final int campaignId) {
        Campaign campaign = campaignRepository.findById(campaignId).orElseThrow(() -> new CampaignNotFoundException(campaignId));
        Player player = playerRepository.save(newPlayer.setCampaign(campaign));
        PlayerModel playerModel = assembler.toModel(player);
        playerModel.add(linkTo(PlayerController.class, campaignId).withRel("players"));
        playerModel.add(linkTo(RootController.class).withRel("index"));

        return ResponseEntity.created(playerModel.getRequiredLink("self").toUri()).body(playerModel);
    }
}
