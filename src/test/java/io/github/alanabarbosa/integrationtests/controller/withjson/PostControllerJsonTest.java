package io.github.alanabarbosa.integrationtests.controller.withjson;

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
import io.github.alanabarbosa.integrationtests.vo.UserVO;
import io.github.alanabarbosa.integrationtests.vo.wrappers.WrapperPostVO;
import io.github.alanabarbosa.integrationtests.vo.wrappers.WrapperUserVO;
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
public class PostControllerJsonTest extends AbstractIntegrationTest{
	
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
	public void testUpdate() throws JsonMappingException, JsonProcessingException {
		post.setTitle("Content Negotiation using Spring MVC");
		
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
		assertNotNull(persistedPost.getUpdatedAt());
		assertNotNull(persistedPost.getCategory());
		assertNotNull(persistedPost.getUser());
		assertNull(persistedPost.getImageDesktop());
	    assertNull(persistedPost.getImageMobile());
		
	    assertEquals(post.getId(), persistedPost.getId());
		
		assertEquals("Content Negotiation using Spring MVC", persistedPost.getTitle());
		assertEquals("In this post I want to discuss how to configure and use content "
		        + "negotiation with Spring, mostly in terms of RESTful Controllers using HTTP "
		        + "message converters. In a later post I will show how to setup content negotiation "
		        + "specifically for use with views using Spring's ContentNegotiatingViewResolver.", 
		        persistedPost.getContent());
		
		assertEquals("content-negotiation-using-spring-mvc", persistedPost.getSlug());
		assertEquals(true, persistedPost.getStatus());
		
		//assertTrue(persistedPost.getCreatedAt()
		//		.truncatedTo(ChronoUnit.SECONDS)
		//		.isEqual(now
		//				.truncatedTo(ChronoUnit.SECONDS)));
		
		//assertTrue(persistedPost.getUpdatedAt()
		//		.truncatedTo(ChronoUnit.SECONDS)
		//		.isEqual(now
		//				.truncatedTo(ChronoUnit.SECONDS)));
	
		assertEquals(null, persistedPost.getImageDesktop()); 
		assertEquals(null, persistedPost.getImageMobile());
		assertEquals(1L, persistedPost.getCategory().getId());
		assertEquals(1L, persistedPost.getUser().getId());		
	}
	
	@Test
	@Order(4)
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
	@Order(5)
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
	
	@Test
	@Order(6)
	public void findPostsByUserId() throws JsonMappingException, JsonProcessingException {
		mockPost();
		
		var content = given().spec(specification)
				.contentType(TestConfigs.CONTENT_TYPE_JSON)
				.accept(TestConfigs.CONTENT_TYPE_JSON)
				.pathParam("id", 1)
				.queryParams("page", 0, "size", 10, "direction", "asc")
					.when()
					.get("user/{id}")
				.then()
					.statusCode(200)
						.extract()
						.body()
							.asString();
		
		WrapperPostVO wrapper = objectMapper
				.readValue(content, WrapperPostVO.class);
		
		var p = wrapper.getEmbedded().getPosts();
		
		
		PostVO founPostOne = p.get(0);
		
		assertNotNull(founPostOne.getId());
		assertEquals(91, founPostOne.getId());		
		assertEquals("Jane Austen Book Club, The", founPostOne.getTitle());
	}
	
	@Test
	@Order(7)
	public void findPostsByCategoryId() throws JsonMappingException, JsonProcessingException {
		mockPost();
		
		var content = given().spec(specification)
				.contentType(TestConfigs.CONTENT_TYPE_JSON)
				.accept(TestConfigs.CONTENT_TYPE_JSON)
				.pathParam("id", 1)
				.queryParams("page", 0, "size", 10, "direction", "asc")
					.when()
					.get("category/{id}")
				.then()
					.statusCode(200)
						.extract()
						.body()
							.asString();
		
		WrapperPostVO wrapper = objectMapper
				.readValue(content, WrapperPostVO.class);
		
		var p = wrapper.getEmbedded().getPosts();
		
		
		PostVO founPostOne = p.get(0);
		
		assertNotNull(founPostOne.getId());
		assertEquals(3, founPostOne.getId());		
		assertEquals("Sebastian", founPostOne.getTitle());
	}	
	
	@Test
	@Order(8)
	public void testFindAll() throws JsonMappingException, JsonProcessingException {
		
		var content = given().spec(specification)
				.queryParams("page", 0, "size", 10, "direction", "asc")
				.contentType(TestConfigs.CONTENT_TYPE_JSON)
					.when()
					.get()
				.then()
					.statusCode(200)
						.extract()
						.body()
							.asString();
		
		WrapperPostVO wrapper = objectMapper
				.readValue(content, WrapperPostVO.class);
		
		var post = wrapper.getEmbedded().getPosts();
		
		PostVO foundPostOne = post.get(0);
		
		assertNotNull(foundPostOne.getId());
		assertNotNull(foundPostOne.getTitle());
		
		assertEquals(793, foundPostOne.getId());
		assertEquals("(Absolutions) Pipilotti's Mistakes ((Entlastungen) Pipilottis Fehler)", foundPostOne.getTitle());
		
		PostVO foundPostSix = post.get(3);
		
		assertNotNull(foundPostSix.getId());
		assertNotNull(foundPostSix.getTitle());
		
		assertEquals(447, foundPostSix.getId());		
		assertEquals("11 Flowers (Wo 11)", foundPostSix.getTitle());
	}
	
	@Test
	@Order(9)
	public void testDisablePostById() throws JsonMappingException, JsonProcessingException {
			
		var content = given().spec(specification)
				.contentType(TestConfigs.CONTENT_TYPE_JSON)
					.pathParam("id", post.getId())
					.when()
					.patch("{id}")
				.then()
					.statusCode(200)
						.extract()
						.body()
							.asString();
		
		PostVO persistedPost = objectMapper.readValue(content, PostVO.class);
		
		assertNotNull(persistedPost);
		
		assertNotNull(persistedPost.getId());
		assertNotNull(persistedPost.getTitle());
		assertNotNull(persistedPost.getContent());
		assertNotNull(persistedPost.getSlug());
		assertNotNull(persistedPost.getCreatedAt());
		assertNotNull(persistedPost.getUpdatedAt());
		assertNotNull(persistedPost.getCategory());
		assertNotNull(persistedPost.getUser());
		
		assertEquals(post.getId(), persistedPost.getId());
		
		assertEquals("Content Negotiation using Spring MVC", persistedPost.getTitle());
		assertEquals("In this post I want to discuss how to configure and use content "
				+ "negotiation with Spring, mostly in terms of RESTful Controllers using HTTP "
				+ "message converters. In a later post I will show how to setup content negotiation "
				+ "specifically for use with views using Spring's ContentNegotiatingViewResolver.", persistedPost.getContent());
	}	
	
	@Test
	@Order(9)
	public void testDelete() throws JsonMappingException, JsonProcessingException {

		given().spec(specification)
			.contentType(TestConfigs.CONTENT_TYPE_JSON)
				.pathParam("id", post.getId())
				.when()
				.delete("{id}")
			.then()
				.statusCode(204);
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