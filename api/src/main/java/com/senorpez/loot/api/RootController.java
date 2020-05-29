package com.senorpez.loot.api;

import org.springframework.hateoas.RepresentationModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.hateoas.MediaTypes.HAL_JSON_VALUE;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

@RequestMapping(
        value = "/",
        method = GET,
        produces = {HAL_JSON_VALUE}
)
@RestController
public class RootController {
    @RequestMapping
    ResponseEntity<RepresentationModel<?>> root() {
        final RepresentationModel<?> root = new RepresentationModel<>();
        root.add(linkTo(RootController.class).withSelfRel());
        root.add(linkTo(RootController.class).withRel("index"));
        root.add(linkTo(methodOn(CampaignController.class).campaigns()).withRel("campaigns"));
        root.add(linkTo(ItemController.class).withRel("lootitems"));
        return ResponseEntity.ok(root);
    }
}
