package com.senorpez.loottrack.api;

import org.springframework.http.MediaType;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

class SupportedMediaTypes {
    static final MediaType LOOTTABLE_API = new MediaType("application", "vnd.senorpez.loottable.v0+json", UTF_8);
    static final String LOOTTABLE_API_VALUE = "application/vnd.senorpez.loottable.v0+json; charset=UTF-8";

    static final MediaType FALLBACK = APPLICATION_JSON;
    static final String FALLBACK_VALUE = APPLICATION_JSON_VALUE;
}
