package com.senorpez.loot.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpHeaders;
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
    private static final MediaType INVALID_MEDIA_TYPE = new MediaType("application", "vnd.senorpez.loot.vx+json", UTF_8);
    private static final String OBJECT_SCHEMA = "character.schema.json";
    private static final String ERROR_SCHEMA = "error.schema.json";

    private static final Campaign FIRST_CAMPAIGN = new Campaign()
            .setId(1)
            .setName("First Campaign");

    private static final com.senorpez.loot.api.Character FIRST_CHARACTER = new com.senorpez.loot.api.Character()
            .setId(1)
            .setName("First Character")
            .setCampaign(FIRST_CAMPAIGN);

    private static final Item FIRST_ITEM = new Item()
            .setId(1)
            .setName("Gold");

    private static final ItemTransaction FIRST_TRANSACTION = new ItemTransaction()
            .setCharacter(FIRST_CHARACTER)
            .setItem(FIRST_ITEM)
            .setQuantity(5);

    @InjectMocks
    ItemTransactionController itemTransactionController;

    @Mock
    CampaignRepository campaignRepository;

    @Mock
    CharacterRepository characterRepository;

    @Mock
    ItemRepository itemRepository;

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
    public void postItemTransaction_ValidCampaign_ValidCharacter_ValidContentType() throws Exception {
        when(campaignRepository.findById(anyInt())).thenReturn(Optional.of(FIRST_CAMPAIGN));
        when(characterRepository.findByCampaignAndId(any(), anyInt())).thenReturn(Optional.of(FIRST_CHARACTER));
        when(itemRepository.findById(anyInt())).thenReturn(Optional.of(FIRST_ITEM));
        when(itemTransactionRepository.save(any(ItemTransaction.class))).thenReturn(FIRST_TRANSACTION);

        ObjectMapper objectMapper = new ObjectMapper();

        mockMvc
                .perform(
                        post(String.format("/campaigns/%d/characters/%d/itemtransactions", FIRST_CAMPAIGN.getId(), FIRST_CHARACTER.getId()))
                                .contentType(HAL_JSON)
                                .content(objectMapper.writeValueAsString(FIRST_TRANSACTION))
                                .header(HttpHeaders.AUTHORIZATION, "bearer 12345")
                )
                .andExpect(status().isCreated())
                .andExpect(content().contentType(HAL_JSON))
                .andExpect(content().string(matchesJsonSchemaInClasspath(OBJECT_SCHEMA)))
                .andExpect(jsonPath("$.id", is(FIRST_CHARACTER.getId())))
                .andExpect(jsonPath("$.name", is(FIRST_CHARACTER.getName())))
                .andExpect(jsonPath("$._links.index", hasEntry("href", "http://localhost:8080")))
                .andExpect(jsonPath("$._links.self", hasEntry("href", String.format("http://localhost:8080/campaigns/%d/characters/%d", FIRST_CAMPAIGN.getId(), FIRST_CHARACTER.getId()))))
                .andExpect(jsonPath("$._links.curies", everyItem(
                        allOf(
                                hasEntry("href", (Object) "http://localhost:8080/docs/reference.html#resources-loot-{rel}"),
                                hasEntry("name", (Object) "loot-api"),
                                hasEntry("templated", (Object) true)
                        )
                )))
                .andExpect(jsonPath("$._links.loot-api:characters", hasEntry("href", String.format("http://localhost:8080/campaigns/%d/characters", FIRST_CAMPAIGN.getId()))));

        verify(itemTransactionRepository, times(1)).save(any(ItemTransaction.class));
        verify(itemTransactionRepository, times(1)).getInventory(anyInt(), anyInt());
        verifyNoMoreInteractions(itemTransactionRepository);
    }

    @Test
    public void postItemTransaction_ValidCampaign_ValidCharacter_InvalidContentType() throws Exception {
        when(itemTransactionRepository.save(any(ItemTransaction.class))).thenReturn(FIRST_TRANSACTION);

        ObjectMapper objectMapper = new ObjectMapper();

        mockMvc
                .perform(
                        post(String.format("/campaigns/%d/characters/%d/itemtransactions", FIRST_CAMPAIGN.getId(), FIRST_CHARACTER.getId()))
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
    public void postItemTransaction_ValidCampaign_ValidCharacter_InvalidSyntax() throws Exception {
        when(itemTransactionRepository.save(any(ItemTransaction.class))).thenReturn(FIRST_TRANSACTION);
        String invalidJson = "{\"name\": \"}";

        mockMvc
                .perform(
                        post(String.format("/campaigns/%d/characters/%d/itemtransactions", FIRST_CAMPAIGN.getId(), FIRST_CHARACTER.getId()))
                                .contentType(HAL_JSON)
                                .content(invalidJson)
                                .header(HttpHeaders.AUTHORIZATION, "bearer 12345")
                )
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(APPLICATION_PROBLEM_JSON))
                .andExpect(content().string(matchesJsonSchemaInClasspath(ERROR_SCHEMA)))
                .andExpect(jsonPath("$.code", is(BAD_REQUEST.value())))
                .andExpect(jsonPath("$.message", is(BAD_REQUEST.getReasonPhrase())));

        verifyNoInteractions(itemTransactionRepository);
    }

    @Test
    public void postItemTransaction_ValidCampaign_InvalidCharacter_ValidContentType() throws Exception {
        when(campaignRepository.findById(anyInt())).thenReturn(Optional.of(FIRST_CAMPAIGN));
        when(characterRepository.findById(anyInt())).thenThrow(new CharacterNotFoundException(8675309));
        when(itemTransactionRepository.save(any(ItemTransaction.class))).thenReturn(FIRST_TRANSACTION);

        ObjectMapper objectMapper = new ObjectMapper();

        mockMvc
                .perform(
                        post(String.format("/campaigns/%d/characters/8675309/itemtransactions", FIRST_CAMPAIGN.getId()))
                                .contentType(HAL_JSON)
                                .content(objectMapper.writeValueAsString(FIRST_TRANSACTION))
                                .header(HttpHeaders.AUTHORIZATION, "bearer 12345")
                )
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(APPLICATION_PROBLEM_JSON))
                .andExpect(content().string(matchesJsonSchemaInClasspath(ERROR_SCHEMA)))
                .andExpect(jsonPath("$.code", is(NOT_FOUND.value())))
                .andExpect(jsonPath("$.message", is(NOT_FOUND.getReasonPhrase())))
                .andExpect(jsonPath("$.detail", is(String.format("Character with ID of %d not found", 8675309))));

        verify(campaignRepository, times(1)).findById(anyInt());
        verify(characterRepository, times(1)).findByCampaignAndId(any(), anyInt());
        verifyNoMoreInteractions(campaignRepository, characterRepository);
        verifyNoInteractions(itemTransactionRepository);
    }

    @Test
    public void postItemTransaction_ValidCampaign_InvalidCharacter_InvalidContentType() throws Exception {
        when(campaignRepository.findById(anyInt())).thenReturn(Optional.of(FIRST_CAMPAIGN));
        when(characterRepository.findById(anyInt())).thenThrow(new CharacterNotFoundException(8675309));
        when(itemTransactionRepository.save(any(ItemTransaction.class))).thenReturn(FIRST_TRANSACTION);

        ObjectMapper objectMapper = new ObjectMapper();

        mockMvc
                .perform(
                        post(String.format("/campaigns/%d/characters/8675309/itemtransactions", FIRST_CAMPAIGN.getId()))
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

        verifyNoInteractions(campaignRepository, characterRepository, itemTransactionRepository);
    }

    @Test
    public void postItemTransaction_ValidCampaign_InvalidCharacter_InvalidSyntax() throws Exception {
        when(campaignRepository.findById(anyInt())).thenReturn(Optional.of(FIRST_CAMPAIGN));
        when(characterRepository.findById(anyInt())).thenThrow(new CharacterNotFoundException(8675309));
        when(itemTransactionRepository.save(any(ItemTransaction.class))).thenReturn(FIRST_TRANSACTION);
        String invalidJson = "{\"name\": \"";

        mockMvc
                .perform(
                        post(String.format("/campaigns/%d/characters/8675309/itemtransactions", FIRST_CAMPAIGN.getId()))
                                .contentType(HAL_JSON)
                                .content(invalidJson)
                                .header(HttpHeaders.AUTHORIZATION, "bearer 12345")
                )
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(APPLICATION_PROBLEM_JSON))
                .andExpect(content().string(matchesJsonSchemaInClasspath(ERROR_SCHEMA)))
                .andExpect(jsonPath("$.code", is(BAD_REQUEST.value())))
                .andExpect(jsonPath("$.message", is(BAD_REQUEST.getReasonPhrase())));

        verifyNoInteractions(campaignRepository, characterRepository, itemTransactionRepository);
    }

    @Test
    public void postItemTransaction_InvalidCampaign_ValidCharacter_ValidContentType() throws Exception {
        when(campaignRepository.findById(anyInt())).thenThrow(new CampaignNotFoundException(8675309));
        when(characterRepository.findByCampaignAndId(any(), anyInt())).thenReturn(Optional.of(FIRST_CHARACTER));
        when(itemTransactionRepository.save(any(ItemTransaction.class))).thenReturn(FIRST_TRANSACTION);

        ObjectMapper objectMapper = new ObjectMapper();

        mockMvc
                .perform(
                        post(String.format("/campaigns/8675309/characters/%d/itemtransactions", FIRST_CHARACTER.getId()))
                                .contentType(HAL_JSON)
                                .content(objectMapper.writeValueAsString(FIRST_TRANSACTION))
                                .header(HttpHeaders.AUTHORIZATION, "bearer 12345")
                )
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(APPLICATION_PROBLEM_JSON))
                .andExpect(content().string(matchesJsonSchemaInClasspath(ERROR_SCHEMA)))
                .andExpect(jsonPath("$.code", is(NOT_FOUND.value())))
                .andExpect(jsonPath("$.message", is(NOT_FOUND.getReasonPhrase())))
                .andExpect(jsonPath("$.detail", is(String.format("Campaign with ID of %d not found", 8675309))));

        verify(campaignRepository, times(1)).findById(anyInt());
        verifyNoMoreInteractions(campaignRepository);
        verifyNoInteractions(characterRepository, itemTransactionRepository);
    }

    @Test
    public void postItemTransaction_InvalidCampaign_ValidCharacter_InvalidContentType() throws Exception {
        when(campaignRepository.findById(anyInt())).thenThrow(new CampaignNotFoundException(8675309));
        when(characterRepository.findByCampaignAndId(any(), anyInt())).thenReturn(Optional.of(FIRST_CHARACTER));
        when(itemTransactionRepository.save(any(ItemTransaction.class))).thenReturn(FIRST_TRANSACTION);

        ObjectMapper objectMapper = new ObjectMapper();

        mockMvc
                .perform(
                        post(String.format("/campaigns/8675309/characters/%d/itemtransactions", FIRST_CHARACTER.getId()))
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

        verifyNoInteractions(campaignRepository, characterRepository, itemTransactionRepository);
    }

    @Test
    public void postItemTransaction_InvalidCampaign_ValidCharacter_InvalidSyntax() throws Exception {
        when(campaignRepository.findById(anyInt())).thenThrow(new CampaignNotFoundException(8675309));
        when(characterRepository.findByCampaignAndId(any(), anyInt())).thenReturn(Optional.of(FIRST_CHARACTER));
        when(itemTransactionRepository.save(any(ItemTransaction.class))).thenReturn(FIRST_TRANSACTION);
        String invalidJson = "{\"name\": \"";

        mockMvc
                .perform(
                        post(String.format("/campaigns/8675309/characters/%d/itemtransactions", FIRST_CHARACTER.getId()))
                                .contentType(HAL_JSON)
                                .content(invalidJson)
                                .header(HttpHeaders.AUTHORIZATION, "bearer 12345")
                )
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(APPLICATION_PROBLEM_JSON))
                .andExpect(content().string(matchesJsonSchemaInClasspath(ERROR_SCHEMA)))
                .andExpect(jsonPath("$.code", is(BAD_REQUEST.value())))
                .andExpect(jsonPath("$.message", is(BAD_REQUEST.getReasonPhrase())));

        verifyNoInteractions(campaignRepository, characterRepository, itemTransactionRepository);
    }
}
