package io.github.alanabarbosa.integrationtests.controller.withxml;

import static io.restassured.RestAssured.given;
import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.text.SimpleDateFormat;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.boot.test.context.SpringBootTest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import io.github.alanabarbosa.configs.TestConfigs;
import io.github.alanabarbosa.integrationtests.testcontainers.AbstractIntegrationTest;
import io.github.alanabarbosa.integrationtests.vo.AccountCredentialsVO;
import io.github.alanabarbosa.integrationtests.vo.CategoryVO;
import io.github.alanabarbosa.integrationtests.vo.TokenVO;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.specification.RequestSpecification;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestMethodOrder(OrderAnnotation.class)
public class CategoryControllerXmlTest extends AbstractIntegrationTest {
	
	private static RequestSpecification specification;
	private static XmlMapper objectMapper;
	
	private static CategoryVO category;

    @BeforeAll
    public static void setup() {
        objectMapper = new XmlMapper();
	    objectMapper.registerModule(new JavaTimeModule());
	    objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
	    objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
	    objectMapper.enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT);
	    
	    objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSSSSS"));

        category = new CategoryVO();
    }

	@Test
	@Order(0)
	public void authorization() throws JsonMappingException, JsonProcessingException {
		AccountCredentialsVO user = new AccountCredentialsVO("alana", "admin123");
		
		var accessToken = given()
				.basePath("/auth/signin")
					.port(TestConfigs.SERVER_PORT)
					.contentType(TestConfigs.CONTENT_TYPE_XML)
					.accept(TestConfigs.CONTENT_TYPE_XML)
				.body(user)
					.when()
				.post()
					.then()
						.statusCode(200)
							.extract()
							.body()
								.as(TokenVO.class)
							.getAccessToken();
		
		specification = new RequestSpecBuilder()
				.addHeader(TestConfigs.HEADER_PARAM_AUTHORIZATION, "Bearer " + accessToken)
				.setBasePath("/api/category/v1")
				.setPort(TestConfigs.SERVER_PORT)
					.addFilter(new RequestLoggingFilter(LogDetail.ALL))
					.addFilter(new ResponseLoggingFilter(LogDetail.ALL))
				.build();
	}

    @Test
    @Order(1)
    public void testCreate() throws JsonProcessingException {
        mockCategory();
        
        var content = given().spec(specification)
                .contentType(TestConfigs.CONTENT_TYPE_XML)
                .accept(TestConfigs.CONTENT_TYPE_XML)
                	.body(category)
                	.when()
                	.post()
                .then()
                	.statusCode(200)
                		.extract()
                		.body()
                			.asString();

        CategoryVO persistedCategory = objectMapper.readValue(content, CategoryVO.class);
        category = persistedCategory;

        assertNotNull(persistedCategory.getKey());
        assertNotNull(persistedCategory.getName());
        assertNotNull(persistedCategory.getDescription());
        assertNotNull(persistedCategory.getCreatedAt());

        assertTrue(persistedCategory.getKey() > 0);
        assertEquals("Technology", persistedCategory.getName());
        assertEquals("Posts related to technology trends and news", persistedCategory.getDescription());
    }
	
	@Test
	@Order(2)
	public void testCreateWithWrongOrigin() throws JsonMappingException, JsonProcessingException {
		mockCategory();
		
		var content = given().spec(specification)
				.contentType(TestConfigs.CONTENT_TYPE_XML)
				.accept(TestConfigs.CONTENT_TYPE_XML)
					.header(TestConfigs.HEADER_PARAM_ORIGIN, TestConfigs.ORIGIN_ICLASS)
					.body(category)
				.when()
					.post()
				.then()
					.statusCode(403)
						.extract()
							.body()
								.asString();
		
		assertNotNull(content);
		assertEquals("Invalid CORS request", content);		
	}
	
	@Test
	@Order(3)
	public void testUpdate() throws JsonMappingException, JsonProcessingException {
		category.setName("Technology");
		
		var content = given().spec(specification)
				.contentType(TestConfigs.CONTENT_TYPE_XML)
				.accept(TestConfigs.CONTENT_TYPE_XML)
					.body(category)
					.when()
					.post()
				.then()
					.statusCode(200)
						.extract()
						.body()
							.asString();
		
		CategoryVO persistedCategory = objectMapper.readValue(content, CategoryVO.class);
		category = persistedCategory;
		
		assertNotNull(persistedCategory);
		
        assertNotNull(persistedCategory.getKey());
        assertNotNull(persistedCategory.getName());
        assertNotNull(persistedCategory.getDescription());
        assertNotNull(persistedCategory.getCreatedAt());
		
		assertEquals(category.getKey(), persistedCategory.getKey());
		
        assertEquals("Technology", persistedCategory.getName());
        assertEquals("Posts related to technology trends and news", persistedCategory.getDescription());
	}
	
	@Test
	@Order(4)
    public void testFindById() throws JsonProcessingException {
        mockCategory();

        var content = given().spec(specification)
                .contentType(TestConfigs.CONTENT_TYPE_XML)
                .accept(TestConfigs.CONTENT_TYPE_XML)
                	.pathParam("id", category.getKey())
                .when()
                	.get("{id}")
                .then()
                	.statusCode(200)
                	.extract()
                		.body()
                			.asString();

        CategoryVO persistedCategory = objectMapper.readValue(content, CategoryVO.class);
        category = persistedCategory;

        assertNotNull(persistedCategory);
        assertNotNull(persistedCategory.getKey());
        assertNotNull(persistedCategory.getName());
        assertNotNull(persistedCategory.getDescription());
        assertNotNull(persistedCategory.getCreatedAt());

        assertTrue(persistedCategory.getKey() > 0);
        assertEquals("Technology", persistedCategory.getName());
        assertEquals("Posts related to technology trends and news", persistedCategory.getDescription());
    }
	
	@Test
	@Order(4)
	public void testFindByIdWithWrongOrigin() throws JsonMappingException, JsonProcessingException {
		mockCategory();
		
		var content = given().spec(specification)
				.contentType(TestConfigs.CONTENT_TYPE_XML)
					.header(TestConfigs.HEADER_PARAM_ORIGIN, TestConfigs.ORIGIN_ICLASS)
					.pathParam("id", category.getKey())
					.when()
					.get("{id}")
				.then()
					.statusCode(403)
						.extract()
						.body()
							.asString();
		
	
		assertNotNull(content);
		assertEquals("Invalid CORS request", content);
	}
	
	@Test
	@Order(5)
	public void testFindAll() throws JsonMappingException, JsonProcessingException {
		
		var content = given().spec(specification)
				.contentType(TestConfigs.CONTENT_TYPE_XML)
				.accept(TestConfigs.CONTENT_TYPE_XML)
					.when()
					.get()
				.then()
					.statusCode(200)
						.extract()
						.body()
							.asString();
		
		List<CategoryVO> c = objectMapper.readValue(content, new TypeReference<List<CategoryVO>>() {});
		
		CategoryVO foundCategoryOne = c.get(0);
		
        assertNotNull(foundCategoryOne.getKey());
        assertNotNull(foundCategoryOne.getName());
        assertNotNull(foundCategoryOne.getDescription());
        assertNotNull(foundCategoryOne.getCreatedAt());
		
		assertEquals(1, foundCategoryOne.getKey());
		
        assertEquals("Technology", foundCategoryOne.getName());
        assertEquals("Posts related to technology trends and news", foundCategoryOne.getDescription());
		
		CategoryVO foundCommentThree = c.get(3);
		
		assertNotNull(foundCommentThree.getKey());
		assertNotNull(foundCommentThree.getName());
		assertNotNull(foundCommentThree.getDescription());
		assertNotNull(foundCommentThree.getCreatedAt());
		
		assertEquals(4, foundCommentThree.getKey());
		
		assertEquals("Education", foundCommentThree.getName());
		assertEquals("Resources and news about education", foundCommentThree.getDescription());	
	}
	
	@Test
	@Order(7)
	public void testDelete() throws JsonMappingException, JsonProcessingException {

		given().spec(specification)
			.contentType(TestConfigs.CONTENT_TYPE_XML)
				.pathParam("id", category.getKey())
				.when()
				.delete("{id}")
			.then()
				.statusCode(204);
	}

	private void mockCategory() {
	    category.setName("Technology");
	    category.setDescription("Posts related to technology trends and news");
	}
}