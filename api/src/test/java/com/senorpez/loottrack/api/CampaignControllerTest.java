package com.senorpez.loottrack.api;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.restdocs.JUnitRestDocumentation;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.Collections;

import static com.senorpez.loottrack.api.RootControllerTest.commonLinks;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.hateoas.MediaTypes.HAL_JSON;
import static org.springframework.hateoas.MediaTypes.HAL_JSON_VALUE;
import static org.springframework.http.MediaType.ALL;
import static org.springframework.http.MediaType.APPLICATION_PROBLEM_JSON;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.snippet.Attributes.key;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class CampaignControllerTest {
    private MockMvc mockMvc;
    private static final MediaType INVALID_MEDIA_TYPE = new MediaType("application", "vnd.senorpez.loottrack.vx+json", UTF_8);
    private static final String COLLECTION_SCHEMA = "campaigns.schema.json";
    private static final String OBJECT_SCHEMA = "campaign.schema.json";
    private static final String ERROR_SCHEMA = "error.schema.json";

    private static final Campaign FIRST_CAMPAIGN = new Campaign()
            .setId(1)
            .setName("First Campaign");
    private static final Campaign SECOND_CAMPAIGN = new Campaign()
            .setId(2)
            .setName("Second Campaign");

    @InjectMocks
    CampaignController campaignController;

    @Mock
    CampaignRepository campaignRepository;

    @Rule
    public final JUnitRestDocumentation restDocumentation = new JUnitRestDocumentation();

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        this.mockMvc = MockMvcBuilders
                .standaloneSetup(campaignController)
                .setMessageConverters(HalMessageConverter.getConverter(Collections.singletonList(ALL)))
                .setControllerAdvice(new APIExceptionHandler())
                .apply(documentationConfiguration(this.restDocumentation))
                .build();
    }

    @Test
    public void getAllCampaigns_ValidAcceptHeader() throws Exception {
        when(campaignRepository.findAll()).thenReturn(Arrays.asList(FIRST_CAMPAIGN, SECOND_CAMPAIGN));

        mockMvc.perform(get("/campaigns").accept(HAL_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(HAL_JSON))
                .andExpect(content().string(matchesJsonSchemaInClasspath(COLLECTION_SCHEMA)))
                .andExpect(jsonPath("$._embedded.loottable-api:campaign", hasItem(
                        allOf(
                                hasEntry("id", (Object) FIRST_CAMPAIGN.getId()),
                                hasEntry("name", (Object) FIRST_CAMPAIGN.getName()),
                                hasEntry(equalTo("_links"),
                                        hasEntry(equalTo("self"),
                                                hasEntry("href", String.format("http://localhost:8080/campaigns/%d", FIRST_CAMPAIGN.getId()))))))))
                .andExpect(jsonPath("$._embedded.loottable-api:campaign", hasItem(
                        allOf(
                                hasEntry("id", (Object) SECOND_CAMPAIGN.getId()),
                                hasEntry("name", (Object) SECOND_CAMPAIGN.getName()),
                                hasEntry(equalTo("_links"),
                                        hasEntry(equalTo("self"),
                                                hasEntry("href", String.format("http://localhost:8080/campaigns/%d", SECOND_CAMPAIGN.getId()))))))))
                .andExpect(jsonPath("$._links.index", hasEntry("href", "http://localhost:8080")))
                .andExpect(jsonPath("$._links.self", hasEntry("href", "http://localhost:8080/campaigns")))
                .andExpect(jsonPath("$._links.curies", everyItem(
                        allOf(
                                hasEntry("href", (Object) "http://localhost:8080/docs/reference.html#resources-loottable-{rel}"),
                                hasEntry("name", (Object) "loottable-api"),
                                hasEntry("templated", (Object) true)))))
                .andDo(document("campaigns",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(
                                headerWithName("Accept")
                                        .description("Accept header.")
                                        .attributes(key("acceptvalue").value(HAL_JSON_VALUE))),
                        responseFields(
                                fieldWithPath("_embedded.loottable-api:campaign").description("Campaign resource."),
                                fieldWithPath("_embedded.loottable-api:campaign[].id").description("Campaign ID number."),
                                fieldWithPath("_embedded.loottable-api:campaign[].name").description("Campaign name."),
                                subsectionWithPath("_links").ignored(),
                                subsectionWithPath("_embedded.loottable-api:campaign[]._links").ignored()),
                        commonLinks));
    }

    @Test
    public void getAllCampaigns_InvalidMethod() throws Exception {
        mockMvc.perform(put("/campaigns").accept(HAL_JSON))
                .andExpect(status().isMethodNotAllowed())
                .andExpect(content().contentType(APPLICATION_PROBLEM_JSON))
                .andExpect(content().string(matchesJsonSchemaInClasspath(ERROR_SCHEMA)));
    }

    @Test
    public void getAllCampaigns_InvalidAcceptHeader() throws Exception {
        mockMvc.perform(get("/campaigns").accept(INVALID_MEDIA_TYPE))
                .andExpect(status().isNotAcceptable())
                .andExpect(content().contentType(APPLICATION_PROBLEM_JSON));
    }
}
