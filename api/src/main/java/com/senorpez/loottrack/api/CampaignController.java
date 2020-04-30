package com.senorpez.loottrack.api;

import org.springframework.hateoas.CollectionModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;

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
    @RequestMapping
    ResponseEntity<CollectionModel<CampaignModel>> campaigns() {
        CampaignModelAssembler assembler = new CampaignModelAssembler(CampaignController.class, CampaignModel.class);
        CollectionModel<CampaignModel> campaignModels = new CollectionModel<>(Arrays.asList(
                assembler.toModel(new Campaign(1, "Test")),
                assembler.toModel(new Campaign(2, "Thing"))));
        campaignModels.add(linkTo(CampaignController.class).withSelfRel());
        campaignModels.add(linkTo(RootController.class).withRel("index"));

        return ResponseEntity.ok(campaignModels);
    }
}
