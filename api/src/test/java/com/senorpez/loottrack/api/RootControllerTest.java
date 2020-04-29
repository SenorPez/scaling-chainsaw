package com.senorpez.loottrack.api;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.restdocs.JUnitRestDocumentation;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static com.senorpez.loottrack.api.SupportedMediaTypes.*;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.hamcrest.Matchers.hasEntry;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.snippet.Attributes.key;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class RootControllerTest {
    private MockMvc mockMvc;
    private static final MediaType INVALID_MEDIA_TYPE = new MediaType("application", "vnd.senorpez.loottrack.vx+json", UTF_8);

    private static final String OBJECT_SCHEMA = "root.schema.json";
    private static final String ERROR_SCHEMA = "error.schema.json";

    @Rule
    public final JUnitRestDocumentation restDocumentation = new JUnitRestDocumentation();

    private final RestDocumentationResultHandler createLinksSnippets = document("links",
            preprocessRequest(prettyPrint()),
            preprocessResponse(prettyPrint()),
            relaxedLinks(halLinks(),
                    linkWithRel("self").description("This resource."),
                    linkWithRel("index").description("API index."),
                    linkWithRel("curies").description("Compact URI resolver.")));

    @Before
    public void setUp() {
        this.mockMvc = MockMvcBuilders
                .standaloneSetup(new RootController())
//                .setMessageConverters(HALMessageConverter.getConverter(Collections.singletonList(ALL)))
                .apply(documentationConfiguration(this.restDocumentation))
                .build();
    }

    @Test
    public void getRoot_ValidAcceptHeader() throws Exception {
        mockMvc.perform(get("/").accept(LOOTTABLE_API))
                .andExpect(status().isOk())
                .andExpect(content().contentType(LOOTTABLE_API))
                .andExpect(content().string(matchesJsonSchemaInClasspath(OBJECT_SCHEMA)))
                .andExpect(jsonPath("$._links.self", hasEntry("href", "http://localhost:8080/")))
                .andDo(createLinksSnippets)
                .andDo(document("index",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(
                                headerWithName("Accept")
                                        .description("Accept header.")
                                        .attributes(key("acceptvalue").value(LOOTTABLE_API_VALUE)))));
    }

    @Test
    public void getRoot_FallbackAcceptHeader() throws Exception {
        mockMvc.perform(get("/").accept(FALLBACK))
                .andExpect(status().isOk())
                .andExpect(content().contentType(FALLBACK))
                .andExpect(content().string(matchesJsonSchemaInClasspath(OBJECT_SCHEMA)))
                .andExpect(jsonPath("$._links.self", hasEntry("href", "http://localhost:8080/")));
    }

    @Test
    public void getRoot_InvalidAcceptHeader() throws Exception {
        mockMvc.perform(get("/").accept(INVALID_MEDIA_TYPE))
                .andExpect(status().isNotAcceptable())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(content().string(matchesJsonSchemaInClasspath(ERROR_SCHEMA)));
    }

    @Test
    public void getRoot_InvalidMethod() throws Exception {
        mockMvc.perform(put("/").accept(LOOTTABLE_API))
                .andExpect(status().isMethodNotAllowed())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(content().string(matchesJsonSchemaInClasspath(ERROR_SCHEMA)));
    }
}
