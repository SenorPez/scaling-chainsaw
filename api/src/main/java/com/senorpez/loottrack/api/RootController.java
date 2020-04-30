package com.senorpez.loottrack.api;

import org.springframework.hateoas.RepresentationModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.hateoas.MediaTypes.HAL_JSON_VALUE;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

@RequestMapping(
        value = "/",
        method = GET
)
@RestController
public class RootController {
    @RequestMapping(produces = {HAL_JSON_VALUE})
    ResponseEntity<RepresentationModel<?>> root() {
        final RepresentationModel<?> root = new RepresentationModel<>();
        root.add(linkTo(RootController.class).withSelfRel());
        root.add(linkTo(RootController.class).withRel("index"));
        return ResponseEntity.ok(root);
    }
}
