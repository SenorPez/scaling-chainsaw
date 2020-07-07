package com.senorpez.loot.api.controller;

import com.senorpez.loot.api.entity.CampaignEntity;
import com.senorpez.loot.api.exception.CampaignNotFoundException;
import com.senorpez.loot.api.model.CampaignModel;
import com.senorpez.loot.api.model.CampaignModelAssembler;
import com.senorpez.loot.api.repository.CampaignRepository;
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
        final CampaignEntity campaignEntity = campaignRepository.findById(campaignId).orElseThrow(() -> new CampaignNotFoundException(campaignId));
        final CampaignModel campaignModel = assembler.toSingleModel(campaignEntity);
        return ResponseEntity.ok(campaignModel);
    }

    @PostMapping(consumes = {HAL_JSON_VALUE})
    @RolesAllowed("user")
    ResponseEntity<CampaignModel> addCampaign(@RequestHeader String Authorization, @RequestBody CampaignEntity newCampaign) {
        final CampaignEntity campaignEntity = campaignRepository.save(newCampaign);
        final CampaignModel campaignModel = assembler.toSingleModel(campaignEntity);
        return ResponseEntity.created(campaignModel.getRequiredLink("self").toUri()).body(campaignModel);
    }
}
