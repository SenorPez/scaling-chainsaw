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

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

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
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.snippet.Attributes.key;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class ItemControllerTest {
    private MockMvc mockMvc;
    private static final MediaType INVALID_MEDIA_TYPE = new MediaType("application", "vnd.senorpez.loottrack.vx-json", UTF_8);
    private static final String COLLECTION_SCHEMA = "items.schema.json";
    private static final String OBJECT_SCHEMA = "item.schema.json";
    private static final String ERROR_SCHEMA = "error.schema.json";

    private static final Item FIRST_ITEM = new Item()
            .setId(1)
            .setName("First Item");
    private static final Item SECOND_ITEM = new Item()
            .setId(2)
            .setName("Second Item");

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
                .andExpect(jsonPath("$._embedded.loottable-api:lootitem", hasItem(
                        allOf(
                                hasEntry("id", (Object) FIRST_ITEM.getId()),
                                hasEntry("name", (Object) FIRST_ITEM.getName()),
                                hasEntry(equalTo("_links"),
                                        hasEntry(equalTo("self"),
                                                hasEntry("href", String.format("http://localhost:8080/items/%d", FIRST_ITEM.getId()))
                                        )
                                )
                        )
                )))
                .andExpect(jsonPath("$._embedded.loottable-api:lootitem", hasItem(
                        allOf(
                                hasEntry("id", (Object) SECOND_ITEM.getId()),
                                hasEntry("name", (Object) SECOND_ITEM.getName()),
                                hasEntry(equalTo("_links"),
                                        hasEntry(equalTo("self"),
                                                hasEntry("href", String.format("http://localhost:8080/items/%d", SECOND_ITEM.getId()))
                                        )
                                )
                        )
                )))
                .andExpect(jsonPath("$._links.index", hasEntry("href", "http://localhost:8080")))
                .andExpect(jsonPath("$._links.self", hasEntry("href", "http://localhost:8080/items")))
                .andExpect(jsonPath("$._links.curies", everyItem(
                        allOf(
                                hasEntry("href", (Object) "http://localhost:8080/docs/reference.html#resources-loottable-{rel}"),
                                hasEntry("name", (Object) "loottable-api"),
                                hasEntry("templated", (Object) true)
                        )
                )))
                .andDo(document("lootitems",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(
                                headerWithName("Accept")
                                        .description("Accept header.")
                                        .attributes(key("acceptvalue").value(HAL_JSON_VALUE))
                        ),
                        responseFields(
                                fieldWithPath("_embedded.loottable-api:lootitem").description("Item resource."),
                                fieldWithPath("_embedded.loottable-api:lootitem[].id").description("Item ID number."),
                                fieldWithPath("_embedded.loottable-api:lootitem[].name").description("Item name."),
                                subsectionWithPath("_links").ignored(),
                                subsectionWithPath("_embedded.loottable-api:lootitem[]._links").ignored()

                        ),
                        links(
                                halLinks(),
                                linkWithRel("self").description("This resource."),
                                linkWithRel("index").description("Index resource."),
                                linkWithRel("curies").description("Curies."))
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
                .andExpect(jsonPath("$._links.index", hasEntry("href", "http://localhost:8080")))
                .andExpect(jsonPath("$._links.self", hasEntry("href", String.format("http://localhost:8080/items/%d", FIRST_ITEM.getId()))))
                .andExpect(jsonPath("$._links.curies", everyItem(
                        allOf(
                                hasEntry("href", (Object) "http://localhost:8080/docs/reference.html#resources-loottable-{rel}"),
                                hasEntry("name", (Object) "loottable-api"),
                                hasEntry("templated", (Object) true)
                        )
                )))
                .andDo(document(
                        "lootitem",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(
                                headerWithName("Accept")
                                        .description("Accept header.")
                                        .attributes(key("acceptvalue").value(HAL_JSON_VALUE))
                        ),
                        responseFields(
                                fieldWithPath("id").description("ID number."),
                                fieldWithPath("name").description("Item name."),
                                subsectionWithPath("_links").ignored()
                        ),
                        links(
                                halLinks(),
                                linkWithRel("self").description("This resource."),
                                linkWithRel("loottable-api:lootitems").description("List of item resources."),
                                linkWithRel("index").description("Index resource."),
                                linkWithRel("curies").description("Curies.")
                        )
                ));

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

        mockMvc.perform(put(String.format("/items/%d", FIRST_ITEM.getId())).accept(HAL_JSON))
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

        mockMvc.perform(put("/items/8675309").accept(HAL_JSON))
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
        when(itemRepository.save(any(Item.class))).thenReturn(FIRST_ITEM);

        ObjectMapper objectMapper = new ObjectMapper();

        mockMvc.perform(post("/items").contentType(HAL_JSON).content(objectMapper.writeValueAsString(FIRST_ITEM)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(HAL_JSON))
                .andExpect(content().string(matchesJsonSchemaInClasspath(OBJECT_SCHEMA)))
                .andExpect(jsonPath("$.id", is(FIRST_ITEM.getId())))
                .andExpect(jsonPath("$.name", is(FIRST_ITEM.getName())))
                .andExpect(jsonPath("$._links.index", hasEntry("href", "http://localhost:8080")))
                .andExpect(jsonPath("$._links.self", hasEntry("href", String.format("http://localhost:8080/items/%d", FIRST_ITEM.getId()))))
                .andExpect(jsonPath("$._links.curies", everyItem(
                        allOf(
                                hasEntry("href", (Object) "http://localhost:8080/docs/reference.html#resources-loottable-{rel}"),
                                hasEntry("name", (Object) "loottable-api"),
                                hasEntry("templated", (Object) true)
                        )
                )))
                .andDo(document(
                        "lootitem",
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
                                subsectionWithPath("_links").ignored()
                        ),
                        links(
                                halLinks(),
                                linkWithRel("self").description("This resource."),
                                linkWithRel("loottable-api:lootitems").description("List of item resources."),
                                linkWithRel("index").description("Index resource."),
                                linkWithRel("curies").description("Curies")
                        )
                ));

        verify(itemRepository, times(1)).save(any(Item.class));
        verifyNoMoreInteractions(itemRepository);
    }

    @Test
    public void postItem_InvalidContentType() throws Exception {
        when(itemRepository.save(any(Item.class))).thenReturn(FIRST_ITEM);

        ObjectMapper objectMapper = new ObjectMapper();

        mockMvc.perform(post("/items").contentType(INVALID_MEDIA_TYPE).content(objectMapper.writeValueAsString(FIRST_ITEM)))
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
        when(itemRepository.save(any(Item.class))).thenReturn(FIRST_ITEM);
        String invalidJson = "{\"name\": \"}";

        mockMvc.perform(post("/items").contentType(HAL_JSON).content(invalidJson))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(APPLICATION_PROBLEM_JSON))
                .andExpect(content().string(matchesJsonSchemaInClasspath(ERROR_SCHEMA)))
                .andExpect(jsonPath("$.code", is(BAD_REQUEST.value())))
                .andExpect(jsonPath("$.message", is(BAD_REQUEST.getReasonPhrase())));

        verifyNoInteractions(itemRepository);
    }
}