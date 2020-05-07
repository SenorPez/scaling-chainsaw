package com.senorpez.loottrack.api;

import com.fasterxml.jackson.databind.ObjectMapper;
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

import java.util.Collections;
import java.util.Optional;

import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;
import static org.springframework.hateoas.MediaTypes.HAL_JSON;
import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.MediaType.ALL;
import static org.springframework.http.MediaType.APPLICATION_PROBLEM_JSON;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class ItemTransactionControllerTest {
    private MockMvc mockMvc;
    private static final MediaType INVALID_MEDIA_TYPE = new MediaType("application", "vnd.senorpez.loottrack.vx+json", UTF_8);
    private static final String OBJECT_SCHEMA = "player.schema.json";
    private static final String ERROR_SCHEMA = "error.schema.json";

    private static final Campaign FIRST_CAMPAIGN = new Campaign()
            .setId(1)
            .setName("First Campaign");

    private static final Player FIRST_PLAYER = new Player()
            .setId(1)
            .setName("First Player")
            .setCampaign(FIRST_CAMPAIGN);

    private static final Item FIRST_ITEM = new Item()
            .setId(1)
            .setName("Gold");

    private static final ItemTransaction FIRST_TRANSACTION = new ItemTransaction()
            .setCampaign(FIRST_CAMPAIGN)
            .setPlayer(FIRST_PLAYER)
            .setItem(FIRST_ITEM)
            .setQuantity(5);

    @InjectMocks
    ItemTransactionController itemTransactionController;

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
                .standaloneSetup(itemTransactionController)
                .setMessageConverters(HalMessageConverter.getConverter(Collections.singletonList(ALL)))
                .setControllerAdvice(new APIExceptionHandler())
                .apply(documentationConfiguration(this.restDocumentation))
                .build();
    }


    @Test
    public void postItemTransaction_ValidCampaign_ValidPlayer_ValidContentType() throws Exception {
        when(campaignRepository.findById(anyInt())).thenReturn(Optional.of(FIRST_CAMPAIGN));
        when(playerRepository.findByCampaignAndId(any(), anyInt())).thenReturn(Optional.of(FIRST_PLAYER));
        when(itemTransactionRepository.save(any(ItemTransaction.class))).thenReturn(FIRST_TRANSACTION);

        ObjectMapper objectMapper = new ObjectMapper();

        mockMvc
                .perform(
                        post(String.format("/campaigns/%d/players/%d/itemtransactions", FIRST_CAMPAIGN.getId(), FIRST_PLAYER.getId()))
                                .contentType(HAL_JSON)
                                .content(objectMapper.writeValueAsString(FIRST_TRANSACTION))
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
                .andExpect(jsonPath("$._links.loottable-api:players", hasEntry("href", String.format("http://localhost:8080/campaigns/%d/players", FIRST_CAMPAIGN.getId()))));

        verify(itemTransactionRepository, times(1)).save(any(ItemTransaction.class));
        verifyNoMoreInteractions(itemTransactionRepository);
    }

    @Test
    public void postItemTransaction_ValidCampaign_ValidPlayer_InvalidContentType() throws Exception {
        when(itemTransactionRepository.save(any(ItemTransaction.class))).thenReturn(FIRST_TRANSACTION);

        ObjectMapper objectMapper = new ObjectMapper();

        mockMvc
                .perform(
                        post(String.format("/campaigns/%d/players/%d/itemtransactions", FIRST_CAMPAIGN.getId(), FIRST_PLAYER.getId()))
                                .contentType(INVALID_MEDIA_TYPE)
                                .content(objectMapper.writeValueAsString(FIRST_TRANSACTION))
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

        verifyNoInteractions(itemTransactionRepository);
    }

    @Test
    public void postItemTransaction_ValidCampaign_ValidPlayer_InvalidSyntax() throws Exception {
        when(itemTransactionRepository.save(any(ItemTransaction.class))).thenReturn(FIRST_TRANSACTION);
        String invalidJson = "{\"name\": \"}";

        mockMvc
                .perform(
                        post(String.format("/campaigns/%d/players/%d/itemtransactions", FIRST_CAMPAIGN.getId(), FIRST_PLAYER.getId()))
                                .contentType(HAL_JSON)
                                .content(invalidJson)
                )
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(APPLICATION_PROBLEM_JSON))
                .andExpect(content().string(matchesJsonSchemaInClasspath(ERROR_SCHEMA)))
                .andExpect(jsonPath("$.code", is(BAD_REQUEST.value())))
                .andExpect(jsonPath("$.message", is(BAD_REQUEST.getReasonPhrase())));

        verifyNoInteractions(itemTransactionRepository);
    }

    @Test
    public void postItemTransaction_ValidCampaign_InvalidPlayer_ValidContentType() throws Exception {
        when(campaignRepository.findById(anyInt())).thenReturn(Optional.of(FIRST_CAMPAIGN));
        when(playerRepository.findById(anyInt())).thenThrow(new PlayerNotFoundException(8675309));
        when(itemTransactionRepository.save(any(ItemTransaction.class))).thenReturn(FIRST_TRANSACTION);

        ObjectMapper objectMapper = new ObjectMapper();

        mockMvc
                .perform(
                        post(String.format("/campaigns/%d/players/8675309/itemtransactions", FIRST_CAMPAIGN.getId()))
                                .contentType(HAL_JSON)
                                .content(objectMapper.writeValueAsString(FIRST_TRANSACTION))
                )
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(APPLICATION_PROBLEM_JSON))
                .andExpect(content().string(matchesJsonSchemaInClasspath(ERROR_SCHEMA)))
                .andExpect(jsonPath("$.code", is(NOT_FOUND.value())))
                .andExpect(jsonPath("$.message", is(NOT_FOUND.getReasonPhrase())))
                .andExpect(jsonPath("$.detail", is(String.format("Player with ID of %d not found", 8675309))));

        verify(campaignRepository, times(1)).findById(anyInt());
        verify(playerRepository, times(1)).findByCampaignAndId(any(), anyInt());
        verifyNoMoreInteractions(campaignRepository, playerRepository);
        verifyNoInteractions(itemTransactionRepository);
    }

    @Test
    public void postItemTransaction_ValidCampaign_InvalidPlayer_InvalidContentType() throws Exception {
        when(campaignRepository.findById(anyInt())).thenReturn(Optional.of(FIRST_CAMPAIGN));
        when(playerRepository.findById(anyInt())).thenThrow(new PlayerNotFoundException(8675309));
        when(itemTransactionRepository.save(any(ItemTransaction.class))).thenReturn(FIRST_TRANSACTION);

        ObjectMapper objectMapper = new ObjectMapper();

        mockMvc
                .perform(
                        post(String.format("/campaigns/%d/players/8675309/itemtransactions", FIRST_CAMPAIGN.getId()))
                                .contentType(INVALID_MEDIA_TYPE)
                                .content(objectMapper.writeValueAsString(FIRST_TRANSACTION)))
                .andExpect(status().isUnsupportedMediaType())
                .andExpect(content().contentType(APPLICATION_PROBLEM_JSON))
                .andExpect(content().string(matchesJsonSchemaInClasspath(ERROR_SCHEMA)))
                .andExpect(jsonPath("$.code", is(UNSUPPORTED_MEDIA_TYPE.value())))
                .andExpect(jsonPath("$.message", is(UNSUPPORTED_MEDIA_TYPE.getReasonPhrase())))
                .andExpect(jsonPath(
                        "$.detail",
                        is(String.format("Content type '%s' not supported", INVALID_MEDIA_TYPE.toString()))
                ));

        verifyNoInteractions(campaignRepository, playerRepository, itemTransactionRepository);
    }

    @Test
    public void postItemTransaction_ValidCampaign_InvalidPlayer_InvalidSyntax() throws Exception {
        when(campaignRepository.findById(anyInt())).thenReturn(Optional.of(FIRST_CAMPAIGN));
        when(playerRepository.findById(anyInt())).thenThrow(new PlayerNotFoundException(8675309));
        when(itemTransactionRepository.save(any(ItemTransaction.class))).thenReturn(FIRST_TRANSACTION);
        String invalidJson = "{\"name\": \"";

        mockMvc
                .perform(
                        post(String.format("/campaigns/%d/players/8675309/itemtransactions", FIRST_CAMPAIGN.getId()))
                                .contentType(HAL_JSON)
                                .content(invalidJson)
                )
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(APPLICATION_PROBLEM_JSON))
                .andExpect(content().string(matchesJsonSchemaInClasspath(ERROR_SCHEMA)))
                .andExpect(jsonPath("$.code", is(BAD_REQUEST.value())))
                .andExpect(jsonPath("$.message", is(BAD_REQUEST.getReasonPhrase())));

        verifyNoInteractions(campaignRepository, playerRepository, itemTransactionRepository);
    }

    @Test
    public void postItemTransaction_InvalidCampaign_ValidPlayer_ValidContentType() throws Exception {
        when(campaignRepository.findById(anyInt())).thenThrow(new CampaignNotFoundException(8675309));
        when(playerRepository.findByCampaignAndId(any(), anyInt())).thenReturn(Optional.of(FIRST_PLAYER));
        when(itemTransactionRepository.save(any(ItemTransaction.class))).thenReturn(FIRST_TRANSACTION);

        ObjectMapper objectMapper = new ObjectMapper();

        mockMvc
                .perform(
                        post(String.format("/campaigns/8675309/players/%d/itemtransactions", FIRST_PLAYER.getId()))
                                .contentType(HAL_JSON)
                                .content(objectMapper.writeValueAsString(FIRST_TRANSACTION))
                )
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(APPLICATION_PROBLEM_JSON))
                .andExpect(content().string(matchesJsonSchemaInClasspath(ERROR_SCHEMA)))
                .andExpect(jsonPath("$.code", is(NOT_FOUND.value())))
                .andExpect(jsonPath("$.message", is(NOT_FOUND.getReasonPhrase())))
                .andExpect(jsonPath("$.detail", is(String.format("Campaign with ID of %d not found", 8675309))));

        verify(campaignRepository, times(1)).findById(anyInt());
        verifyNoMoreInteractions(campaignRepository);
        verifyNoInteractions(playerRepository, itemTransactionRepository);
    }

    @Test
    public void postItemTransaction_InvalidCampaign_ValidPlayer_InvalidContentType() throws Exception {
        when(campaignRepository.findById(anyInt())).thenThrow(new CampaignNotFoundException(8675309));
        when(playerRepository.findByCampaignAndId(any(), anyInt())).thenReturn(Optional.of(FIRST_PLAYER));
        when(itemTransactionRepository.save(any(ItemTransaction.class))).thenReturn(FIRST_TRANSACTION);

        ObjectMapper objectMapper = new ObjectMapper();

        mockMvc
                .perform(
                        post(String.format("/campaigns/8675309/players/%d/itemtransactions", FIRST_PLAYER.getId()))
                                .contentType(INVALID_MEDIA_TYPE)
                                .content(objectMapper.writeValueAsString(FIRST_TRANSACTION))

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

        verifyNoInteractions(campaignRepository, playerRepository, itemTransactionRepository);
    }

    @Test
    public void postItemTransaction_InvalidCampaign_ValidPlayer_InvalidSyntax() throws Exception {
        when(campaignRepository.findById(anyInt())).thenThrow(new CampaignNotFoundException(8675309));
        when(playerRepository.findByCampaignAndId(any(), anyInt())).thenReturn(Optional.of(FIRST_PLAYER));
        when(itemTransactionRepository.save(any(ItemTransaction.class))).thenReturn(FIRST_TRANSACTION);
        String invalidJson = "{\"name\": \"";

        mockMvc
                .perform(
                        post(String.format("/campaigns/8675309/players/%d/itemtransactions", FIRST_PLAYER.getId()))
                                .contentType(HAL_JSON)
                                .content(invalidJson)
                )
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(APPLICATION_PROBLEM_JSON))
                .andExpect(content().string(matchesJsonSchemaInClasspath(ERROR_SCHEMA)))
                .andExpect(jsonPath("$.code", is(BAD_REQUEST.value())))
                .andExpect(jsonPath("$.message", is(BAD_REQUEST.getReasonPhrase())));

        verifyNoInteractions(campaignRepository, playerRepository, itemTransactionRepository);
    }
}
