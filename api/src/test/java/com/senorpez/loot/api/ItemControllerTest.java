package com.senorpez.loot.api;

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

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;
import java.util.Random;

import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.apache.http.HttpHeaders.AUTHORIZATION;
import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.hateoas.MediaTypes.HAL_JSON;
import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.MediaType.ALL;
import static org.springframework.http.MediaType.APPLICATION_PROBLEM_JSON;
import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class ItemControllerTest {
    private MockMvc mockMvc;
    private static final MediaType INVALID_MEDIA_TYPE = new MediaType("application", "vnd.senorpez.loot.vx-json", UTF_8);
    private static final String COLLECTION_SCHEMA = "items.schema.json";
    private static final String OBJECT_SCHEMA = "item.schema.json";
    private static final String ERROR_SCHEMA = "error.schema.json";

    static final Item FIRST_ITEM = new Item(
            1,
            "First Item",
            BigDecimal.valueOf(3.5),
            "An item to test with",
            1
    );
    private static final Item SECOND_ITEM = new Item(
            2,
            "Second Item",
            null,
            null,
            null
    );
    private static final BigDecimal newWeight = BigDecimal.valueOf(new Random().nextDouble());
    private static final Integer newCharges = new Random().nextInt();
    private static final Item NEW_ITEM = new Item(
            new Random().nextInt(),
            "New Item",
            newWeight,
            "Adding a new item",
            newCharges);
    private static final String NEW_ITEM_JSON = String.format(
            "{\"name\": \"New Item\", \"weight\": %.2f, \"details\": \"Adding a new item\", \"charges\": %d}",
            newWeight, newCharges
    );

    private static final Item NEW_MINIMAL_ITEM = new Item(
            new Random().nextInt(),
            "Minimal Item",
            null,
            null,
            null
    );
    private static final String NEW_MINIMAL_ITEM_JSON = "{\"name\": \"Minimal Item\"}";


    @InjectMocks
    ItemController itemController;

    @Mock
    ItemRepository itemRepository;

    @Rule
    public final JUnitRestDocumentation restDocumentation = new JUnitRestDocumentation();

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        this.mockMvc = MockMvcBuilders
                .standaloneSetup(itemController)
                .setMessageConverters(HalMessageConverter.getConverter(Collections.singletonList(ALL)))
                .setControllerAdvice(new APIExceptionHandler())
                .apply(documentationConfiguration(this.restDocumentation))
                .build();
    }

    @Test
    public void getAllItems_ValidAcceptHeader() throws Exception {
        when(itemRepository.findAll()).thenReturn(Arrays.asList(FIRST_ITEM, SECOND_ITEM));

        mockMvc.perform(get("/items").accept(HAL_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(HAL_JSON))
                .andExpect(content().string(matchesJsonSchemaInClasspath(COLLECTION_SCHEMA)))
                .andExpect(jsonPath("$._embedded.loot-api:lootitem", hasItem(
                        allOf(
                                hasEntry("id", (Object) FIRST_ITEM.getId()),
                                hasEntry("name", (Object) FIRST_ITEM.getName()),
                                hasEntry(equalTo("_links"),
                                        hasEntry(equalTo("self"),
                                                hasEntry("href", String.format("http://localhost:8080/items/%d", FIRST_ITEM.getId()))))
                        )
                )))
                .andExpect(jsonPath("$._embedded.loot-api:lootitem", hasItem(
                        allOf(
                                hasEntry("id", (Object) SECOND_ITEM.getId()),
                                hasEntry("name", (Object) SECOND_ITEM.getName()),
                                hasEntry(equalTo("_links"),
                                        hasEntry(equalTo("self"),
                                                hasEntry("href", String.format("http://localhost:8080/items/%d", SECOND_ITEM.getId()))))
                        )
                )))
                .andExpect(jsonPath("$._links.self", hasEntry("href", "http://localhost:8080/items")))
                .andExpect(jsonPath("$._links.index", hasEntry("href", "http://localhost:8080")))
                .andExpect(jsonPath("$._links.curies", everyItem(
                        allOf(
                                hasEntry("href", (Object) "http://localhost:8080/docs/reference.html#resources-loot-{rel}"),
                                hasEntry("name", (Object) "loot-api"),
                                hasEntry("templated", (Object) true)
                        )
                )))
                .andDo(document("lootitems",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(
                                headerWithName("Accept").description("Required Accept header")
                        ),
                        responseHeaders(
                                headerWithName("Content-Type").description("Content type of response"),
                                headerWithName("Content-Length").description("Length of response")
                        ),
                        responseFields(
                                fieldWithPath("_embedded.loot-api:lootitem").description("Array of item resources"),
                                fieldWithPath("_embedded.loot-api:lootitem[].id").description("Item ID"),
                                fieldWithPath("_embedded.loot-api:lootitem[].name").description("Item name"),
                                fieldWithPath("_embedded.loot-api:lootitem[]._links.self").description("Link to item resource"),
                                fieldWithPath("_embedded.loot-api:lootitem[]._links.self.href").ignored(),
                                subsectionWithPath("_links").description("Links to other resources")
                        ),
                        links(
                                halLinks(),
                                linkWithRel("self").description("This resource"),
                                linkWithRel("index").description("Index resource"),
                                linkWithRel("curies").description("Compact URI resolvers")
                        )
                ));

        verify(itemRepository, times(1)).findAll();
        verifyNoMoreInteractions(itemRepository);
    }

    @Test
    public void getAllItems_InvalidAcceptHeader() throws Exception {
        when(itemRepository.findAll()).thenReturn(Arrays.asList(FIRST_ITEM, SECOND_ITEM));

        mockMvc.perform(get("/items").accept(INVALID_MEDIA_TYPE))
                .andExpect(status().isNotAcceptable())
                .andExpect(content().contentType(APPLICATION_PROBLEM_JSON))
                .andExpect(content().string(matchesJsonSchemaInClasspath(ERROR_SCHEMA)))
                .andExpect(jsonPath("$.code", is(NOT_ACCEPTABLE.value())))
                .andExpect(jsonPath("$.message", is(NOT_ACCEPTABLE.getReasonPhrase())))
                .andExpect(jsonPath("$.detail", is(String.format("Accept header must be \"%s\"", HAL_JSON.toString()))));

        verifyNoInteractions(itemRepository);
    }

    @Test
    public void getAllItems_InvalidMethod() throws Exception {
        when(itemRepository.findAll()).thenReturn(Arrays.asList(FIRST_ITEM, SECOND_ITEM));

        mockMvc.perform(put("/items").accept(HAL_JSON))
                .andExpect(status().isMethodNotAllowed())
                .andExpect(content().contentType(APPLICATION_PROBLEM_JSON))
                .andExpect(content().string(matchesJsonSchemaInClasspath(ERROR_SCHEMA)))
                .andExpect(jsonPath("$.code", is(METHOD_NOT_ALLOWED.value())))
                .andExpect(jsonPath("$.message", is(METHOD_NOT_ALLOWED.getReasonPhrase())))
                .andExpect(jsonPath("$.detail", is("Method not allowed.")));

        verifyNoInteractions(itemRepository);
    }

    @Test
    public void getSingleItem_ValidItem_ValidAcceptHeader() throws Exception {
        when(itemRepository.findById(anyInt())).thenReturn(Optional.of(FIRST_ITEM));

        mockMvc.perform(get(String.format("/items/%d", FIRST_ITEM.getId())).accept(HAL_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(HAL_JSON))
                .andExpect(content().string(matchesJsonSchemaInClasspath(OBJECT_SCHEMA)))
                .andExpect(jsonPath("$.id", is(FIRST_ITEM.getId())))
                .andExpect(jsonPath("$.name", is(FIRST_ITEM.getName())))
                .andExpect(jsonPath("$.weight", is(FIRST_ITEM.getWeight().doubleValue())))
                .andExpect(jsonPath("$.details", is(FIRST_ITEM.getDetails())))
                .andExpect(jsonPath("$.charges", is(FIRST_ITEM.getCharges())))
                .andExpect(jsonPath("$._links.self", hasEntry("href", String.format("http://localhost:8080/items/%d", FIRST_ITEM.getId()))))
                .andExpect(jsonPath("$._links.index", hasEntry("href", "http://localhost:8080")))
                .andExpect(jsonPath("$._links.curies", everyItem(
                        allOf(
                                hasEntry("href", (Object) "http://localhost:8080/docs/reference.html#resources-loot-{rel}"),
                                hasEntry("name", (Object) "loot-api"),
                                hasEntry("templated", (Object) true)
                        )
                )))
                .andDo(document(
                        "lootitem-get",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(
                                headerWithName("Accept").description("Accept header.")
                        ),
                        responseHeaders(
                                headerWithName("Content-Type").description("Content type of response"),
                                headerWithName("Content-Length").description("Length of response")
                        ),
                        responseFields(
                                fieldWithPath("id").description("Item ID"),
                                fieldWithPath("name").description("Item name"),
                                fieldWithPath("weight").description("Item weight (lbs.); may be null"),
                                fieldWithPath("details").description("Item details; may be null"),
                                fieldWithPath("charges").description("Item charges; may be null"),
                                subsectionWithPath("_links").description("Links to other resources")
                        ),
                        links(
                                halLinks(),
                                linkWithRel("self").description("This resource"),
                                linkWithRel("index").description("Index resource"),
                                linkWithRel("curies").description("Curies."),
                                linkWithRel("loot-api:lootitems").description("Array of item resources")
                        )
                ));

        verify(itemRepository, times(1)).findById(anyInt());
        verifyNoMoreInteractions(itemRepository);
    }

    @Test
    public void getMinimalItem_ValidItem_ValidAcceptHeader() throws Exception {
        when(itemRepository.findById(anyInt())).thenReturn(Optional.of(SECOND_ITEM));

        mockMvc.perform(get(String.format("/items/%d", SECOND_ITEM.getId())).accept(HAL_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(HAL_JSON))
                .andExpect(content().string(matchesJsonSchemaInClasspath(OBJECT_SCHEMA)))
                .andExpect(jsonPath("$.id", is(SECOND_ITEM.getId())))
                .andExpect(jsonPath("$.name", is(SECOND_ITEM.getName())))
                .andExpect(jsonPath("$.weight", is(nullValue())))
                .andExpect(jsonPath("$.details", is(nullValue())))
                .andExpect(jsonPath("$.charges", is(nullValue())))
                .andExpect(jsonPath("$._links.self", hasEntry("href", String.format("http://localhost:8080/items/%d", SECOND_ITEM.getId()))))
                .andExpect(jsonPath("$._links.index", hasEntry("href", "http://localhost:8080")))
                .andExpect(jsonPath("$._links.curies", everyItem(
                        allOf(
                                hasEntry("href", (Object) "http://localhost:8080/docs/reference.html#resources-loot-{rel}"),
                                hasEntry("name", (Object) "loot-api"),
                                hasEntry("templated", (Object) true)
                        )
                )));

        verify(itemRepository, times(1)).findById(anyInt());
        verifyNoMoreInteractions(itemRepository);
    }

    @Test
    public void getSingleItem_ValidItem_InvalidAcceptHeader() throws Exception {
        when(itemRepository.findById(anyInt())).thenReturn(Optional.of(FIRST_ITEM));

        mockMvc.perform(get(String.format("/items/%d", FIRST_ITEM.getId())).accept(INVALID_MEDIA_TYPE))
                .andExpect(status().isNotAcceptable())
                .andExpect(content().contentType(APPLICATION_PROBLEM_JSON))
                .andExpect(content().string(matchesJsonSchemaInClasspath(ERROR_SCHEMA)))
                .andExpect(jsonPath("$.code", is(NOT_ACCEPTABLE.value())))
                .andExpect(jsonPath("$.message", is(NOT_ACCEPTABLE.getReasonPhrase())))
                .andExpect(jsonPath("$.detail", is("Accept header must be \"application/hal+json\"")));

        verifyNoInteractions(itemRepository);
    }

    @Test
    public void getSingleItem_ValidItem_InvalidMethod() throws Exception {
        when(itemRepository.findById(anyInt())).thenReturn(Optional.of(FIRST_ITEM));

        mockMvc.perform(patch(String.format("/items/%d", FIRST_ITEM.getId())).accept(HAL_JSON))
                .andExpect(status().isMethodNotAllowed())
                .andExpect(content().contentType(APPLICATION_PROBLEM_JSON))
                .andExpect(content().string(matchesJsonSchemaInClasspath(ERROR_SCHEMA)))
                .andExpect(jsonPath("$.code", is(METHOD_NOT_ALLOWED.value())))
                .andExpect(jsonPath("$.message", is(METHOD_NOT_ALLOWED.getReasonPhrase())))
                .andExpect(jsonPath("$.detail", is("Method not allowed.")));

        verifyNoInteractions(itemRepository);
    }

    @Test
    public void getSingleItem_InvalidItem_ValidAcceptHeader() throws Exception {
        when(itemRepository.findById(anyInt())).thenThrow(new ItemNotFoundException(8675309));

        mockMvc.perform(get("/items/8675309").accept(HAL_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(APPLICATION_PROBLEM_JSON))
                .andExpect(content().string(matchesJsonSchemaInClasspath(ERROR_SCHEMA)))
                .andExpect(jsonPath("$.code", is(NOT_FOUND.value())))
                .andExpect(jsonPath("$.message", is(NOT_FOUND.getReasonPhrase())))
                .andExpect(jsonPath("$.detail", is(String.format("Item with ID of %d not found", 8675309))));

        verify(itemRepository, times(1)).findById(anyInt());
        verifyNoMoreInteractions(itemRepository);
    }

    @Test
    public void getSingleItem_InvalidItem_InvalidAcceptHeader() throws Exception {
        when(itemRepository.findById(anyInt())).thenThrow(new ItemNotFoundException(8675309));

        mockMvc.perform(get("/items/8675309").accept(INVALID_MEDIA_TYPE))
                .andExpect(status().isNotAcceptable())
                .andExpect(content().contentType(APPLICATION_PROBLEM_JSON))
                .andExpect(content().string(matchesJsonSchemaInClasspath(ERROR_SCHEMA)))
                .andExpect(jsonPath("$.code", is(NOT_ACCEPTABLE.value())))
                .andExpect(jsonPath("$.message", is(NOT_ACCEPTABLE.getReasonPhrase())))
                .andExpect(jsonPath("$.detail", is("Accept header must be \"application/hal+json\"")));

        verifyNoInteractions(itemRepository);
    }

    @Test
    public void getSingleItem_InvalidItem_InvalidMethod() throws Exception {
        when(itemRepository.findById(anyInt())).thenThrow(new ItemNotFoundException(8675309));

        mockMvc.perform(patch("/items/8675309").accept(HAL_JSON))
                .andExpect(status().isMethodNotAllowed())
                .andExpect(content().contentType(APPLICATION_PROBLEM_JSON))
                .andExpect(content().string(matchesJsonSchemaInClasspath(ERROR_SCHEMA)))
                .andExpect(jsonPath("$.code", is(METHOD_NOT_ALLOWED.value())))
                .andExpect(jsonPath("$.message", is(METHOD_NOT_ALLOWED.getReasonPhrase())))
                .andExpect(jsonPath("$.detail", is("Method not allowed.")));

        verifyNoInteractions(itemRepository);
    }

    @Test
    public void postItem_ValidContentType() throws Exception {
        when(itemRepository.save(any(Item.class))).thenReturn(NEW_ITEM);

        mockMvc
                .perform(
                        post("/items")
                                .contentType(HAL_JSON)
                                .content(NEW_ITEM_JSON)
                                .header(AUTHORIZATION, "bearer 12345")
                )
                .andExpect(status().isCreated())
                .andExpect(content().contentType(HAL_JSON))
                .andExpect(content().string(matchesJsonSchemaInClasspath(OBJECT_SCHEMA)))
                .andExpect(jsonPath("$.id", is(NEW_ITEM.getId())))
                .andExpect(jsonPath("$.name", is(NEW_ITEM.getName())))
                .andExpect(jsonPath("$.weight", is(NEW_ITEM.getWeight().doubleValue())))
                .andExpect(jsonPath("$.details", is(NEW_ITEM.getDetails())))
                .andExpect(jsonPath("$.charges", is(NEW_ITEM.getCharges())))
                .andExpect(jsonPath("$._links.self", hasEntry("href", String.format("http://localhost:8080/items/%d", NEW_ITEM.getId()))))
                .andExpect(jsonPath("$._links.index", hasEntry("href", "http://localhost:8080")))
                .andExpect(jsonPath("$._links.curies", everyItem(
                        allOf(
                                hasEntry("href", (Object) "http://localhost:8080/docs/reference.html#resources-loot-{rel}"),
                                hasEntry("name", (Object) "loot-api"),
                                hasEntry("templated", (Object) true)
                        )
                )))
                .andDo(document(
                        "lootitems-post",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(
                                headerWithName("Content-Type").description("Content type of request; must be application/hal+json"),
                                headerWithName("Authorization").description("Authorization token")
                        ),
                        requestFields(
                                fieldWithPath("name").description("Item name"),
                                fieldWithPath("weight").description("Item weight (lbs.); may be null").optional(),
                                fieldWithPath("details").description("Item details; may be null").optional(),
                                fieldWithPath("charges").description("Item charges; may be null").optional()
                        ),
                        responseHeaders(
                                headerWithName("Content-Type").description("Content type of response"),
                                headerWithName("Content-Length").description("Length of response")
                        ),
                        responseFields(
                                fieldWithPath("id").description("Item ID"),
                                fieldWithPath("name").description("Item name"),
                                fieldWithPath("weight").description("Item weight (lbs.); may be null"),
                                fieldWithPath("details").description("Item details; may be null"),
                                fieldWithPath("charges").description("Item charges; may be null"),
                                subsectionWithPath("_links").description("Links to other resources")
                        ),
                        links(
                                halLinks(),
                                linkWithRel("self").description("This resource"),
                                linkWithRel("index").description("Index resource"),
                                linkWithRel("curies").description("Curies."),
                                linkWithRel("loot-api:lootitems").description("Array of item resources")
                        )
                ));

        verify(itemRepository, times(1)).save(any(Item.class));
        verifyNoMoreInteractions(itemRepository);
    }

    @Test
    public void postMinimalItem_ValidContentType() throws Exception {
        when(itemRepository.save(any(Item.class))).thenReturn(NEW_MINIMAL_ITEM);

        mockMvc
                .perform(
                        post("/items")
                                .contentType(HAL_JSON)
                                .content(NEW_MINIMAL_ITEM_JSON)
                                .header(AUTHORIZATION, "bearer 12345")
                )
                .andExpect(status().isCreated())
                .andExpect(content().contentType(HAL_JSON))
                .andExpect(content().string(matchesJsonSchemaInClasspath(OBJECT_SCHEMA)))
                .andExpect(jsonPath("$.id", is(NEW_MINIMAL_ITEM.getId())))
                .andExpect(jsonPath("$.name", is(NEW_MINIMAL_ITEM.getName())))
                .andExpect(jsonPath("$.weight", is(nullValue())))
                .andExpect(jsonPath("$.details", is(nullValue())))
                .andExpect(jsonPath("$.charges", is(nullValue())))
                .andExpect(jsonPath("$._links.self", hasEntry("href", String.format("http://localhost:8080/items/%d", NEW_MINIMAL_ITEM.getId()))))
                .andExpect(jsonPath("$._links.index", hasEntry("href", "http://localhost:8080")))
                .andExpect(jsonPath("$._links.curies", everyItem(
                        allOf(
                                hasEntry("href", (Object) "http://localhost:8080/docs/reference.html#resources-loot-{rel}"),
                                hasEntry("name", (Object) "loot-api"),
                                hasEntry("templated", (Object) true)
                        )
                )));

        verify(itemRepository, times(1)).save(any(Item.class));
        verifyNoMoreInteractions(itemRepository);
    }

    @Test
    public void postItem_InvalidContentType() throws Exception {
        when(itemRepository.save(any(Item.class))).thenReturn(NEW_ITEM);

        mockMvc
                .perform(
                        post("/items")
                                .contentType(INVALID_MEDIA_TYPE)
                                .content(NEW_ITEM_JSON)
                                .header(AUTHORIZATION, "bearer 12345")
                )
                .andExpect(status().isUnsupportedMediaType())
                .andExpect(content().contentType(APPLICATION_PROBLEM_JSON))
                .andExpect(content().string(matchesJsonSchemaInClasspath(ERROR_SCHEMA)))
                .andExpect(jsonPath("$.code", is(UNSUPPORTED_MEDIA_TYPE.value())))
                .andExpect(jsonPath("$.message", is(UNSUPPORTED_MEDIA_TYPE.getReasonPhrase())))
                .andExpect(jsonPath("$.detail", is(String.format("Content type '%s' not supported", INVALID_MEDIA_TYPE.toString()))));

        verifyNoInteractions(itemRepository);
    }

    @Test
    public void postItem_InvalidSyntax() throws Exception {
        when(itemRepository.save(any(Item.class))).thenReturn(NEW_ITEM);
        String invalidJson = "{\"name\": \"}";

        mockMvc
                .perform(
                        post("/items")
                                .contentType(HAL_JSON)
                                .content(invalidJson)
                                .header(AUTHORIZATION, "bearer 12345")
                )
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(APPLICATION_PROBLEM_JSON))
                .andExpect(content().string(matchesJsonSchemaInClasspath(ERROR_SCHEMA)))
                .andExpect(jsonPath("$.code", is(BAD_REQUEST.value())))
                .andExpect(jsonPath("$.message", is(BAD_REQUEST.getReasonPhrase())));

        verifyNoInteractions(itemRepository);
    }

    @Test
    public void putItem_ValidItem_ValidContentType() throws Exception {
        when(itemRepository.findById(anyInt())).thenReturn(Optional.of(NEW_ITEM));
        when(itemRepository.save(any(Item.class))).thenReturn(NEW_ITEM);

        mockMvc
                .perform(
                        put(String.format("/items/%d", NEW_ITEM.getId()))
                                .contentType(HAL_JSON)
                                .content(NEW_ITEM_JSON)
                                .header(AUTHORIZATION, "bearer 12345")
                )
                .andExpect(status().isCreated())
                .andExpect(content().contentType(HAL_JSON))
                .andExpect(content().string(matchesJsonSchemaInClasspath(OBJECT_SCHEMA)))
                .andExpect(jsonPath("$.id", is(NEW_ITEM.getId())))
                .andExpect(jsonPath("$.name", is(NEW_ITEM.getName())))
                .andExpect(jsonPath("$.weight", is(NEW_ITEM.getWeight().doubleValue())))
                .andExpect(jsonPath("$.details", is(NEW_ITEM.getDetails())))
                .andExpect(jsonPath("$.charges", is(NEW_ITEM.getCharges())))
                .andExpect(jsonPath("$._links.self", hasEntry("href", String.format("http://localhost:8080/items/%d", NEW_ITEM.getId()))))
                .andExpect(jsonPath("$._links.index", hasEntry("href", "http://localhost:8080")))
                .andExpect(jsonPath("$._links.curies", everyItem(
                        allOf(
                                hasEntry("href", (Object) "http://localhost:8080/docs/reference.html#resources-loot-{rel}"),
                                hasEntry("name", (Object) "loot-api"),
                                hasEntry("templated", (Object) true)
                        )
                )))
                .andDo(document(
                        "lootitem-put",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(
                                headerWithName("Content-Type").description("Content type of request; must be application/hal+json"),
                                headerWithName("Authorization").description("Authorization token")
                        ),
                        requestFields(
                                fieldWithPath("name").description("Item name; may be null to leave value unchanged"),
                                fieldWithPath("weight").description("Item weight (lbs.); may be null to leave value unchanged").optional(),
                                fieldWithPath("details").description("Item details; may be null to leave value unchanged").optional(),
                                fieldWithPath("charges").description("Item charges; may be null to leave value unchanged").optional()
                        ),
                        responseHeaders(
                                headerWithName("Content-Type").description("Content type of response"),
                                headerWithName("Content-Length").description("Length of response")
                        ),
                        responseFields(
                                fieldWithPath("id").description("Item ID"),
                                fieldWithPath("name").description("Item name"),
                                fieldWithPath("weight").description("Item weight (lbs.); may be null"),
                                fieldWithPath("details").description("Item details; may be null"),
                                fieldWithPath("charges").description("Item charges; may be null"),
                                subsectionWithPath("_links").description("Links to other resources")
                        ),
                        links(
                                halLinks(),
                                linkWithRel("self").description("This resource"),
                                linkWithRel("index").description("Index resource"),
                                linkWithRel("curies").description("Curies."),
                                linkWithRel("loot-api:lootitems").description("Array of item resources")
                        )
                ));

        verify(itemRepository, times(1)).findById(anyInt());
        verify(itemRepository, times(1)).save(any(Item.class));
        verifyNoMoreInteractions(itemRepository);
    }
    
    @Test
    public void putMinimalItem_ValidItem_ValidContentType() throws Exception {
        when(itemRepository.findById(anyInt())).thenReturn(Optional.of(NEW_MINIMAL_ITEM));
        when(itemRepository.save(any(Item.class))).thenReturn(NEW_MINIMAL_ITEM);

        mockMvc
                .perform(
                        put(String.format("/items/%d", NEW_MINIMAL_ITEM.getId()))
                                .contentType(HAL_JSON)
                                .content(NEW_MINIMAL_ITEM_JSON)
                                .header(AUTHORIZATION, "bearer 12345")
                )
                .andExpect(status().isCreated())
                .andExpect(content().contentType(HAL_JSON))
                .andExpect(content().string(matchesJsonSchemaInClasspath(OBJECT_SCHEMA)))
                .andExpect(jsonPath("$.id", is(NEW_MINIMAL_ITEM.getId())))
                .andExpect(jsonPath("$.name", is(NEW_MINIMAL_ITEM.getName())))
                .andExpect(jsonPath("$._links.self", hasEntry("href", String.format("http://localhost:8080/items/%d", NEW_MINIMAL_ITEM.getId()))))
                .andExpect(jsonPath("$._links.index", hasEntry("href", "http://localhost:8080")))
                .andExpect(jsonPath("$._links.curies", everyItem(
                        allOf(
                                hasEntry("href", (Object) "http://localhost:8080/docs/reference.html#resources-loot-{rel}"),
                                hasEntry("name", (Object) "loot-api"),
                                hasEntry("templated", (Object) true)
                        )
                )));

        verify(itemRepository, times(1)).findById(anyInt());
        verify(itemRepository, times(1)).save(any(Item.class));
        verifyNoMoreInteractions(itemRepository);
    }

    @Test
    public void putItem_ValidItem_InvalidContentType() throws Exception {
        when(itemRepository.findById(anyInt())).thenReturn(Optional.of(NEW_ITEM));
        when(itemRepository.save(any(Item.class))).thenReturn(NEW_ITEM);

        mockMvc
                .perform(
                        put(String.format("/items/%d", NEW_ITEM.getId()))
                                .contentType(INVALID_MEDIA_TYPE)
                                .content(NEW_ITEM_JSON)
                                .header(AUTHORIZATION, "bearer 12345")
                )
                .andExpect(status().isUnsupportedMediaType())
                .andExpect(content().contentType(APPLICATION_PROBLEM_JSON))
                .andExpect(content().string(matchesJsonSchemaInClasspath(ERROR_SCHEMA)))
                .andExpect(jsonPath("$.code", is(UNSUPPORTED_MEDIA_TYPE.value())))
                .andExpect(jsonPath("$.message", is(UNSUPPORTED_MEDIA_TYPE.getReasonPhrase())))
                .andExpect(jsonPath("$.detail", is(String.format("Content type '%s' not supported", INVALID_MEDIA_TYPE.toString()))));

        verifyNoInteractions(itemRepository);
    }

    @Test
    public void putItem_ValidItem_InvalidSyntax() throws Exception {
        when(itemRepository.findById(anyInt())).thenReturn(Optional.of(NEW_ITEM));
        when(itemRepository.save(any(Item.class))).thenReturn(NEW_ITEM);
        String invalidJson = "{\"name\": \"}";

        mockMvc
                .perform(
                        put(String.format("/items/%d", NEW_ITEM.getId()))
                                .contentType(HAL_JSON)
                                .content(invalidJson)
                                .header(AUTHORIZATION, "bearer 12345")
                )
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(APPLICATION_PROBLEM_JSON))
                .andExpect(content().string(matchesJsonSchemaInClasspath(ERROR_SCHEMA)))
                .andExpect(jsonPath("$.code", is(BAD_REQUEST.value())))
                .andExpect(jsonPath("$.message", is(BAD_REQUEST.getReasonPhrase())));

        verifyNoInteractions(itemRepository);
    }

    @Test
    public void putItem_InvalidItem_ValidContentType() throws Exception {
        when(itemRepository.findById(anyInt())).thenThrow(new ItemNotFoundException(8675309));
        when(itemRepository.save(any(Item.class))).thenReturn(NEW_ITEM);

        mockMvc
                .perform(
                        put("/items/8675309")
                                .contentType(HAL_JSON)
                                .content(NEW_ITEM_JSON)
                                .header(AUTHORIZATION, "bearer 12345")
                )
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(APPLICATION_PROBLEM_JSON))
                .andExpect(content().string(matchesJsonSchemaInClasspath(ERROR_SCHEMA)))
                .andExpect(jsonPath("$.code", is(NOT_FOUND.value())))
                .andExpect(jsonPath("$.message", is(NOT_FOUND.getReasonPhrase())))
                .andExpect(jsonPath("$.detail", is(String.format("Item with ID of %d not found", 8675309))));

        verify(itemRepository, times(1)).findById(anyInt());
        verifyNoMoreInteractions(itemRepository);
    }

    @Test
    public void putItem_InvalidItem_InvalidContentType() throws Exception {
        when(itemRepository.findById(anyInt())).thenThrow(new ItemNotFoundException(8675309));
        when(itemRepository.save(any(Item.class))).thenReturn(NEW_ITEM);

        mockMvc
                .perform(
                        put("/items/8675309")
                                .contentType(INVALID_MEDIA_TYPE)
                                .content(NEW_ITEM_JSON)
                                .header(AUTHORIZATION, "bearer 12345")
                )
                .andExpect(status().isUnsupportedMediaType())
                .andExpect(content().contentType(APPLICATION_PROBLEM_JSON))
                .andExpect(content().string(matchesJsonSchemaInClasspath(ERROR_SCHEMA)))
                .andExpect(jsonPath("$.code", is(UNSUPPORTED_MEDIA_TYPE.value())))
                .andExpect(jsonPath("$.message", is(UNSUPPORTED_MEDIA_TYPE.getReasonPhrase())))
                .andExpect(jsonPath("$.detail", is(String.format("Content type '%s' not supported", INVALID_MEDIA_TYPE.toString()))));

        verifyNoInteractions(itemRepository);
    }

    @Test
    public void putItem_InvalidItem_InvalidSyntax() throws Exception {
        when(itemRepository.findById(anyInt())).thenThrow(new ItemNotFoundException(8675309));
        when(itemRepository.save(any(Item.class))).thenReturn(NEW_ITEM);
        String invalidJson = "{\"name\": \"}";

        mockMvc
                .perform(
                        put("/items/8675309")
                                .contentType(HAL_JSON)
                                .content(invalidJson)
                                .header(AUTHORIZATION, "bearer 12345")
                )
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(APPLICATION_PROBLEM_JSON))
                .andExpect(content().string(matchesJsonSchemaInClasspath(ERROR_SCHEMA)))
                .andExpect(jsonPath("$.code", is(BAD_REQUEST.value())))
                .andExpect(jsonPath("$.message", is(BAD_REQUEST.getReasonPhrase())));

        verifyNoInteractions(itemRepository);
    }
}
