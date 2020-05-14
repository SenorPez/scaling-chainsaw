package com.senorpez.loottrack.api;

import com.fasterxml.jackson.databind.ObjectMapper;
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

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import static com.senorpez.loottrack.api.RootControllerTest.commonLinks;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.hateoas.MediaTypes.HAL_JSON;
import static org.springframework.hateoas.MediaTypes.HAL_JSON_VALUE;
import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.MediaType.ALL;
import static org.springframework.http.MediaType.APPLICATION_PROBLEM_JSON;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
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

    private static final Object[] FIRST_INVENTORY_ARRAY = new Object[]{"Gold", new BigDecimal(329)};
    private static final Object[] SECOND_INVENTORY_ARRAY = new Object[]{"Likes", new BigDecimal(69)};

    private static final Player FIRST_PLAYER = new Player()
            .setId(1)
            .setName("First Player")
            .setCampaign(FIRST_CAMPAIGN)
            .setInventory(Arrays.asList(FIRST_INVENTORY_ARRAY, SECOND_INVENTORY_ARRAY));

    private static final Player SECOND_PLAYER = new Player()
            .setId(2)
            .setName("Second Player")
            .setCampaign(FIRST_CAMPAIGN)
            .setInventory(Arrays.asList(FIRST_INVENTORY_ARRAY, SECOND_INVENTORY_ARRAY));

    @InjectMocks
    PlayerController playerController;

    @Mock
    CampaignRepository campaignRepository;

    @Mock
    PlayerRepository playerRepository;

    @Mock
    ItemTransactionRepository itemTransactionRepository;

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
                                fieldWithPath("_embedded.loottable-api:player[].inventory").description("Inventory."),
                                subsectionWithPath("_links").ignored(),
                                subsectionWithPath("_embedded.loottable-api:player[]._links").ignored(),
                                subsectionWithPath("_embedded.loottable-api:player[].inventory[]").ignored()
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
    public void getAllPlayers_InvalidCampaign_ValidAcceptHeader() throws Exception {
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
    public void getAllPlayers_InvalidCampaign_InvalidMethod() throws Exception {
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

    @Test
    public void getSinglePlayer_ValidCampaign_ValidPlayer_ValidAcceptHeader() throws Exception {
        when(campaignRepository.findById(anyInt())).thenReturn(Optional.of(FIRST_CAMPAIGN));
        when(playerRepository.findByCampaignAndId(any(), anyInt())).thenReturn(Optional.of(FIRST_PLAYER));
        when(itemTransactionRepository.getInventory(anyInt(), anyInt())).thenReturn(Arrays.asList(FIRST_INVENTORY_ARRAY, SECOND_INVENTORY_ARRAY));

        mockMvc.perform(get(String.format("/campaigns/%d/players/%d", FIRST_CAMPAIGN.getId(), FIRST_PLAYER.getId())).accept(HAL_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(HAL_JSON))
                .andExpect(content().string(matchesJsonSchemaInClasspath(OBJECT_SCHEMA)))
                .andExpect(jsonPath("$.id", is(FIRST_PLAYER.getId())))
                .andExpect(jsonPath("$.name", is(FIRST_PLAYER.getName())))
                .andExpect(jsonPath("$._links.index", hasEntry("href", "http://localhost:8080")))
                .andExpect(jsonPath("$._links.self", hasEntry("href", String.format("http://localhost:8080/campaigns/%d/players/%d", FIRST_CAMPAIGN.getId(), FIRST_PLAYER.getId()))))
                .andExpect(jsonPath("$._links.curies", everyItem(
                        allOf(
                                hasEntry("href", (Object) "http://localhost:8080/docs/reference.html#resources-loottable-{rel}"),
                                hasEntry("name", (Object) "loottable-api"),
                                hasEntry("templated", (Object) true)
                        )
                )))
                .andExpect(jsonPath("$._links.loottable-api:players", hasEntry("href", String.format("http://localhost:8080/campaigns/%d/players", FIRST_CAMPAIGN.getId()))))
                .andDo(document(
                        "player",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(
                                headerWithName("Accept")
                                        .description("Accept header.")
                                        .attributes(key("acceptvalue").value(HAL_JSON_VALUE))
                        ),
                        responseFields(
                                fieldWithPath("id").description("ID number."),
                                fieldWithPath("name").description("Campaign name."),
                                subsectionWithPath("inventory").ignored(),
                                subsectionWithPath("_links").ignored()
                        ),
                        links(
                                halLinks(),
                                linkWithRel("self").description("This resource."),
                                linkWithRel("loottable-api:players").description("List of player resources."),
                                linkWithRel("index").description("Index resource."),
                                linkWithRel("curies").description("Curies.")
                        )
                ));

        verify(campaignRepository, times(1)).findById(anyInt());
        verifyNoMoreInteractions(campaignRepository);
        verify(playerRepository, times(1)).findByCampaignAndId(any(), anyInt());
        verifyNoMoreInteractions(playerRepository);
    }

    @Test
    public void getSinglePlayer_ValidCampaign_ValidPlayer_InvalidAcceptHeader() throws Exception {
        when(campaignRepository.findById(anyInt())).thenReturn(Optional.of(FIRST_CAMPAIGN));
        when(playerRepository.findByCampaignAndId(any(), anyInt())).thenReturn(Optional.of(FIRST_PLAYER));

        mockMvc.perform(get(String.format("/campaigns/%d/players/%d", FIRST_CAMPAIGN.getId(), FIRST_PLAYER.getId())).accept(INVALID_MEDIA_TYPE))
                .andExpect(status().isNotAcceptable())
                .andExpect(content().contentType(APPLICATION_PROBLEM_JSON))
                .andExpect(content().string(matchesJsonSchemaInClasspath(ERROR_SCHEMA)))
                .andExpect(jsonPath("$.code", is(NOT_ACCEPTABLE.value())))
                .andExpect(jsonPath("$.message", is(NOT_ACCEPTABLE.getReasonPhrase())))
                .andExpect(jsonPath("$.detail", is("Accept header must be \"application/hal+json\"")));

        verifyNoInteractions(campaignRepository);
        verifyNoInteractions(playerRepository);
    }

    @Test
    public void getSinglePlayer_ValidCampaign_ValidPlayer_InvalidMethod() throws Exception {
        when(campaignRepository.findById(anyInt())).thenReturn(Optional.of(FIRST_CAMPAIGN));
        when(playerRepository.findByCampaignAndId(any(), anyInt())).thenReturn(Optional.of(FIRST_PLAYER));

        mockMvc.perform(put(String.format("/campaigns/%d/players/%d", FIRST_CAMPAIGN.getId(), FIRST_PLAYER.getId())).accept(HAL_JSON))
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
    public void getSinglePlayer_InvalidCampaign_ValidPlayer_ValidAcceptHeader() throws Exception {
        when(campaignRepository.findById(anyInt())).thenThrow(new CampaignNotFoundException(8675309));
        when(playerRepository.findByCampaignAndId(any(), anyInt())).thenReturn(Optional.of(FIRST_PLAYER));

        mockMvc.perform(get(String.format("/campaigns/8675309/players/%d", FIRST_PLAYER.getId())).accept(HAL_JSON))
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
    public void getSinglePlayer_InvalidCampaign_ValidPlayer_InvalidAcceptHeader() throws Exception {
        when(campaignRepository.findById(anyInt())).thenThrow(new CampaignNotFoundException(8675309));
        when(playerRepository.findByCampaignAndId(any(), anyInt())).thenReturn(Optional.of(FIRST_PLAYER));

        mockMvc.perform(get(String.format("/campaigns/8675309/players/%d", FIRST_PLAYER.getId())).accept(INVALID_MEDIA_TYPE))
                .andExpect(status().isNotAcceptable())
                .andExpect(content().contentType(APPLICATION_PROBLEM_JSON))
                .andExpect(content().string(matchesJsonSchemaInClasspath(ERROR_SCHEMA)))
                .andExpect(jsonPath("$.code", is(NOT_ACCEPTABLE.value())))
                .andExpect(jsonPath("$.message", is(NOT_ACCEPTABLE.getReasonPhrase())))
                .andExpect(jsonPath("$.detail", is("Accept header must be \"application/hal+json\"")));

        verifyNoInteractions(campaignRepository, playerRepository);
    }

    @Test
    public void getSinglePlayer_InvalidCampaign_ValidPlayer_InvalidMethod() throws Exception {
        when(campaignRepository.findById(anyInt())).thenThrow(new CampaignNotFoundException(8675309));
        when(playerRepository.findByCampaignAndId(any(), anyInt())).thenReturn(Optional.of(FIRST_PLAYER));

        mockMvc.perform(put(String.format("/campaigns/8675309/players/%d", FIRST_PLAYER.getId())).accept(HAL_JSON))
                .andExpect(status().isMethodNotAllowed())
                .andExpect(content().contentType(APPLICATION_PROBLEM_JSON))
                .andExpect(content().string(matchesJsonSchemaInClasspath(ERROR_SCHEMA)))
                .andExpect(jsonPath("$.code", is(METHOD_NOT_ALLOWED.value())))
                .andExpect(jsonPath("$.message", is(METHOD_NOT_ALLOWED.getReasonPhrase())))
                .andExpect(jsonPath("$.detail", is("Method not allowed.")));

        verifyNoInteractions(campaignRepository, playerRepository);
    }

    @Test
    public void getSinglePlayer_ValidCampaign_InvalidPlayer_ValidAcceptHeader() throws Exception {
        when(campaignRepository.findById(anyInt())).thenReturn(Optional.of(FIRST_CAMPAIGN));
        when(playerRepository.findByCampaignAndId(any(), anyInt())).thenThrow(new PlayerNotFoundException(8675309));

        mockMvc.perform(get(String.format("/campaigns/%d/players/8675309", FIRST_CAMPAIGN.getId())).accept(HAL_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(APPLICATION_PROBLEM_JSON))
                .andExpect(content().string(matchesJsonSchemaInClasspath(ERROR_SCHEMA)))
                .andExpect(jsonPath("$.code", is(NOT_FOUND.value())))
                .andExpect(jsonPath("$.message", is(NOT_FOUND.getReasonPhrase())))
                .andExpect(jsonPath("$.detail", is(String.format("Player with ID of %d not found", 8675309))));

        verify(campaignRepository, times(1)).findById(anyInt());
        verify(playerRepository, times(1)).findByCampaignAndId(any(), anyInt());
        verifyNoMoreInteractions(campaignRepository, playerRepository);
    }

    @Test
    public void getSinglePlayer_ValidCampaign_InvalidPlayer_InvalidAcceptHeader() throws Exception {
        when(campaignRepository.findById(anyInt())).thenReturn(Optional.of(FIRST_CAMPAIGN));
        when(playerRepository.findByCampaignAndId(any(), anyInt())).thenThrow(new PlayerNotFoundException(8675309));

        mockMvc.perform(get(String.format("/campaigns/%d/players/8675309", FIRST_CAMPAIGN.getId())).accept(INVALID_MEDIA_TYPE))
                .andExpect(status().isNotAcceptable())
                .andExpect(content().contentType(APPLICATION_PROBLEM_JSON))
                .andExpect(content().string(matchesJsonSchemaInClasspath(ERROR_SCHEMA)))
                .andExpect(jsonPath("$.code", is(NOT_ACCEPTABLE.value())))
                .andExpect(jsonPath("$.message", is(NOT_ACCEPTABLE.getReasonPhrase())))
                .andExpect(jsonPath("$.detail", is("Accept header must be \"application/hal+json\"")));

        verifyNoInteractions(campaignRepository, playerRepository);
    }

    @Test
    public void getSinglePlayer_ValidCampaign_InvalidPlayer_InvalidMethod() throws Exception {
        when(campaignRepository.findById(anyInt())).thenReturn(Optional.of(FIRST_CAMPAIGN));
        when(playerRepository.findByCampaignAndId(any(), anyInt())).thenThrow(new PlayerNotFoundException(8675309));

        mockMvc.perform(put(String.format("/campaigns/%d/players/8675309", FIRST_CAMPAIGN.getId())).accept(HAL_JSON))
                .andExpect(status().isMethodNotAllowed())
                .andExpect(content().contentType(APPLICATION_PROBLEM_JSON))
                .andExpect(content().string(matchesJsonSchemaInClasspath(ERROR_SCHEMA)))
                .andExpect(jsonPath("$.code", is(METHOD_NOT_ALLOWED.value())))
                .andExpect(jsonPath("$.message", is(METHOD_NOT_ALLOWED.getReasonPhrase())))
                .andExpect(jsonPath("$.detail", is("Method not allowed.")));

        verifyNoInteractions(campaignRepository, playerRepository);
    }

    @Test
    public void postPlayer_ValidCampaign_ValidContentType() throws Exception {
        when(campaignRepository.findById(anyInt())).thenReturn(Optional.of(FIRST_CAMPAIGN));
        when(playerRepository.save(any(Player.class))).thenReturn(FIRST_PLAYER);

        ObjectMapper objectMapper = new ObjectMapper();
Integer i = 4;
        mockMvc.perform(
                post(String.format("/campaigns/%d/players", FIRST_CAMPAIGN.getId()))
                        .contentType(HAL_JSON)
                        .content(objectMapper.writeValueAsString(FIRST_PLAYER))
        )
                .andExpect(status().isCreated())
                .andExpect(content().contentType(HAL_JSON))
                .andExpect(content().string(matchesJsonSchemaInClasspath(OBJECT_SCHEMA)))
                .andExpect(jsonPath("$.id", is(FIRST_PLAYER.getId())))
                .andExpect(jsonPath("$.name", is(FIRST_PLAYER.getName())))
                .andExpect(jsonPath("$._links.index", hasEntry("href", "http://localhost:8080")))
                .andExpect(jsonPath("$._links.self", hasEntry("href", String.format("http://localhost:8080/campaigns/%d/players/%d", FIRST_CAMPAIGN.getId(), FIRST_PLAYER.getId()))))
                .andExpect(jsonPath("$._links.curies", everyItem(
                        allOf(
                                hasEntry("href", (Object) "http://localhost:8080/docs/reference.html#resources-loottable-{rel}"),
                                hasEntry("name", (Object) "loottable-api"),
                                hasEntry("templated", (Object) true)
                        )
                )))
                .andExpect(jsonPath("$._links.loottable-api:players", hasEntry("href", String.format("http://localhost:8080/campaigns/%d/players", FIRST_CAMPAIGN.getId()))))
                .andDo(document(
                        "player",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(
                                headerWithName("Content-Type")
                                        .description("Content Type header.")
                                        .attributes(key("contenttype").value(HAL_JSON_VALUE))
                        ),
                        responseFields(
                                fieldWithPath("id").description("ID number."),
                                fieldWithPath("name").description("Player name."),
                                subsectionWithPath("inventory").ignored(),
                                subsectionWithPath("_links").ignored()
                        ),
                        links(
                                halLinks(),
                                linkWithRel("self").description("This resource."),
                                linkWithRel("loottable-api:players").description("List of player resources."),
                                linkWithRel("index").description("Index resource."),
                                linkWithRel("curies").description("Curies.")
                        )
                ));

        verify(campaignRepository, times(1)).findById(anyInt());
        verify(playerRepository, times(1)).save(any(Player.class));
        verifyNoMoreInteractions(campaignRepository, playerRepository);
    }

    @Test
    public void postPlayer_ValidCampaign_InvalidContentType() throws Exception {
        when(campaignRepository.findById(anyInt())).thenReturn(Optional.of(FIRST_CAMPAIGN));
        when(playerRepository.save(any(Player.class))).thenReturn(FIRST_PLAYER);

        ObjectMapper objectMapper = new ObjectMapper();

        mockMvc
                .perform(
                        post(String.format("/campaigns/%d/players", FIRST_CAMPAIGN.getId()))
                                .contentType(INVALID_MEDIA_TYPE)
                                .content(objectMapper.writeValueAsString(FIRST_PLAYER))
                )
                .andExpect(status().isUnsupportedMediaType())
                .andExpect(content().contentType(APPLICATION_PROBLEM_JSON))
                .andExpect(content().string(matchesJsonSchemaInClasspath(ERROR_SCHEMA)))
                .andExpect(jsonPath("$.code", is(UNSUPPORTED_MEDIA_TYPE.value())))
                .andExpect(jsonPath("$.message", is(UNSUPPORTED_MEDIA_TYPE.getReasonPhrase())))
                .andExpect(jsonPath(
                        "$.detail",
                        is(String.format("Content type '%s' not supported", INVALID_MEDIA_TYPE.toString()))
                ));

        verifyNoInteractions(campaignRepository, playerRepository);
    }

    @Test
    public void postPlayer_ValidCampaign_InvalidSyntax() throws Exception {
        when(campaignRepository.findById(anyInt())).thenReturn(Optional.of(FIRST_CAMPAIGN));
        when(playerRepository.save(any(Player.class))).thenReturn(FIRST_PLAYER);
        String invalidJson = "{\"name\": \"}";

        mockMvc
                .perform(
                        post(String.format("/campaigns/%d/players", FIRST_CAMPAIGN.getId()))
                                .contentType(HAL_JSON)
                                .content(invalidJson)
                )
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(APPLICATION_PROBLEM_JSON))
                .andExpect(content().string(matchesJsonSchemaInClasspath(ERROR_SCHEMA)))
                .andExpect(jsonPath("$.code", is(BAD_REQUEST.value())))
                .andExpect(jsonPath("$.message", is(BAD_REQUEST.getReasonPhrase())));

        verifyNoInteractions(campaignRepository, playerRepository);
    }

    private static class Inventory {
        private String name;
        private Integer quantity;

        public String getName() {
            return name;
        }

        public Inventory setName(String name) {
            this.name = name;
            return this;
        }

        public Integer getQuantity() {
            return quantity;
        }

        public Inventory setQuantity(Integer quantity) {
            this.quantity = quantity;
            return this;
        }
    }
}
