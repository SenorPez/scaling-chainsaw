package com.senorpez.loottrack.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
