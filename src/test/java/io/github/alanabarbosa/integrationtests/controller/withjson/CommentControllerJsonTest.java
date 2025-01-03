package io.github.alanabarbosa.integrationtests.controller.withjson;

import static io.restassured.RestAssured.given;
import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
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
import io.github.alanabarbosa.integrationtests.vo.CommentVO;
import io.github.alanabarbosa.integrationtests.vo.PostVO;
import io.github.alanabarbosa.integrationtests.vo.TokenVO;
import io.github.alanabarbosa.integrationtests.vo.wrappers.WrapperCommentVO;
import io.github.alanabarbosa.model.Role;
import io.github.alanabarbosa.model.User;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.specification.RequestSpecification;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestMethodOrder(OrderAnnotation.class)
public class CommentControllerJsonTest extends AbstractIntegrationTest{
	
	private static RequestSpecification specification;
	private static ObjectMapper objectMapper;
	private static CommentVO comment;

	LocalDateTime now = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
	
	@BeforeAll
	public static void setup() {
	    objectMapper = new ObjectMapper();
	    objectMapper.registerModule(new JavaTimeModule());
	    objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
	    objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
	    objectMapper.enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT);
	    
	    objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSSSSS"));
	    
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
		
		assertTrue(persistedComment.getKey() > 0);
		
		assertEquals("Great article! Thanks for sharing.", persistedComment.getContent());
		/*assertTrue(persistedComment.getCreatedAt()
			    .truncatedTo(ChronoUnit.SECONDS)
			    .isEqual(now.truncatedTo(ChronoUnit.SECONDS)));*/
		assertEquals(1L, persistedComment.getPost().getId());
		assertEquals(1L, persistedComment.getUser().getId());
	}
	
	@Test
	@Order(2)
	public void testCreateAllWithoutToken() throws JsonMappingException, JsonProcessingException {
		
		RequestSpecification specificationWithoutToken = new RequestSpecBuilder()
			.setBasePath("/api/comment/v1")
			.setPort(TestConfigs.SERVER_PORT)
				.addFilter(new RequestLoggingFilter(LogDetail.ALL))
				.addFilter(new ResponseLoggingFilter(LogDetail.ALL))
			.build();
		
		given().spec(specificationWithoutToken)
			.contentType(TestConfigs.CONTENT_TYPE_JSON)
				.when()
				.post()
			.then()
				.statusCode(403);
	}
	
	@Test
	@Order(3)
	public void testUpdate() throws JsonMappingException, JsonProcessingException {
		comment.setContent("Great article! Thanks for sharing.");
		mockComment();
		
	    objectMapper.registerModule(new JavaTimeModule());
	    objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
	    
		var content = given().spec(specification)
				.contentType(TestConfigs.CONTENT_TYPE_JSON)
					.body(comment)
					.when()
					.post()
				.then()
					.statusCode(200)
						.extract()
						.body()
							.asString();
		
		CommentVO persistedComment = objectMapper.readValue(content, CommentVO.class);
		
		comment = persistedComment;
		
		assertNotNull(persistedComment);
		
		assertNotNull(persistedComment.getKey());
		assertNotNull(persistedComment.getContent());
		assertTrue(persistedComment.getStatus());
		assertNotNull(persistedComment.getCreatedAt());
		assertNotNull(persistedComment.getPost());
		assertNotNull(persistedComment.getUser());
		
		assertEquals(comment.getKey(), persistedComment.getKey());
		
		assertEquals("Great article! Thanks for sharing.", persistedComment.getContent());
		

		assertEquals(1L, persistedComment.getPost().getId());
		assertEquals(1L, persistedComment.getUser().getId());
	}
	
	@Test
	@Order(4)
	public void testFindById() throws JsonMappingException, JsonProcessingException {
		mockComment();
			
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
	} 
	
	@Test
	@Order(5)
	public void findCommentsByUserId() throws JsonMappingException, JsonProcessingException {
		mockComment();
		
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
		
		WrapperCommentVO wrapper = objectMapper
				.readValue(content, WrapperCommentVO.class);		

		var c = wrapper.getEmbedded().getComments();
		
		CommentVO foundCommentOne = c.get(0);
		
		assertNotNull(foundCommentOne.getKey());
		assertNotNull(foundCommentOne.getContent());
		
		assertEquals(1, foundCommentOne.getKey());
		
		assertEquals("Great article! Thanks for sharing.", foundCommentOne.getContent());
	}
	
	@Test
	@Order(6)
	public void findCommentsByPostId() throws JsonMappingException, JsonProcessingException {
		mockComment();
		
		var content = given().spec(specification)
				.contentType(TestConfigs.CONTENT_TYPE_JSON)
				.accept(TestConfigs.CONTENT_TYPE_JSON)
				.pathParam("id", 1)
				.queryParams("page", 0, "size", 10, "direction", "asc")
					.when()
					.get("post/{id}")
				.then()
					.statusCode(200)
						.extract()
						.body()
							.asString();
		
		WrapperCommentVO wrapper = objectMapper
				.readValue(content, WrapperCommentVO.class);		

		var c = wrapper.getEmbedded().getComments();
		
		CommentVO foundCommentOne = c.get(0);
		
		assertNotNull(foundCommentOne.getKey());
		assertNotNull(foundCommentOne.getContent());
		
		assertEquals(1, foundCommentOne.getKey());
		
		assertEquals("Great article! Thanks for sharing.", foundCommentOne.getContent());
	}	
	
	@Test
	@Order(7)
	public void testHATEOAS() throws JsonMappingException, JsonProcessingException {
		
		var content = given().spec(specification)
				.contentType(TestConfigs.CONTENT_TYPE_JSON)
				.accept(TestConfigs.CONTENT_TYPE_JSON)
				.queryParams("page", 3, "size", 12, "direction", "asc")
					.when()
					.get()
				.then()
					.statusCode(200)
						.extract()
						.body()
							.asString();
		
		assertTrue(content.contains("\"_links\":{\"comment-details\":{\"href\":\"http://localhost:8888/api/comment/v1/530\"}}}"));
		assertTrue(content.contains("\"_links\":{\"comment-details\":{\"href\":\"http://localhost:8888/api/comment/v1/458\"}}}"));
		assertTrue(content.contains("\"_links\":{\"comment-details\":{\"href\":\"http://localhost:8888/api/comment/v1/490\"}}}"));		
		
		assertTrue(content.contains("{\"first\":{\"href\":\"http://localhost:8888/api/comment/v1?direction=asc&page=0&size=12&sort=content,asc\"}"));
		assertTrue(content.contains("\"prev\":{\"href\":\"http://localhost:8888/api/comment/v1?direction=asc&page=2&size=12&sort=content,asc\"}"));
		assertTrue(content.contains("\"self\":{\"href\":\"http://localhost:8888/api/comment/v1?page=3&size=12&direction=asc\"}"));
		assertTrue(content.contains("\"next\":{\"href\":\"http://localhost:8888/api/comment/v1?direction=asc&page=4&size=12&sort=content,asc\"}"));
		assertTrue(content.contains("\"last\":{\"href\":\"http://localhost:8888/api/comment/v1?direction=asc&page=83&size=12&sort=content,asc\"}}"));
		
		assertTrue(content.contains("\"page\":{\"size\":12,\"totalElements\":1005,\"totalPages\":84,\"number\":3}}"));
	}
	
	@Test
	@Order(8)
	public void testFindAll() throws JsonMappingException, JsonProcessingException {
		
		var content = given().spec(specification)
				.contentType(TestConfigs.CONTENT_TYPE_JSON)
				.queryParams("page", 0, "size", 12, "direction", "asc")
					.when()
					.get()
				.then()
					.statusCode(200)
						.extract()
						.body()
							.asString();
		
		WrapperCommentVO wrapper = objectMapper
				.readValue(content, WrapperCommentVO.class);
		
		var comment = wrapper.getEmbedded().getComments();
		
		CommentVO founCommentOne = comment.get(0);
		
		assertNotNull(founCommentOne.getKey());
		assertNotNull(founCommentOne.getContent());
		
		assertEquals(957, founCommentOne.getKey());
		
		assertEquals("Aenean fermentum. Donec ut mauris eget massa tempor convallis. Nulla neque libero, convallis eget, eleifend luctus, ultricies eu, nibh.", founCommentOne.getContent());	

		CommentVO foundCommentThree = comment.get(3);
		
		assertNotNull(foundCommentThree.getKey());
		assertNotNull(foundCommentThree.getContent());
		
		assertEquals(397, foundCommentThree.getKey());
		
		assertEquals("Aenean fermentum. Donec ut mauris eget massa tempor convallis. Nulla neque libero, convallis eget, eleifend luctus, ultricies eu, nibh.", foundCommentThree.getContent());		
	}
	
	@Test
	@Order(9)
	public void testDelete() throws JsonMappingException, JsonProcessingException {

		given().spec(specification)
			.contentType(TestConfigs.CONTENT_TYPE_JSON)
				.pathParam("id", comment.getKey())
				.when()
				.delete("{id}")
			.then()
				.statusCode(204);
	}

	private void mockComment() {
	    comment.setContent("Great article! Thanks for sharing.");
	    comment.setStatus(true);
	    comment.setCreatedAt(now);
	    
	    PostVO post = new PostVO();
	    post.setId(1L);
	    post.setTitle("Content Negotiation using Spring MVC");
	    post.setContent("In this post I want to discuss...");
	    post.setSlug("content-negotiation-using-spring-mvc");
	    post.setStatus(true);
	    post.setCreatedAt(now);
	    post.setPublishedAt(now);
	    
	    User user = new User();
	    user.setId(1L);
	    user.setBio("This is a biography");
	    user.setCreatedAt(now);
	    user.setFirstName("Son");
	    user.setLastName("Goku");
	    user.setUserName("songoku");
	    user.setEnabled(true);
	    
	    Role role = new Role();
	    role.setId(1L);
	    
	    List<Role> roles = new ArrayList<>();
	    roles.add(role);
	    
	    user.setRoles(roles);	    

	    comment.setPost(post);
	    comment.setUser(user);
	}
}