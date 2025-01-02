package io.github.alanabarbosa.integrationtests.controller.cors.withjson;

import static io.restassured.RestAssured.given;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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
import io.github.alanabarbosa.integrationtests.testcontainers.AbstractIntegrationTest;
import io.github.alanabarbosa.integrationtests.vo.AccountCredentialsVO;
import io.github.alanabarbosa.integrationtests.vo.PostVO;
import io.github.alanabarbosa.integrationtests.vo.TokenVO;
import io.github.alanabarbosa.model.Category;
import io.github.alanabarbosa.model.File;
import io.github.alanabarbosa.model.Role;
import io.github.alanabarbosa.model.User;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.specification.RequestSpecification;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestMethodOrder(OrderAnnotation.class)
public class PostControllerCorsJsonTest extends AbstractIntegrationTest{
	
	private static RequestSpecification specification ;
	private static ObjectMapper objectMapper;
	private static PostVO post;

	LocalDateTime now = LocalDateTime.now();
	
	@BeforeAll
	public static void setup() {
		objectMapper = new ObjectMapper();
		objectMapper.registerModule(new JavaTimeModule());
		objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
		objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
		objectMapper.enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT);

	    post = new PostVO();
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
				.setBasePath("/api/post/v1")
				.setPort(TestConfigs.SERVER_PORT)
					.addFilter(new RequestLoggingFilter(LogDetail.ALL))
					.addFilter(new ResponseLoggingFilter(LogDetail.ALL))
				.build();
	}

	@Test
	@Order(1)
	public void testCreate() throws JsonMappingException, JsonProcessingException {
		mockPost();
		
	    //ObjectMapper objectMapper = new ObjectMapper();
	    objectMapper.registerModule(new JavaTimeModule());
	    objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);		
		
		var content = given().spec(specification)
				.contentType(TestConfigs.CONTENT_TYPE_JSON)
					.body(post)
					.when()
					.post()
				.then()
					.statusCode(200)
						.extract()
						.body()
							.asString();
		
		PostVO persistedPost = objectMapper.readValue(content, PostVO.class);
		post = persistedPost;
		
		assertNotNull(persistedPost);
		
		assertNotNull(persistedPost.getId());
		assertNotNull(persistedPost.getTitle());
		assertNotNull(persistedPost.getContent());
		assertNotNull(persistedPost.getSlug());
		assertTrue(persistedPost.getStatus()); 
		assertNotNull(persistedPost.getCreatedAt());
		//assertNotNull(persistedPost.getCategory());
		assertNotNull(persistedPost.getUpdatedAt());
		assertNotNull(persistedPost.getPublishedAt());
		assertNotNull(persistedPost.getUser());
		assertNull(persistedPost.getImageDesktop());
	    assertNull(persistedPost.getImageMobile());
		
		assertTrue(persistedPost.getId() > 0);
		
		assertEquals("Content Negotiation using Spring MVC", persistedPost.getTitle());
		assertEquals("In this post I want to discuss how to configure and use content "
		        + "negotiation with Spring, mostly in terms of RESTful Controllers using HTTP "
		        + "message converters. In a later post I will show how to setup content negotiation "
		        + "specifically for use with views using Spring's ContentNegotiatingViewResolver.", 
		        persistedPost.getContent());
		
		assertEquals(null, persistedPost.getImageDesktop()); 
		assertEquals(null, persistedPost.getImageMobile());
		//assertEquals(1L, persistedPost.getCategory().getId());
		assertEquals(1L, persistedPost.getUser().getId());		
	}
	
	@Test
	@Order(2)
	public void testCreateWithWrongOrigin() throws JsonMappingException, JsonProcessingException {
		mockPost();
		
		var content = given().spec(specification)
				.contentType(TestConfigs.CONTENT_TYPE_JSON)
					.header(TestConfigs.HEADER_PARAM_ORIGIN, TestConfigs.ORIGIN_ICLASS)
					.body(post)
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
		System.out.println("Inicio do testFindById");
		mockPost();
		
		var content = given().spec(specification)
				.contentType(TestConfigs.CONTENT_TYPE_JSON)
					.header(TestConfigs.HEADER_PARAM_ORIGIN, TestConfigs.ORIGIN_ALANA)
						.pathParam("id", post.getId())
						.when()
						.get("{id}")
					.then()
						.statusCode(200)
							.extract()
							.body()
								.asString();
		
		PostVO persistedPost = objectMapper.readValue(content, PostVO.class);
		post = persistedPost;
		
		System.out.println(post);
		
		assertNotNull(persistedPost);
		
		assertNotNull(persistedPost.getId());
		assertNotNull(persistedPost.getTitle());
		assertNotNull(persistedPost.getContent());
		assertNotNull(persistedPost.getSlug());
		assertNotNull(persistedPost.getCreatedAt());
		assertNotNull(persistedPost.getUpdatedAt());
		assertNotNull(persistedPost.getPublishedAt());		
		assertNotNull(persistedPost.getCategory());
		assertNotNull(persistedPost.getUser());
		assertNull(persistedPost.getImageDesktop());
		assertNull(persistedPost.getImageMobile());
		
	    assertTrue(persistedPost.getId() > 0);
		
		assertEquals("Content Negotiation using Spring MVC", persistedPost.getTitle());
		assertEquals("In this post I want to discuss how to configure and use content "
		        + "negotiation with Spring, mostly in terms of RESTful Controllers using HTTP "
		        + "message converters. In a later post I will show how to setup content negotiation "
		        + "specifically for use with views using Spring's ContentNegotiatingViewResolver.", 
		        persistedPost.getContent());	
	    
		assertNull(null, persistedPost.getImageDesktop());
		assertNull(null, persistedPost.getImageMobile());
		assertEquals(1L, persistedPost.getCategory().getId());
		assertEquals(1L, persistedPost.getUser().getId());
	} 
	
	@Test
	@Order(4)
	public void testFindByIdWithWrongOrigin() throws JsonMappingException, JsonProcessingException {
		mockPost();	
		
		var content = given().spec(specification)
				.contentType(TestConfigs.CONTENT_TYPE_JSON)
					.header(TestConfigs.HEADER_PARAM_ORIGIN, TestConfigs.ORIGIN_ICLASS)
					.pathParam("id", post.getId())
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

	private void mockPost() {
		Category category = new Category();
		category.setId(1L);
		category.setCreatedAt(now);
		category.setDescription("This is a description a category");
		category.setName("This is a category");
		
	    post.setTitle("Content Negotiation using Spring MVC");
	    post.setContent("In this post I want to discuss how to configure and use content "
	            + "negotiation with Spring, mostly in terms of RESTful Controllers using HTTP "
	            + "message converters. In a later post I will show how to setup content negotiation "
	            + "specifically for use with views using Spring's ContentNegotiatingViewResolver.");
	    post.setSlug("content-negotiation-using-spring-mvc");
	    post.setStatus(true);
	    post.setCreatedAt(now);
	    post.setUpdatedAt(now);
	    post.setPublishedAt(now);

	    File imageDesktop = new File();
	    imageDesktop.setId(null);

	    File imageMobile = new File();
	    imageMobile.setId(null);

	    User user = new User();
	    user.setId(1L);
	    user.setBio("This is a briograph");
	    user.setCreatedAt(now);
	    user.setEnabled(true);
	    user.setFirstName("Son");
	    user.setLastName("Goku");
	    user.setUserName("songoku");
	    
	    Role role = new Role();
	    role.setId(1L);
	    
	    List<Role> roles = new ArrayList<>();
	    roles.add(role);
	    
	    user.setRoles(roles);

	    post.setUser(user);
	    post.setCategory(category);
	    post.setImageDesktop(null);
	    post.setImageMobile(null);
	}	

}