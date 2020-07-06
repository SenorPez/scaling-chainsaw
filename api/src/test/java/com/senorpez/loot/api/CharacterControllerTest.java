package com.senorpez.loot.api;

import com.senorpez.loot.api.controller.CharacterController;
import com.senorpez.loot.api.entity.Character;
import com.senorpez.loot.api.exception.CampaignNotFoundException;
import com.senorpez.loot.api.exception.CharacterNotFoundException;
import com.senorpez.loot.api.repository.CampaignRepository;
import com.senorpez.loot.api.repository.CharacterRepository;
import com.senorpez.loot.api.repository.ItemTransactionRepository;
import org.apache.http.HttpHeaders;
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

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;
import java.util.Random;

import static com.senorpez.loot.api.CampaignControllerTest.FIRST_CAMPAIGN;
import static com.senorpez.loot.api.RootControllerTest.commonLinks;
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

public class CharacterControllerTest {
    private MockMvc mockMvc;
    private static final MediaType INVALID_MEDIA_TYPE = new MediaType("application", "vnd.senorpez.loot.vx+json", UTF_8);
    private static final String COLLECTION_SCHEMA = "characters.schema.json";
    private static final String OBJECT_SCHEMA = "character.schema.json";
    private static final String ERROR_SCHEMA = "error.schema.json";

    private static final Object[] FIRST_INVENTORY_ARRAY = new Object[]{1, new BigInteger(String.valueOf(329)), "Gold", null, null, null};
    private static final Object[] SECOND_INVENTORY_ARRAY = new Object[]{2, new BigInteger(String.valueOf(69)), "Likes", null, null, null};

    static final Character FIRST_CHARACTER = new Character(1, FIRST_CAMPAIGN, "First Character", Collections.emptyList());
    static final Character SECOND_CHARACTER = new Character(2, FIRST_CAMPAIGN, "Second Character", Collections.emptyList());
    static final Character NEW_CHARACTER = new Character(new Random().nextInt(), FIRST_CAMPAIGN, "New Character", Collections.emptyList());
    static final String NEW_CHARACTER_JSON = "{\"name\": \"New Character\"}";

    @InjectMocks
    CharacterController characterController;

    @Mock
    CampaignRepository campaignRepository;

    @Mock
    CharacterRepository characterRepository;

    @Mock
    ItemTransactionRepository itemTransactionRepository;

    @Rule
    public final JUnitRestDocumentation restDocumentation = new JUnitRestDocumentation();

    @Before
    public void setUp() {
        FIRST_CAMPAIGN.setCharacters(Arrays.asList(FIRST_CHARACTER, SECOND_CHARACTER));

        MockitoAnnotations.initMocks(this);
        this.mockMvc = MockMvcBuilders
                .standaloneSetup(characterController)
                .setMessageConverters(HalMessageConverter.getConverter(Collections.singletonList(ALL)))
                .setControllerAdvice(new APIExceptionHandler())
                .apply(documentationConfiguration(this.restDocumentation))
                .build();
    }

    @Test
    public void getAllCharacters_ValidCampaign_ValidAcceptHeader() throws Exception {
        when(campaignRepository.findById(anyInt())).thenReturn(Optional.of(FIRST_CAMPAIGN));

        mockMvc.perform(get(String.format("/campaigns/%d/characters", FIRST_CAMPAIGN.getId())).accept(HAL_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(HAL_JSON))
                .andExpect(content().string(matchesJsonSchemaInClasspath(COLLECTION_SCHEMA)))
                .andExpect(jsonPath("$._embedded.loot-api:character", hasItem(
                        allOf(
                                hasEntry("id", (Object) FIRST_CHARACTER.getId()),
                                hasEntry("name", (Object) FIRST_CHARACTER.getName()),
                                hasEntry(equalTo("_links"),
                                        hasEntry(equalTo("self"),
                                                hasEntry("href", String.format("http://localhost:8080/campaigns/%d/characters/%d", FIRST_CAMPAIGN.getId(), FIRST_CHARACTER.getId()))
                                        )
                                )
                        )
                )))
                .andExpect(jsonPath("$._embedded.loot-api:character", hasItem(
                        allOf(
                                hasEntry("id", (Object) SECOND_CHARACTER.getId()),
                                hasEntry("name", (Object) SECOND_CHARACTER.getName()),
                                hasEntry(equalTo("_links"),
                                        hasEntry(equalTo("self"),
                                                hasEntry("href", String.format("http://localhost:8080/campaigns/%d/characters/%d", FIRST_CAMPAIGN.getId(), SECOND_CHARACTER.getId()))
                                        )
                                )
                        )
                )))
                .andExpect(jsonPath("$._links.index", hasEntry("href", "http://localhost:8080")))
                .andExpect(jsonPath("$._links.self", hasEntry("href", String.format("http://localhost:8080/campaigns/%d/characters", FIRST_CAMPAIGN.getId()))))
                .andExpect(jsonPath("$._links.curies", everyItem(
                        allOf(
                                hasEntry("href", (Object) "http://localhost:8080/docs/reference.html#resources-loot-{rel}"),
                                hasEntry("name", (Object) "loot-api"),
                                hasEntry("templated", (Object) true)
                        )
                )))
                .andDo(document("character",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(
                                headerWithName("Accept")
                                        .description("Accept header.")
                                        .attributes(key("acceptvalue").value(HAL_JSON_VALUE))
                        ),
                        responseFields(
                                fieldWithPath("_embedded.loot-api:character").description("Character resource."),
                                fieldWithPath("_embedded.loot-api:character[].id").description("Character ID number."),
                                fieldWithPath("_embedded.loot-api:character[].name").description("Character name."),
                                subsectionWithPath("_links").ignored(),
                                subsectionWithPath("_embedded.loot-api:character[]._links").ignored()
                        ),
                        commonLinks.and(
                                linkWithRel("loot-api:campaign").description("Campaign resource.")
                        )
                ));

        verify(campaignRepository, times(1)).findById(anyInt());
        verifyNoMoreInteractions(campaignRepository);

        verifyNoInteractions(characterRepository);
    }

    @Test
    public void getAllCharacters_ValidCampaign_InvalidAcceptHeader() throws Exception {
        when(campaignRepository.findById(anyInt())).thenReturn(Optional.of(FIRST_CAMPAIGN));

        mockMvc.perform(get(String.format("/campaigns/%d/characters", FIRST_CAMPAIGN.getId())).accept(INVALID_MEDIA_TYPE))
                .andExpect(status().isNotAcceptable())
                .andExpect(content().contentType(APPLICATION_PROBLEM_JSON))
                .andExpect(content().string(matchesJsonSchemaInClasspath(ERROR_SCHEMA)))
                .andExpect(jsonPath("$.code", is(NOT_ACCEPTABLE.value())))
                .andExpect(jsonPath("$.message", is(NOT_ACCEPTABLE.getReasonPhrase())))
                .andExpect(jsonPath("$.detail", is(String.format("Accept header must be \"%s\"", MediaTypes.HAL_JSON.toString()))));

        verifyNoInteractions(campaignRepository);
        verifyNoInteractions(characterRepository);
    }

    @Test
    public void getAllCharacters_ValidCampaign_InvalidMethod() throws Exception {
        when(campaignRepository.findById(anyInt())).thenReturn(Optional.of(FIRST_CAMPAIGN));

        mockMvc.perform(put(String.format("/campaigns/%d/characters", FIRST_CAMPAIGN.getId())).accept(HAL_JSON))
                .andExpect(status().isMethodNotAllowed())
                .andExpect(content().contentType(APPLICATION_PROBLEM_JSON))
                .andExpect(content().string(matchesJsonSchemaInClasspath(ERROR_SCHEMA)))
                .andExpect(jsonPath("$.code", is(METHOD_NOT_ALLOWED.value())))
                .andExpect(jsonPath("$.message", is(METHOD_NOT_ALLOWED.getReasonPhrase())))
                .andExpect(jsonPath("$.detail", is("Method not allowed.")));

        verifyNoInteractions(campaignRepository);
        verifyNoInteractions(characterRepository);
    }

    @Test
    public void getAllCharacters_InvalidCampaign_ValidAcceptHeader() throws Exception {
        when(campaignRepository.findById(anyInt())).thenThrow(new CampaignNotFoundException(8675309));

        mockMvc.perform(get("/campaigns/8675309/characters").accept(HAL_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(APPLICATION_PROBLEM_JSON))
                .andExpect(content().string(matchesJsonSchemaInClasspath(ERROR_SCHEMA)))
                .andExpect(jsonPath("$.code", is(NOT_FOUND.value())))
                .andExpect(jsonPath("$.message", is(NOT_FOUND.getReasonPhrase())))
                .andExpect(jsonPath("$.detail", is(String.format("Campaign with ID of %d not found", 8675309))));

        verify(campaignRepository, times(1)).findById(anyInt());
        verifyNoMoreInteractions(campaignRepository);

        verifyNoInteractions(characterRepository);
    }

    @Test
    public void getAllCharacters_InvalidCampaign_InvalidAcceptHeader() throws Exception {
        when(campaignRepository.findById(anyInt())).thenThrow(new CampaignNotFoundException(8675309));

        mockMvc.perform(get("/campaigns/8675309/characters").accept(INVALID_MEDIA_TYPE))
                .andExpect(status().isNotAcceptable())
                .andExpect(content().contentType(APPLICATION_PROBLEM_JSON))
                .andExpect(content().string(matchesJsonSchemaInClasspath(ERROR_SCHEMA)))
                .andExpect(jsonPath("$.code", is(NOT_ACCEPTABLE.value())))
                .andExpect(jsonPath("$.message", is(NOT_ACCEPTABLE.getReasonPhrase())))
                .andExpect(jsonPath("$.detail", is(String.format("Accept header must be \"%s\"", MediaTypes.HAL_JSON.toString()))));

        verifyNoInteractions(campaignRepository);
        verifyNoInteractions(characterRepository);
    }

    @Test
    public void getAllCharacters_InvalidCampaign_InvalidMethod() throws Exception {
        when(campaignRepository.findById(anyInt())).thenThrow(new CampaignNotFoundException(8675309));

        mockMvc.perform(put("/campaigns/8675309/characters").accept(HAL_JSON))
                .andExpect(status().isMethodNotAllowed())
                .andExpect(content().contentType(APPLICATION_PROBLEM_JSON))
                .andExpect(content().string(matchesJsonSchemaInClasspath(ERROR_SCHEMA)))
                .andExpect(jsonPath("$.code", is(METHOD_NOT_ALLOWED.value())))
                .andExpect(jsonPath("$.message", is(METHOD_NOT_ALLOWED.getReasonPhrase())))
                .andExpect(jsonPath("$.detail", is("Method not allowed.")));

        verifyNoInteractions(campaignRepository);
        verifyNoInteractions(characterRepository);
    }

    @Test
    public void getSingleCharacter_ValidCampaign_ValidCharacter_ValidAcceptHeader() throws Exception {
        when(campaignRepository.findById(anyInt())).thenReturn(Optional.of(FIRST_CAMPAIGN));
        when(characterRepository.findByCampaignAndId(any(), anyInt())).thenReturn(Optional.of(FIRST_CHARACTER));
        when(itemTransactionRepository.getQuantity(anyInt())).thenReturn(1);

        mockMvc.perform(get(String.format("/campaigns/%d/characters/%d", FIRST_CAMPAIGN.getId(), FIRST_CHARACTER.getId())).accept(HAL_JSON))
                .andExpect(status().isOk())
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
                .andExpect(jsonPath("$._links.loot-api:characters", hasEntry("href", String.format("http://localhost:8080/campaigns/%d/characters", FIRST_CAMPAIGN.getId()))))
                .andDo(document(
                        "character",
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
                                linkWithRel("loot-api:characters").description("List of character resources."),
                                linkWithRel("index").description("Index resource."),
                                linkWithRel("curies").description("Curies.")
                        )
                ));

        verify(campaignRepository, times(1)).findById(anyInt());
        verifyNoMoreInteractions(campaignRepository);
        verify(characterRepository, times(1)).findByCampaignAndId(any(), anyInt());
        verifyNoMoreInteractions(characterRepository);
    }

    @Test
    public void getSingleCharacter_ValidCampaign_ValidCharacter_InvalidAcceptHeader() throws Exception {
        when(campaignRepository.findById(anyInt())).thenReturn(Optional.of(FIRST_CAMPAIGN));
        when(characterRepository.findByCampaignAndId(any(), anyInt())).thenReturn(Optional.of(FIRST_CHARACTER));

        mockMvc.perform(get(String.format("/campaigns/%d/characters/%d", FIRST_CAMPAIGN.getId(), FIRST_CHARACTER.getId())).accept(INVALID_MEDIA_TYPE))
                .andExpect(status().isNotAcceptable())
                .andExpect(content().contentType(APPLICATION_PROBLEM_JSON))
                .andExpect(content().string(matchesJsonSchemaInClasspath(ERROR_SCHEMA)))
                .andExpect(jsonPath("$.code", is(NOT_ACCEPTABLE.value())))
                .andExpect(jsonPath("$.message", is(NOT_ACCEPTABLE.getReasonPhrase())))
                .andExpect(jsonPath("$.detail", is("Accept header must be \"application/hal+json\"")));

        verifyNoInteractions(campaignRepository);
        verifyNoInteractions(characterRepository);
    }

    @Test
    public void getSingleCharacter_ValidCampaign_ValidCharacter_InvalidMethod() throws Exception {
        when(campaignRepository.findById(anyInt())).thenReturn(Optional.of(FIRST_CAMPAIGN));
        when(characterRepository.findByCampaignAndId(any(), anyInt())).thenReturn(Optional.of(FIRST_CHARACTER));

        mockMvc.perform(put(String.format("/campaigns/%d/characters/%d", FIRST_CAMPAIGN.getId(), FIRST_CHARACTER.getId())).accept(HAL_JSON))
                .andExpect(status().isMethodNotAllowed())
                .andExpect(content().contentType(APPLICATION_PROBLEM_JSON))
                .andExpect(content().string(matchesJsonSchemaInClasspath(ERROR_SCHEMA)))
                .andExpect(jsonPath("$.code", is(METHOD_NOT_ALLOWED.value())))
                .andExpect(jsonPath("$.message", is(METHOD_NOT_ALLOWED.getReasonPhrase())))
                .andExpect(jsonPath("$.detail", is("Method not allowed.")));

        verifyNoInteractions(campaignRepository);
        verifyNoInteractions(characterRepository);
    }

    @Test
    public void getSingleCharacter_InvalidCampaign_ValidCharacter_ValidAcceptHeader() throws Exception {
        when(campaignRepository.findById(anyInt())).thenThrow(new CampaignNotFoundException(8675309));
        when(characterRepository.findByCampaignAndId(any(), anyInt())).thenReturn(Optional.of(FIRST_CHARACTER));

        mockMvc.perform(get(String.format("/campaigns/8675309/characters/%d", FIRST_CHARACTER.getId())).accept(HAL_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(APPLICATION_PROBLEM_JSON))
                .andExpect(content().string(matchesJsonSchemaInClasspath(ERROR_SCHEMA)))
                .andExpect(jsonPath("$.code", is(NOT_FOUND.value())))
                .andExpect(jsonPath("$.message", is(NOT_FOUND.getReasonPhrase())))
                .andExpect(jsonPath("$.detail", is(String.format("Campaign with ID of %d not found", 8675309))));

        verify(campaignRepository, times(1)).findById(anyInt());
        verifyNoMoreInteractions(campaignRepository);
        verifyNoInteractions(characterRepository);
    }

    @Test
    public void getSingleCharacter_InvalidCampaign_ValidCharacter_InvalidAcceptHeader() throws Exception {
        when(campaignRepository.findById(anyInt())).thenThrow(new CampaignNotFoundException(8675309));
        when(characterRepository.findByCampaignAndId(any(), anyInt())).thenReturn(Optional.of(FIRST_CHARACTER));

        mockMvc.perform(get(String.format("/campaigns/8675309/characters/%d", FIRST_CHARACTER.getId())).accept(INVALID_MEDIA_TYPE))
                .andExpect(status().isNotAcceptable())
                .andExpect(content().contentType(APPLICATION_PROBLEM_JSON))
                .andExpect(content().string(matchesJsonSchemaInClasspath(ERROR_SCHEMA)))
                .andExpect(jsonPath("$.code", is(NOT_ACCEPTABLE.value())))
                .andExpect(jsonPath("$.message", is(NOT_ACCEPTABLE.getReasonPhrase())))
                .andExpect(jsonPath("$.detail", is("Accept header must be \"application/hal+json\"")));

        verifyNoInteractions(campaignRepository, characterRepository);
    }

    @Test
    public void getSingleCharacter_InvalidCampaign_ValidCharacter_InvalidMethod() throws Exception {
        when(campaignRepository.findById(anyInt())).thenThrow(new CampaignNotFoundException(8675309));
        when(characterRepository.findByCampaignAndId(any(), anyInt())).thenReturn(Optional.of(FIRST_CHARACTER));

        mockMvc.perform(put(String.format("/campaigns/8675309/characters/%d", FIRST_CHARACTER.getId())).accept(HAL_JSON))
                .andExpect(status().isMethodNotAllowed())
                .andExpect(content().contentType(APPLICATION_PROBLEM_JSON))
                .andExpect(content().string(matchesJsonSchemaInClasspath(ERROR_SCHEMA)))
                .andExpect(jsonPath("$.code", is(METHOD_NOT_ALLOWED.value())))
                .andExpect(jsonPath("$.message", is(METHOD_NOT_ALLOWED.getReasonPhrase())))
                .andExpect(jsonPath("$.detail", is("Method not allowed.")));

        verifyNoInteractions(campaignRepository, characterRepository);
    }

    @Test
    public void getSingleCharacter_ValidCampaign_InvalidCharacter_ValidAcceptHeader() throws Exception {
        when(campaignRepository.findById(anyInt())).thenReturn(Optional.of(FIRST_CAMPAIGN));
        when(characterRepository.findByCampaignAndId(any(), anyInt())).thenThrow(new CharacterNotFoundException(8675309));

        mockMvc.perform(get(String.format("/campaigns/%d/characters/8675309", FIRST_CAMPAIGN.getId())).accept(HAL_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(APPLICATION_PROBLEM_JSON))
                .andExpect(content().string(matchesJsonSchemaInClasspath(ERROR_SCHEMA)))
                .andExpect(jsonPath("$.code", is(NOT_FOUND.value())))
                .andExpect(jsonPath("$.message", is(NOT_FOUND.getReasonPhrase())))
                .andExpect(jsonPath("$.detail", is(String.format("Character with ID of %d not found", 8675309))));

        verify(campaignRepository, times(1)).findById(anyInt());
        verify(characterRepository, times(1)).findByCampaignAndId(any(), anyInt());
        verifyNoMoreInteractions(campaignRepository, characterRepository);
    }

    @Test
    public void getSingleCharacter_ValidCampaign_InvalidCharacter_InvalidAcceptHeader() throws Exception {
        when(campaignRepository.findById(anyInt())).thenReturn(Optional.of(FIRST_CAMPAIGN));
        when(characterRepository.findByCampaignAndId(any(), anyInt())).thenThrow(new CharacterNotFoundException(8675309));

        mockMvc.perform(get(String.format("/campaigns/%d/characters/8675309", FIRST_CAMPAIGN.getId())).accept(INVALID_MEDIA_TYPE))
                .andExpect(status().isNotAcceptable())
                .andExpect(content().contentType(APPLICATION_PROBLEM_JSON))
                .andExpect(content().string(matchesJsonSchemaInClasspath(ERROR_SCHEMA)))
                .andExpect(jsonPath("$.code", is(NOT_ACCEPTABLE.value())))
                .andExpect(jsonPath("$.message", is(NOT_ACCEPTABLE.getReasonPhrase())))
                .andExpect(jsonPath("$.detail", is("Accept header must be \"application/hal+json\"")));

        verifyNoInteractions(campaignRepository, characterRepository);
    }

    @Test
    public void getSingleCharacter_ValidCampaign_InvalidCharacter_InvalidMethod() throws Exception {
        when(campaignRepository.findById(anyInt())).thenReturn(Optional.of(FIRST_CAMPAIGN));
        when(characterRepository.findByCampaignAndId(any(), anyInt())).thenThrow(new CharacterNotFoundException(8675309));

        mockMvc.perform(put(String.format("/campaigns/%d/characters/8675309", FIRST_CAMPAIGN.getId())).accept(HAL_JSON))
                .andExpect(status().isMethodNotAllowed())
                .andExpect(content().contentType(APPLICATION_PROBLEM_JSON))
                .andExpect(content().string(matchesJsonSchemaInClasspath(ERROR_SCHEMA)))
                .andExpect(jsonPath("$.code", is(METHOD_NOT_ALLOWED.value())))
                .andExpect(jsonPath("$.message", is(METHOD_NOT_ALLOWED.getReasonPhrase())))
                .andExpect(jsonPath("$.detail", is("Method not allowed.")));

        verifyNoInteractions(campaignRepository, characterRepository);
    }

    @Test
    public void postCharacter_ValidCampaign_ValidContentType() throws Exception {
        when(campaignRepository.findById(anyInt())).thenReturn(Optional.of(FIRST_CAMPAIGN));
        when(characterRepository.save(any(Character.class))).thenReturn(NEW_CHARACTER);

        mockMvc
                .perform(
                        post(String.format("/campaigns/%d/characters", FIRST_CAMPAIGN.getId()))
                                .contentType(HAL_JSON)
                                .content(NEW_CHARACTER_JSON)
                                .header(HttpHeaders.AUTHORIZATION, "bearer 12345")
                )
                .andExpect(status().isCreated())
                .andExpect(content().contentType(HAL_JSON))
                .andExpect(content().string(matchesJsonSchemaInClasspath(OBJECT_SCHEMA)))
                .andExpect(jsonPath("$.id", is(NEW_CHARACTER.getId())))
                .andExpect(jsonPath("$.name", is(NEW_CHARACTER.getName())))
                .andExpect(jsonPath("$._links.index", hasEntry("href", "http://localhost:8080")))
                .andExpect(jsonPath("$._links.self", hasEntry("href", String.format("http://localhost:8080/campaigns/%d/characters/%d", FIRST_CAMPAIGN.getId(), NEW_CHARACTER.getId()))))
                .andExpect(jsonPath("$._links.curies", everyItem(
                        allOf(
                                hasEntry("href", (Object) "http://localhost:8080/docs/reference.html#resources-loot-{rel}"),
                                hasEntry("name", (Object) "loot-api"),
                                hasEntry("templated", (Object) true)
                        )
                )))
                .andExpect(jsonPath("$._links.loot-api:characters", hasEntry("href", String.format("http://localhost:8080/campaigns/%d/characters", FIRST_CAMPAIGN.getId()))))
                .andDo(document(
                        "character",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(
                                headerWithName("Content-Type")
                                        .description("Content Type header.")
                                        .attributes(key("contenttype").value(HAL_JSON_VALUE))
                        ),
                        responseFields(
                                fieldWithPath("id").description("ID number."),
                                fieldWithPath("name").description("Character name."),
                                subsectionWithPath("inventory").ignored(),
                                subsectionWithPath("_links").ignored()
                        ),
                        links(
                                halLinks(),
                                linkWithRel("self").description("This resource."),
                                linkWithRel("loot-api:characters").description("List of character resources."),
                                linkWithRel("index").description("Index resource."),
                                linkWithRel("curies").description("Curies.")
                        )
                ));

        verify(campaignRepository, times(1)).findById(anyInt());
        verify(characterRepository, times(1)).save(any(Character.class));
        verifyNoMoreInteractions(campaignRepository, characterRepository);
    }

    @Test
    public void postCharacter_ValidCampaign_InvalidContentType() throws Exception {
        when(campaignRepository.findById(anyInt())).thenReturn(Optional.of(FIRST_CAMPAIGN));
        when(characterRepository.save(any(Character.class))).thenReturn(NEW_CHARACTER);

        mockMvc
                .perform(
                        post(String.format("/campaigns/%d/characters", FIRST_CAMPAIGN.getId()))
                                .contentType(INVALID_MEDIA_TYPE)
                                .content(NEW_CHARACTER_JSON)
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

        verifyNoInteractions(campaignRepository, characterRepository);
    }

    @Test
    public void postCharacter_ValidCampaign_InvalidSyntax() throws Exception {
        when(campaignRepository.findById(anyInt())).thenReturn(Optional.of(FIRST_CAMPAIGN));
        when(characterRepository.save(any(Character.class))).thenReturn(NEW_CHARACTER);
        String invalidJson = "{\"name\": \"}";

        mockMvc
                .perform(
                        post(String.format("/campaigns/%d/characters", FIRST_CAMPAIGN.getId()))
                                .contentType(HAL_JSON)
                                .content(invalidJson)
                                .header(HttpHeaders.AUTHORIZATION, "bearer 12345")
                )
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(APPLICATION_PROBLEM_JSON))
                .andExpect(content().string(matchesJsonSchemaInClasspath(ERROR_SCHEMA)))
                .andExpect(jsonPath("$.code", is(BAD_REQUEST.value())))
                .andExpect(jsonPath("$.message", is(BAD_REQUEST.getReasonPhrase())));

        verifyNoInteractions(campaignRepository, characterRepository);
    }
}
