package io.github.alanabarbosa.integrationtests.controller.withjson;

import static io.restassured.RestAssured.given;
import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.boot.test.context.SpringBootTest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import io.github.alanabarbosa.configs.TestConfigs;
import io.github.alanabarbosa.data.vo.v1.CategoryVO;
import io.github.alanabarbosa.integrationtests.testcontainers.AbstractIntegrationTest;
import io.github.alanabarbosa.integrationtests.vo.AccountCredentialsVO;
import io.github.alanabarbosa.integrationtests.vo.TokenVO;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.specification.RequestSpecification;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestMethodOrder(OrderAnnotation.class)
public class CategoryControllerJsonTest extends AbstractIntegrationTest{
	
	private static RequestSpecification specification ;
	private static ObjectMapper objectMapper;
	private static CategoryVO category;

	LocalDateTime now = LocalDateTime.now();
	
	@BeforeAll
	public static void setup() {
		objectMapper = new ObjectMapper();
		objectMapper.registerModule(new JavaTimeModule());
		objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
		objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
		objectMapper.enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT);

	    category = new CategoryVO();
	}
	
	@Test
	@Order(0)
	public void authorization() throws JsonMappingException, JsonProcessingException {
		AccountCredentialsVO user = new AccountCredentialsVO("alana", "admin123");
		
		var accessToken = given()
				.basePath("/auth/signin")
					.port(TestConfigs.SERVER_PORT)
					.contentType(TestConfigs.CONTENT_TYPE_JSON)
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
	public void testCreate() throws JsonMappingException, JsonProcessingException {
		mockCategory();
		
	    ObjectMapper objectMapper = new ObjectMapper();
	    objectMapper.registerModule(new JavaTimeModule());
	    objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);		
		
	 /*   specification = new RequestSpecBuilder()
	            .addHeader(TestConfigs.HEADER_PARAM_ORIGIN, TestConfigs.ORIGIN_ALANA)
	            .setBasePath("/api/category/v1")
	            .setPort(TestConfigs.SERVER_PORT)
		            .addFilter(new RequestLoggingFilter(LogDetail.ALL))
		            .addFilter(new ResponseLoggingFilter(LogDetail.ALL))
	            .build();*/
		
		var content = given().spec(specification)
				.contentType(TestConfigs.CONTENT_TYPE_JSON)
					.header(TestConfigs.HEADER_PARAM_ORIGIN, TestConfigs.ORIGIN_ALANA)
					.body(category)
					.when()
					.post()
				.then()
					.statusCode(200)
						.extract()
						.body()
							.asString();
		System.out.println(content);
		CategoryVO persistedCategory = objectMapper.readValue(content, CategoryVO.class);
		
		category = persistedCategory;
		
		assertNotNull(persistedCategory.getKey());
		assertNotNull(persistedCategory.getName());
		assertNotNull(persistedCategory.getDescription());
		assertNotNull(persistedCategory.getCreatedAt());
		
		assertTrue(persistedCategory.getKey() > 0);
		
		assertEquals("Technology", persistedCategory.getName());
		assertEquals("Posts related to technology trends and news", persistedCategory.getDescription());		
		assertTrue(persistedCategory.getCreatedAt()
				.truncatedTo(ChronoUnit.SECONDS)
				.isEqual(now
						.truncatedTo(ChronoUnit.SECONDS)));	
	}
	
	@Test
	@Order(2)
	public void testCreateWithWrongOrigin() throws JsonMappingException, JsonProcessingException {
		mockCategory();
		
		/*specification = new RequestSpecBuilder()
				.addHeader(TestConfigs.HEADER_PARAM_ORIGIN, TestConfigs.ORIGIN_ICLASS)
				.setBasePath("/api/category/v1")
				.setPort(TestConfigs.SERVER_PORT)
					.addFilter(new RequestLoggingFilter(LogDetail.ALL))
					.addFilter(new ResponseLoggingFilter(LogDetail.ALL))
				.build();*/
		
		var content = given().spec(specification)
				.contentType(TestConfigs.CONTENT_TYPE_JSON)
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
	public void testFindById() throws JsonMappingException, JsonProcessingException {
		mockCategory();
		
		/*specification = new RequestSpecBuilder()
				.addHeader(TestConfigs.HEADER_PARAM_ORIGIN, TestConfigs.ORIGIN_ALANA)
				.setBasePath("/api/category/v1")
				.setPort(TestConfigs.SERVER_PORT)
					.addFilter(new RequestLoggingFilter(LogDetail.ALL))
					.addFilter(new ResponseLoggingFilter(LogDetail.ALL))
				.build();*/
			
		var content = given().spec(specification)
				.contentType(TestConfigs.CONTENT_TYPE_JSON)
					.header(TestConfigs.HEADER_PARAM_ORIGIN, TestConfigs.ORIGIN_ALANA)
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
		
		System.out.println(category);
		
		assertNotNull(persistedCategory);
		
		assertNotNull(persistedCategory.getKey());
		assertNotNull(persistedCategory.getName());
		assertNotNull(persistedCategory.getDescription());
		assertNotNull(persistedCategory.getCreatedAt());
		
	    assertTrue(persistedCategory.getKey() > 0);
		
		assertNotNull(persistedCategory.getKey());
		assertNotNull(persistedCategory.getName());
		assertNotNull(persistedCategory.getDescription());
		assertNotNull(persistedCategory.getCreatedAt());
		
		assertTrue(persistedCategory.getKey() > 0);
		
		assertEquals("Technology", persistedCategory.getName());
		assertEquals("Posts related to technology trends and news", persistedCategory.getDescription());
		
		assertTrue(persistedCategory.getCreatedAt()
				.truncatedTo(ChronoUnit.SECONDS)
				.isEqual(now
						.truncatedTo(ChronoUnit.SECONDS)));	
	} 
	
	@Test
	@Order(4)
	public void testFindByIdWithWrongOrigin() throws JsonMappingException, JsonProcessingException {
		mockCategory();	
		
	/*	specification = new RequestSpecBuilder()
				.addHeader(TestConfigs.HEADER_PARAM_ORIGIN, TestConfigs.ORIGIN_ICLASS)
				.setBasePath("/api/category/v1")
				.setPort(TestConfigs.SERVER_PORT)
					.addFilter(new RequestLoggingFilter(LogDetail.ALL))
					.addFilter(new ResponseLoggingFilter(LogDetail.ALL))
				.build();	*/	
		
		var content = given().spec(specification)
				.contentType(TestConfigs.CONTENT_TYPE_JSON)
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
	
	/*@Test
	@Order(5)
	public void testDelete() throws JsonMappingException, JsonProcessingException {

		given().spec(specification)
			.contentType(TestConfigs.CONTENT_TYPE_JSON)
				.pathParam("id", category.getId())
				.when()
				.delete("{id}")
			.then()
				.statusCode(204);
	}	*/

	private void mockCategory() {
		//category.setId(1L);
	    category.setName("Technology");
	    category.setDescription("Posts related to technology trends and news");
	    category.setCreatedAt(now);
	}	

}