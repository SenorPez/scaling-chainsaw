package com.senorpez.loot.api.controller;

import com.senorpez.loot.api.CampaignRepository;
import com.senorpez.loot.api.entity.Campaign;
import com.senorpez.loot.api.exception.CampaignNotFoundException;
import com.senorpez.loot.api.model.CampaignModel;
import com.senorpez.loot.api.model.CampaignModelAssembler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;

import static org.springframework.hateoas.MediaTypes.HAL_JSON_VALUE;

@RequestMapping(
        value = "/campaigns",
        produces = {HAL_JSON_VALUE}
)
@RestController
public class CampaignController {
    @Autowired
    private CampaignRepository campaignRepository;

    private final CampaignModelAssembler assembler = new CampaignModelAssembler(CampaignController.class, CampaignModel.class);

    @GetMapping
    ResponseEntity<CollectionModel<CampaignModel>> campaigns() {
        final CollectionModel<CampaignModel> campaignModels = assembler.toCollectionModel(campaignRepository.findAll());
        return ResponseEntity.ok(campaignModels);
    }

    @GetMapping("/{campaignId}")
    public ResponseEntity<CampaignModel> campaigns(@PathVariable final int campaignId) {
        final Campaign campaign = campaignRepository.findById(campaignId).orElseThrow(() -> new CampaignNotFoundException(campaignId));
        final CampaignModel campaignModel = assembler.toSingleModel(campaign);
        return ResponseEntity.ok(campaignModel);
    }

    @PostMapping(consumes = {HAL_JSON_VALUE})
    @RolesAllowed("user")
    ResponseEntity<CampaignModel> addCampaign(@RequestHeader String Authorization, @RequestBody Campaign newCampaign) {
        final Campaign campaign = campaignRepository.save(newCampaign);
        final CampaignModel campaignModel = assembler.toSingleModel(campaign);
        return ResponseEntity.created(campaignModel.getRequiredLink("self").toUri()).body(campaignModel);
    }
}
