package io.github.alanabarbosa.integrationtests.controller.withyaml;

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
import com.fasterxml.jackson.databind.JsonMappingException;

import io.github.alanabarbosa.configs.TestConfigs;
import io.github.alanabarbosa.integrationtests.controller.withyaml.mapper.YMLMapper;
import io.github.alanabarbosa.integrationtests.testcontainers.AbstractIntegrationTest;
import io.github.alanabarbosa.integrationtests.vo.AccountCredentialsVO;
import io.github.alanabarbosa.integrationtests.vo.CategoryVO;
import io.github.alanabarbosa.integrationtests.vo.TokenVO;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.config.EncoderConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.filter.log.LogDetail;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestMethodOrder(OrderAnnotation.class)
public class CategoryControllerYamlTest extends AbstractIntegrationTest {
	
	private static RequestSpecification specification;
	private static YMLMapper objectMapper;
	
	private static CategoryVO category;
	LocalDateTime now = LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS);

    @BeforeAll
    public static void setup() {
        objectMapper = new YMLMapper();
        category = new CategoryVO();
    }

	@Test
	@Order(0)
	public void authorization() throws JsonMappingException, JsonProcessingException {
		AccountCredentialsVO user = new AccountCredentialsVO("alana", "admin123");
		
		var accessToken = given()
				.config(
						RestAssuredConfig
							.config()
							.encoderConfig(EncoderConfig.encoderConfig()
								.encodeContentTypeAs(
									TestConfigs.CONTENT_TYPE_YML,
									ContentType.TEXT)))
				.basePath("/auth/signin")
					.port(TestConfigs.SERVER_PORT)
					.contentType(TestConfigs.CONTENT_TYPE_YML)
					.accept(TestConfigs.CONTENT_TYPE_YML)
				.body(user, objectMapper)
					.when()
				.post()
					.then()
						.statusCode(200)
							.extract()
							.body()
								.as(TokenVO.class, objectMapper)
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
        
        var persistedCategory = given().spec(specification)
        	    .config(
        	        RestAssuredConfig
        	            .config()
        	            .encoderConfig(EncoderConfig.encoderConfig()
        	                .encodeContentTypeAs(
        	                    TestConfigs.CONTENT_TYPE_YML,
        	                    ContentType.TEXT)))
        	    .contentType(TestConfigs.CONTENT_TYPE_YML)
        	    .accept(TestConfigs.CONTENT_TYPE_YML)
        	    	.body(category, objectMapper)
        	    	.when()
        	    	.post()
        	    .then()
        	    	.log().all()
        	        .statusCode(200)
        	        	.extract()
        	        	.body()
        	        		.as(CategoryVO.class, objectMapper);

        category = persistedCategory;
        System.out.println("Persisted Category Response: " + persistedCategory);
        
        assertNotNull(persistedCategory);

        assertNotNull(persistedCategory.getKey());
        assertNotNull(persistedCategory.getName());
        assertNotNull(persistedCategory.getDescription());
        assertNotNull(persistedCategory.getCreatedAt());

        assertTrue(persistedCategory.getKey() > 0);
        assertEquals("Technology", persistedCategory.getName());
        assertEquals("Posts related to technology trends and news", persistedCategory.getDescription());
    }
	/*
	@Test
	@Order(2)
	public void testCreateWithWrongOrigin() throws JsonMappingException, JsonProcessingException {
		mockCategory();
		
		var content = given().spec(specification)
				.contentType(TestConfigs.CONTENT_TYPE_YML)
				.accept(TestConfigs.CONTENT_TYPE_YML)
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
	
    public void testFindById() throws JsonProcessingException {
        mockCategory();

        var persistedCategory = given().spec(specification)
				.config(
						RestAssuredConfig
							.config()
							.encoderConfig(EncoderConfig.encoderConfig()
								.encodeContentTypeAs(
									TestConfigs.CONTENT_TYPE_YML,
									ContentType.TEXT)))
                .contentType(TestConfigs.CONTENT_TYPE_YML)
                .accept(TestConfigs.CONTENT_TYPE_YML)
                	.pathParam("id", category.getKey())
                .when()
                	.get("{id}")
                .then()
                	.statusCode(200)
                	.extract()
                		.body()
                			.asString();

        //CategoryVO persistedCategory = objectMapper.readValue(content, CategoryVO.class);
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
		
        var persistedCategory = given().spec(specification)
				.config(
						RestAssuredConfig
							.config()
							.encoderConfig(EncoderConfig.encoderConfig()
								.encodeContentTypeAs(
									TestConfigs.CONTENT_TYPE_YML,
									ContentType.TEXT)))
                .contentType(TestConfigs.CONTENT_TYPE_YML)
					.header(TestConfigs.HEADER_PARAM_ORIGIN, TestConfigs.ORIGIN_ICLASS)
					.pathParam("id", category.getKey())
					.when()
					.get("{id}")
				.then()
					.statusCode(403)
						.extract()
						.body()
							.asString();
		
	
		assertNotNull(persistedCategory);
		assertEquals("Invalid CORS request", persistedCategory);
	}	
	
	@Test
	@Order(5)
	public void testDelete() throws JsonMappingException, JsonProcessingException {

        given().spec(specification)
				.config(
						RestAssuredConfig
							.config()
							.encoderConfig(EncoderConfig.encoderConfig()
								.encodeContentTypeAs(
									TestConfigs.CONTENT_TYPE_YML,
									ContentType.TEXT)))
                .contentType(TestConfigs.CONTENT_TYPE_YML)
			.contentType(TestConfigs.CONTENT_TYPE_YML)
				.pathParam("id", category.getKey())
				.when()
				.delete("{id}")
			.then()
				.statusCode(204);
	}
*/
	private void mockCategory() {
		//category.setKey(1L);
	    category.setName("Technology");
	    category.setDescription("Posts related to technology trends and news");
	    category.setCreatedAt(now);
	}
}