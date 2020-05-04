package com.senorpez.loottrack.api;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.MediaType;
import org.springframework.restdocs.JUnitRestDocumentation;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import static com.senorpez.loottrack.api.RootControllerTest.commonLinks;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.*;
import static org.springframework.hateoas.MediaTypes.HAL_JSON;
import static org.springframework.hateoas.MediaTypes.HAL_JSON_VALUE;
import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.MediaType.ALL;
import static org.springframework.http.MediaType.APPLICATION_PROBLEM_JSON;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.linkWithRel;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.snippet.Attributes.key;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class PlayerControllerTest {
    private MockMvc mockMvc;
    private static final MediaType INVALID_MEDIA_TYPE = new MediaType("application", "vnd.senorpez.loottrack.vx+json", UTF_8);
    private static final String COLLECTION_SCHEMA = "players.schema.json";
    private static final String OBJECT_SCHEMA = "player.schema.json";
    private static final String ERROR_SCHEMA = "error.schema.json";

    private static final Campaign FIRST_CAMPAIGN = new Campaign()
            .setId(1)
            .setName("First Campaign");

    private static final Player FIRST_PLAYER = new Player()
            .setId(1)
            .setName("First Player")
            .setCampaign(FIRST_CAMPAIGN);
    private static final Player SECOND_PLAYER = new Player()
            .setId(2)
            .setName("Second Player")
            .setCampaign(FIRST_CAMPAIGN);

    @InjectMocks
    PlayerController playerController;

    @Mock
    CampaignRepository campaignRepository;

    @Mock
    PlayerRepository playerRepository;

    @Rule
    public final JUnitRestDocumentation restDocumentation = new JUnitRestDocumentation();

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        this.mockMvc = MockMvcBuilders
                .standaloneSetup(playerController)
                .setMessageConverters(HalMessageConverter.getConverter(Collections.singletonList(ALL)))
                .setControllerAdvice(new APIExceptionHandler())
                .apply(documentationConfiguration(this.restDocumentation))
                .build();
    }

    @Test
    public void getAllPlayers_ValidCampaign_ValidAcceptHeader() throws Exception {
        when(campaignRepository.findById(anyInt())).thenReturn(Optional.of(FIRST_CAMPAIGN));
        when(playerRepository.findByCampaign(any())).thenReturn(Arrays.asList(FIRST_PLAYER, SECOND_PLAYER));

        mockMvc.perform(get(String.format("/campaigns/%d/players", FIRST_CAMPAIGN.getId())).accept(HAL_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(HAL_JSON))
                .andExpect(content().string(matchesJsonSchemaInClasspath(COLLECTION_SCHEMA)))
                .andExpect(jsonPath("$._embedded.loottable-api:player", hasItem(
                        allOf(
                                hasEntry("id", (Object) FIRST_PLAYER.getId()),
                                hasEntry("name", (Object) FIRST_PLAYER.getName()),
                                hasEntry(equalTo("_links"),
                                        hasEntry(equalTo("self"),
                                                hasEntry("href", String.format("http://localhost:8080/campaigns/%d/players/%d", FIRST_CAMPAIGN.getId(), FIRST_PLAYER.getId()))
                                        )
                                )
                        )
                )))
                .andExpect(jsonPath("$._embedded.loottable-api:player", hasItem(
                        allOf(
                                hasEntry("id", (Object) SECOND_PLAYER.getId()),
                                hasEntry("name", (Object) SECOND_PLAYER.getName()),
                                hasEntry(equalTo("_links"),
                                        hasEntry(equalTo("self"),
                                                hasEntry("href", String.format("http://localhost:8080/campaigns/%d/players/%d", FIRST_CAMPAIGN.getId(), SECOND_PLAYER.getId()))
                                        )
                                )
                        )
                )))
                .andExpect(jsonPath("$._links.index", hasEntry("href", "http://localhost:8080")))
                .andExpect(jsonPath("$._links.self", hasEntry("href", String.format("http://localhost:8080/campaigns/%d/players", FIRST_CAMPAIGN.getId()))))
                .andExpect(jsonPath("$._links.curies", everyItem(
                        allOf(
                                hasEntry("href", (Object) "http://localhost:8080/docs/reference.html#resources-loottable-{rel}"),
                                hasEntry("name", (Object) "loottable-api"),
                                hasEntry("templated", (Object) true)
                        )
                )))
                .andDo(document("player",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(
                                headerWithName("Accept")
                                        .description("Accept header.")
                                        .attributes(key("acceptvalue").value(HAL_JSON_VALUE))
                        ),
                        responseFields(
                                fieldWithPath("_embedded.loottable-api:player").description("Player resource."),
                                fieldWithPath("_embedded.loottable-api:player[].id").description("Player ID number."),
                                fieldWithPath("_embedded.loottable-api:player[].name").description("Player name."),
                                subsectionWithPath("_links").ignored(),
                                subsectionWithPath("_embedded.loottable-api:player[]._links").ignored()
                        ),
                        commonLinks.and(
                                linkWithRel("loottable-api:campaign").description("Campaign resource.")
                        )
                ));

        verify(campaignRepository, times(1)).findById(anyInt());
        verifyNoMoreInteractions(campaignRepository);

        verify(playerRepository, times(1)).findByCampaign(any());
        verifyNoMoreInteractions(playerRepository);
    }

    @Test
    public void getAllPlayers_ValidCampaign_InvalidAcceptHeader() throws Exception {
        when(campaignRepository.findById(anyInt())).thenReturn(Optional.of(FIRST_CAMPAIGN));
        when(playerRepository.findByCampaign(any())).thenReturn(Arrays.asList(FIRST_PLAYER, SECOND_PLAYER));

        mockMvc.perform(get(String.format("/campaigns/%d/players", FIRST_CAMPAIGN.getId())).accept(INVALID_MEDIA_TYPE))
                .andExpect(status().isNotAcceptable())
                .andExpect(content().contentType(APPLICATION_PROBLEM_JSON))
                .andExpect(content().string(matchesJsonSchemaInClasspath(ERROR_SCHEMA)))
                .andExpect(jsonPath("$.code", is(NOT_ACCEPTABLE.value())))
                .andExpect(jsonPath("$.message", is(NOT_ACCEPTABLE.getReasonPhrase())))
                .andExpect(jsonPath("$.detail", is(String.format("Accept header must be \"%s\"", MediaTypes.HAL_JSON.toString()))));

        verifyNoInteractions(campaignRepository);
        verifyNoInteractions(playerRepository);
    }

    @Test
    public void getAllPlayers_ValidCampaign_InvalidMethod() throws Exception {
        when(campaignRepository.findById(anyInt())).thenReturn(Optional.of(FIRST_CAMPAIGN));
        when(playerRepository.findByCampaign(any())).thenReturn(Arrays.asList(FIRST_PLAYER, SECOND_PLAYER));

        mockMvc.perform(put(String.format("/campaigns/%d/players", FIRST_CAMPAIGN.getId())).accept(HAL_JSON))
                .andExpect(status().isMethodNotAllowed())
                .andExpect(content().contentType(APPLICATION_PROBLEM_JSON))
                .andExpect(content().string(matchesJsonSchemaInClasspath(ERROR_SCHEMA)))
                .andExpect(jsonPath("$.code", is(METHOD_NOT_ALLOWED.value())))
                .andExpect(jsonPath("$.message", is(METHOD_NOT_ALLOWED.getReasonPhrase())))
                .andExpect(jsonPath("$.detail", is("Method not allowed.")));

        verifyNoInteractions(campaignRepository);
        verifyNoInteractions(playerRepository);
    }

    @Test
    public void getAllPlayer_InvalidCampaign_ValidAcceptHeader() throws Exception {
        when(campaignRepository.findById(anyInt())).thenThrow(new CampaignNotFoundException(8675309));
        when(playerRepository.findByCampaign(any())).thenThrow(new CampaignNotFoundException(8675309));

        mockMvc.perform(get("/campaigns/8675309/players").accept(HAL_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(APPLICATION_PROBLEM_JSON))
                .andExpect(content().string(matchesJsonSchemaInClasspath(ERROR_SCHEMA)))
                .andExpect(jsonPath("$.code", is(NOT_FOUND.value())))
                .andExpect(jsonPath("$.message", is(NOT_FOUND.getReasonPhrase())))
                .andExpect(jsonPath("$.detail", is(String.format("Campaign with ID of %d not found", 8675309))));

        verify(campaignRepository, times(1)).findById(anyInt());
        verifyNoMoreInteractions(campaignRepository);

        verifyNoInteractions(playerRepository);
    }

    @Test
    public void getAllPlayers_InvalidCampaign_InvalidAcceptHeader() throws Exception {
        when(campaignRepository.findById(anyInt())).thenThrow(new CampaignNotFoundException(8675309));
        when(playerRepository.findByCampaign(any())).thenThrow(new CampaignNotFoundException(8675309));

        mockMvc.perform(get("/campaigns/8675309/players").accept(INVALID_MEDIA_TYPE))
                .andExpect(status().isNotAcceptable())
                .andExpect(content().contentType(APPLICATION_PROBLEM_JSON))
                .andExpect(content().string(matchesJsonSchemaInClasspath(ERROR_SCHEMA)))
                .andExpect(jsonPath("$.code", is(NOT_ACCEPTABLE.value())))
                .andExpect(jsonPath("$.message", is(NOT_ACCEPTABLE.getReasonPhrase())))
                .andExpect(jsonPath("$.detail", is(String.format("Accept header must be \"%s\"", MediaTypes.HAL_JSON.toString()))));

        verifyNoInteractions(campaignRepository);
        verifyNoInteractions(playerRepository);
    }

    @Test
    public void getAllPlayer_InvalidCampaign_InvalidMethod() throws Exception {
        when(campaignRepository.findById(anyInt())).thenThrow(new CampaignNotFoundException(8675309));
        when(playerRepository.findByCampaign(any())).thenThrow(new CampaignNotFoundException(8675309));

        mockMvc.perform(put("/campaigns/8675309/players").accept(HAL_JSON))
                .andExpect(status().isMethodNotAllowed())
                .andExpect(content().contentType(APPLICATION_PROBLEM_JSON))
                .andExpect(content().string(matchesJsonSchemaInClasspath(ERROR_SCHEMA)))
                .andExpect(jsonPath("$.code", is(METHOD_NOT_ALLOWED.value())))
                .andExpect(jsonPath("$.message", is(METHOD_NOT_ALLOWED.getReasonPhrase())))
                .andExpect(jsonPath("$.detail", is("Method not allowed.")));

        verifyNoInteractions(campaignRepository);
        verifyNoInteractions(playerRepository);
    }
}
