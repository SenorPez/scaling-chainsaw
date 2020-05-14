package com.senorpez.loottrack.api;

import org.springframework.hateoas.UriTemplate;
import org.springframework.hateoas.mediatype.hal.DefaultCurieProvider;

class Common {
    public static final DefaultCurieProvider curieProvider = new DefaultCurieProvider("loottable-api", UriTemplate.of("/docs/reference.html#resources-loottable-{rel}"));
}
