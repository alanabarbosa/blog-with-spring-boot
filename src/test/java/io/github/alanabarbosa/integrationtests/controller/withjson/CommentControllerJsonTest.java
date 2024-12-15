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
import io.github.alanabarbosa.data.vo.v1.CommentVO;
import io.github.alanabarbosa.data.vo.v1.PostVO;
import io.github.alanabarbosa.data.vo.v1.UserVO;
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
public class CommentControllerJsonTest extends AbstractIntegrationTest{
	
	private static RequestSpecification specification ;
	private static ObjectMapper objectMapper;
	private static CommentVO comment;

	LocalDateTime now = LocalDateTime.now();
	
	@BeforeAll
	public static void setup() {
		objectMapper = new ObjectMapper();
		objectMapper.registerModule(new JavaTimeModule());
		objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
		objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
		objectMapper.enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT);

	    comment = new CommentVO();
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
				.setBasePath("/api/comment/v1")
				.setPort(TestConfigs.SERVER_PORT)
					.addFilter(new RequestLoggingFilter(LogDetail.ALL))
					.addFilter(new ResponseLoggingFilter(LogDetail.ALL))
				.build();
	}

	@Test
	@Order(1)
	public void testCreate() throws JsonMappingException, JsonProcessingException {
		mockComment();
		
	    ObjectMapper objectMapper = new ObjectMapper();
	    objectMapper.registerModule(new JavaTimeModule());
	    objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);				
		
	 /*   specification = new RequestSpecBuilder()
	            .addHeader(TestConfigs.HEADER_PARAM_ORIGIN, TestConfigs.ORIGIN_ALANA)
	            .setBasePath("/api/comment/v1")
	            .setPort(TestConfigs.SERVER_PORT)
		            .addFilter(new RequestLoggingFilter(LogDetail.ALL))
		            .addFilter(new ResponseLoggingFilter(LogDetail.ALL))
	            .build();*/
		
		var content = given().spec(specification)
				.contentType(TestConfigs.CONTENT_TYPE_JSON)
					.header(TestConfigs.HEADER_PARAM_ORIGIN, TestConfigs.ORIGIN_ALANA)
					.body(comment)
					.when()
					.post()
				.then()
					.statusCode(200)
						.extract()
						.body()
							.asString();
		
		System.out.println("Response Body: " + content);
		
		CommentVO persistedComment = objectMapper.readValue(content, CommentVO.class);
		
		comment = persistedComment;
		
		assertNotNull(persistedComment);
		
		assertNotNull(persistedComment.getKey());
		assertNotNull(persistedComment.getContent());
		assertTrue(persistedComment.getStatus());
		assertNotNull(persistedComment.getCreatedAt());
		assertNotNull(persistedComment.getPost());
		assertNotNull(persistedComment.getUser());
		//assertNotNull(persistedComment).getPost();
		
		assertTrue(persistedComment.getKey() > 0);
		
		assertEquals("Great article! Thanks for sharing.", persistedComment.getContent());
		//assertEquals(true, persistedComment.getStatus());		
		assertTrue(persistedComment.getCreatedAt()
			    .truncatedTo(ChronoUnit.SECONDS)
			    .isEqual(now.truncatedTo(ChronoUnit.SECONDS)));

		assertEquals(1L, persistedComment.getPost().getKey());
		assertEquals(1L, persistedComment.getUser().getKey());
		assertEquals(true, persistedComment.getUser().getAccountNonExpired());
		assertEquals(true, persistedComment.getUser().getAccountNonLocked());
		assertEquals(true, persistedComment.getUser().getCredentialsNonExpired());
	}
	
	@Test
	@Order(2)
	public void testCreateWithWrongOrigin() throws JsonMappingException, JsonProcessingException {
		mockComment();
		
		/*specification = new RequestSpecBuilder()
				.addHeader(TestConfigs.HEADER_PARAM_ORIGIN, TestConfigs.ORIGIN_ICLASS)
				.setBasePath("/api/comment/v1")
				.setPort(TestConfigs.SERVER_PORT)
					.addFilter(new RequestLoggingFilter(LogDetail.ALL))
					.addFilter(new ResponseLoggingFilter(LogDetail.ALL))
				.build();*/
		
		var content = given().spec(specification)
				.contentType(TestConfigs.CONTENT_TYPE_JSON)
					.header(TestConfigs.HEADER_PARAM_ORIGIN, TestConfigs.ORIGIN_ICLASS)
					.body(comment)
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
		mockComment();
		
		/*specification = new RequestSpecBuilder()
				.addHeader(TestConfigs.HEADER_PARAM_ORIGIN, TestConfigs.ORIGIN_ALANA)
				.setBasePath("/api/comment/v1")
				.setPort(TestConfigs.SERVER_PORT)
					.addFilter(new RequestLoggingFilter(LogDetail.ALL))
					.addFilter(new ResponseLoggingFilter(LogDetail.ALL))
				.build();*/
			
		var content = given().spec(specification)
				.contentType(TestConfigs.CONTENT_TYPE_JSON)
					.header(TestConfigs.HEADER_PARAM_ORIGIN, TestConfigs.ORIGIN_ALANA)
						.pathParam("id", comment.getKey())
						.when()
						.get("{id}")
					.then()
						.statusCode(200)
							.extract()
							.body()
								.asString();
		
		CommentVO persistedComment = objectMapper.readValue(content, CommentVO.class);
		comment = persistedComment;
		
		System.out.println(comment);
		
		assertNotNull(persistedComment);
		
		assertNotNull(persistedComment.getKey());
		assertNotNull(persistedComment.getContent());
		assertNotNull(persistedComment.getStatus());
		assertNotNull(persistedComment.getCreatedAt());
		
	    assertTrue(persistedComment.getKey() > 0);
		
		assertNotNull(persistedComment.getKey());
		assertNotNull(persistedComment.getContent());
		assertNotNull(persistedComment.getStatus());
		assertNotNull(persistedComment.getCreatedAt());
		
		assertTrue(persistedComment.getKey() > 0);
		
		assertEquals("Great article! Thanks for sharing.", persistedComment.getContent());
		
		assertTrue(persistedComment.getCreatedAt()
				.truncatedTo(ChronoUnit.SECONDS)
				.isEqual(now
						.truncatedTo(ChronoUnit.SECONDS)));	
	} 
	
	@Test
	@Order(4)
	public void testFindByIdWithWrongOrigin() throws JsonMappingException, JsonProcessingException {
		mockComment();	
		
	/*	specification = new RequestSpecBuilder()
				.addHeader(TestConfigs.HEADER_PARAM_ORIGIN, TestConfigs.ORIGIN_ICLASS)
				.setBasePath("/api/comment/v1")
				.setPort(TestConfigs.SERVER_PORT)
					.addFilter(new RequestLoggingFilter(LogDetail.ALL))
					.addFilter(new ResponseLoggingFilter(LogDetail.ALL))
				.build();	*/	
		
		var content = given().spec(specification)
				.contentType(TestConfigs.CONTENT_TYPE_JSON)
					.header(TestConfigs.HEADER_PARAM_ORIGIN, TestConfigs.ORIGIN_ICLASS)
					.pathParam("id", comment.getKey())
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
				.pathParam("id", comment.getId())
				.when()
				.delete("{id}")
			.then()
				.statusCode(204);
	}	*/


	private void mockComment() {
		//comment.setKey(1L);
	    comment.setContent("Great article! Thanks for sharing.");
	    comment.setStatus(true);
	    comment.setCreatedAt(now);
	    
	    PostVO post = new PostVO();
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
	    post.setKey(1L);
	    
	    CategoryVO category = new CategoryVO();
	    category.setKey(1L);
	    category.setCreatedAt(now);
	    category.setDescription("This is a description a category");
	    category.setName("This is a category");
	    
	    //comment.setCategory(category);
	    
	    UserVO user = new UserVO();
	    user.setKey(1L);
	    user.setBio("This is a briograph");
	    user.setCreatedAt(now);
	    user.setFirstName("Son");
	    user.setLastName("Goku");
	    user.setUserName("songoku");
	    user.setAccountNonExpired(true);
	    user.setAccountNonLocked(true);
	    user.setCredentialsNonExpired(true);
	    user.setEnabled(true);
	    
	    //user.setRoles();
	    
	    post.setUser(user);
	    post.setCategory(category);
	    
	    comment.setPost(post);
	    
	    UserVO userVO = new UserVO();
	    userVO.setKey(1L);
	    userVO.setBio("This is a briograph");
	    userVO.setCreatedAt(now);
	    userVO.setFirstName("Son");
	    userVO.setLastName("Goku");
	    userVO.setUserName("songoku");
	    userVO.setKey(1L);
	    userVO.setAccountNonExpired(true);
	    userVO.setAccountNonLocked(true);
	    userVO.setCredentialsNonExpired(true);
	    userVO.setEnabled(true);
	    
	    comment.setUser(userVO);	    
	}	

}