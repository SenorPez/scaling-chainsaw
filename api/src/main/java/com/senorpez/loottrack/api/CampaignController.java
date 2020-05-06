package com.senorpez.loottrack.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.springframework.hateoas.MediaTypes.HAL_JSON_VALUE;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

@RequestMapping(
        value = "/campaigns",
        method = GET,
        produces = {HAL_JSON_VALUE}
)
@RestController
public class CampaignController {
    @Autowired
    private CampaignRepository campaignRepository;

    private CampaignModelAssembler assembler = new CampaignModelAssembler(CampaignController.class, CampaignModel.class);

    @GetMapping
    ResponseEntity<CollectionModel<CampaignModel>> campaigns() {
        CollectionModel<CampaignModel> campaignModels = new CollectionModel<>(
                StreamSupport
                        .stream(campaignRepository.findAll().spliterator(), false)
                        .map(assembler::toModel)
                        .collect(Collectors.toList()));
        campaignModels.add(linkTo(CampaignController.class).withSelfRel());
        campaignModels.add(linkTo(RootController.class).withRel("index"));

        return ResponseEntity.ok(campaignModels);
    }

    @GetMapping("/{campaignId}")
    ResponseEntity<CampaignModel> campaigns(@PathVariable final int campaignId) {
        Campaign campaign = campaignRepository.findById(campaignId).orElseThrow(() -> new CampaignNotFoundException(campaignId));
        CampaignModel campaignModel = assembler.toModel(campaign);
        campaignModel.add(linkTo(CampaignController.class).withRel("campaigns"));
        campaignModel.add(linkTo(RootController.class).withRel("index"));

        return ResponseEntity.ok(campaignModel);
    }

    @PostMapping(consumes = {HAL_JSON_VALUE})
    ResponseEntity<CampaignModel> addCampaign(@RequestBody Campaign newCampaign) {
        Campaign campaign = campaignRepository.save(newCampaign);
        CampaignModel campaignModel = assembler.toModel(campaign);
        campaignModel.add(linkTo(CampaignController.class).withRel("campaigns"));
        campaignModel.add(linkTo(RootController.class).withRel("index"));

        return ResponseEntity.created(campaignModel.getRequiredLink("self").toUri()).body(campaignModel);
    }
}
