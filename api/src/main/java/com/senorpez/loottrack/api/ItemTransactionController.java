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
    private ItemRepository itemRepository;

    @Autowired
    private ItemTransactionRepository itemTransactionRepository;

    private final PlayerModelAssembler playerModelAssembler = new PlayerModelAssembler(PlayerController.class, PlayerModel.class);
    private final ItemTransactionAssembler assembler = new ItemTransactionAssembler(ItemTransactionController.class, ItemTransactionModel.class);

    @PostMapping(consumes = {HAL_JSON_VALUE})
    ResponseEntity<PlayerModel> addItemTransaction(@RequestBody ItemTransactionInsert incomingValue, @PathVariable final int campaignId, @PathVariable final int playerId) {
        ItemTransaction newItemTransaction = new ItemTransaction();

        Campaign campaign = campaignRepository.findById(campaignId).orElseThrow(() -> new CampaignNotFoundException(campaignId));
        Player player = playerRepository.findByCampaignAndId(campaign, playerId).orElseThrow(() -> new PlayerNotFoundException(playerId));
        newItemTransaction.setPlayer(player);

        Item item = itemRepository.findById(incomingValue.getItem()).orElseThrow(() -> new ItemNotFoundException(incomingValue.getItem()));
        newItemTransaction.setItem(item);

        newItemTransaction
                .setQuantity(incomingValue.getQuantity())
                .setRemark(incomingValue.getRemark());

        itemTransactionRepository.save(newItemTransaction);

        Player updatedPlayer = playerRepository.findByCampaignAndId(campaign, playerId).orElseThrow(() -> new PlayerNotFoundException(playerId));
        PlayerModel playerModel = playerModelAssembler.toModel(
                updatedPlayer.setInventory(itemTransactionRepository.getInventory(playerId, campaignId))
        );
        playerModel.add(linkTo(PlayerController.class, campaignId).withRel("players"));
        playerModel.add(linkTo(RootController.class).withRel("index"));

        return ResponseEntity.created(playerModel.getRequiredLink("self").toUri()).body(playerModel);
    }

    static class ItemTransactionInsert {
        private int item;
        private int quantity;
        private String remark;

        public int getItem() {
            return item;
        }

        public ItemTransactionInsert setItem(int item) {
            this.item = item;
            return this;
        }

        public int getQuantity() {
            return quantity;
        }

        public ItemTransactionInsert setQuantity(int quantity) {
            this.quantity = quantity;
            return this;
        }

        public String getRemark() {
            return remark;
        }

        public ItemTransactionInsert setRemark(String remark) {
            this.remark = remark;
            return this;
        }
    }
}
