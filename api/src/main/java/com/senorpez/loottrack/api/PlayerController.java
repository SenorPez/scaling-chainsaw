package com.senorpez.loottrack.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.stream.Collectors;

import static org.springframework.hateoas.MediaTypes.HAL_JSON_VALUE;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RequestMapping(
        value = "/campaigns/{campaignId}/players",
        produces = {HAL_JSON_VALUE}
)
@RestController
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

    @GetMapping("/{playerId}")
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

    @PostMapping(consumes = {HAL_JSON_VALUE})
    ResponseEntity<PlayerModel> addPlayer(@RequestBody Player newPlayer, @PathVariable final int campaignId) {
        Campaign campaign = campaignRepository.findById(campaignId).orElseThrow(() -> new CampaignNotFoundException(campaignId));
        Player player = playerRepository.save(newPlayer.setCampaign(campaign));
        PlayerModel playerModel = assembler.toModel(player);
        playerModel.add(linkTo(PlayerController.class, campaignId).withRel("players"));
        playerModel.add(linkTo(RootController.class).withRel("index"));

        return ResponseEntity.created(playerModel.getRequiredLink("self").toUri()).body(playerModel);
    }
}
