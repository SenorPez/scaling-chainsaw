package com.senorpez.loottrack.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.hateoas.MediaTypes.HAL_JSON_VALUE;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@RequestMapping(
        value = "/campaigns/{campaignId}/players/{playerId}/itemtransactions",
        produces = {HAL_JSON_VALUE}
)
@RestController
public class ItemTransactionController {
    @Autowired
    private CampaignRepository campaignRepository;

    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private ItemTransactionRepository itemTransactionRepository;

    private final PlayerModelAssembler playerModelAssembler = new PlayerModelAssembler(PlayerController.class, PlayerModel.class);
    private final ItemTransactionAssembler assembler = new ItemTransactionAssembler(ItemTransactionController.class, ItemTransactionModel.class);

    @PostMapping(consumes = {HAL_JSON_VALUE})
    ResponseEntity<PlayerModel> addItemTransaction(@RequestBody ItemTransaction newItemTransaction, @PathVariable final int campaignId, @PathVariable final int playerId) {
        Campaign campaign = campaignRepository.findById(campaignId).orElseThrow(() -> new CampaignNotFoundException(campaignId));
        Player player = playerRepository.findByCampaignAndId(campaign, playerId).orElseThrow(() -> new PlayerNotFoundException(playerId));
        ItemTransaction itemTransaction = itemTransactionRepository.save(newItemTransaction.setCampaign(campaign).setPlayer(player));

        PlayerModel playerModel = playerModelAssembler.toModel(player);
        playerModel.add(linkTo(PlayerController.class, campaignId).withRel("players"));
        playerModel.add(linkTo(RootController.class).withRel("index"));
        ItemTransactionModel itemTransactionModel = assembler.toModel(itemTransaction);
        itemTransactionModel.add(linkTo(ItemTransactionController.class, campaignId, playerId).withRel("itemtransactions"));
        itemTransactionModel.add(linkTo(RootController.class).withRel("index"));

        return ResponseEntity.created(playerModel.getRequiredLink("self").toUri()).body(playerModel);
    }
}
