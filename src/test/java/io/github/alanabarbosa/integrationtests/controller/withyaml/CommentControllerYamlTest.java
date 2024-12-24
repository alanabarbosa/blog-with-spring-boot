package io.github.alanabarbosa.integrationtests.controller.withyaml;

import static io.restassured.RestAssured.given;
import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
import io.github.alanabarbosa.integrationtests.vo.CommentVO;
import io.github.alanabarbosa.integrationtests.vo.PostVO;
import io.github.alanabarbosa.integrationtests.vo.TokenVO;
import io.github.alanabarbosa.model.Role;
import io.github.alanabarbosa.model.User;
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
public class CommentControllerYamlTest extends AbstractIntegrationTest{
	
	private static RequestSpecification specification;
	private static YMLMapper objectMapper;
	
	private static CommentVO comment;	
		
	LocalDateTime now = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
	
	@BeforeAll
    public static void setup() {
        objectMapper = new YMLMapper();
        comment = new CommentVO();
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
	    
		var persistedComment = given().spec(specification)
        	    .config(
        	        RestAssuredConfig
        	            .config()
        	            .encoderConfig(EncoderConfig.encoderConfig()
        	                .encodeContentTypeAs(
        	                    TestConfigs.CONTENT_TYPE_YML,
        	                    ContentType.TEXT)))
        	    .contentType(TestConfigs.CONTENT_TYPE_YML)
        	    .accept(TestConfigs.CONTENT_TYPE_YML)
        	    	.body(comment, objectMapper)
        	    	.when()
        	    	.post()
        	    .then()
        	    	.log().all()
        	        .statusCode(200)
        	        	.extract()
        	        	.body()
        	        		.as(CommentVO.class, objectMapper);
		
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
		//assertTrue(persistedComment.getCreatedAt()
		//	    .truncatedTo(ChronoUnit.SECONDS)
		//	    .isEqual(now.truncatedTo(ChronoUnit.SECONDS)));
		assertEquals(1L, persistedComment.getPost().getId());
		assertEquals(1L, persistedComment.getUser().getId());
	}
	
	
	@Test
	@Order(2)
	public void testCreateWithWrongOrigin() throws JsonMappingException, JsonProcessingException {
		mockComment();
		
		var content = given().spec(specification)
				.config(
						RestAssuredConfig
							.config()
							.encoderConfig(EncoderConfig.encoderConfig()
								.encodeContentTypeAs(
									TestConfigs.CONTENT_TYPE_YML,
									ContentType.TEXT)))
				.contentType(TestConfigs.CONTENT_TYPE_YML)
				.accept(TestConfigs.CONTENT_TYPE_YML)
					.header(TestConfigs.HEADER_PARAM_ORIGIN, TestConfigs.ORIGIN_ICLASS)
					.body(comment, new YMLMapper())
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
		comment.setContent("Great article! Thanks for sharing.");
		
		var persistedComment = given().spec(specification)
				.config(
						RestAssuredConfig
							.config()
							.encoderConfig(EncoderConfig.encoderConfig()
								.encodeContentTypeAs(
									TestConfigs.CONTENT_TYPE_YML,
									ContentType.TEXT)))
				.contentType(TestConfigs.CONTENT_TYPE_YML)
				.accept(TestConfigs.CONTENT_TYPE_YML)
					.body(comment, objectMapper)
					.when()
					.post()
				.then()
					.statusCode(200)
						.extract()
						.body()
						.as(CommentVO.class, objectMapper);
		
		comment = persistedComment;

        assertNotNull(persistedComment.getKey());
        assertNotNull(persistedComment.getContent());
        assertTrue(persistedComment.getStatus());
        assertNotNull(persistedComment.getCreatedAt());
        assertNotNull(persistedComment.getPost());
        assertNotNull(persistedComment.getUser());
        
        assertEquals(comment.getKey(), persistedComment.getKey());
        
        assertEquals("Great article! Thanks for sharing.", persistedComment.getContent());
        assertEquals(1L, persistedComment.getUser().getId());


	}
	
	@Test
	@Order(4)
	public void testFindById() throws JsonMappingException, JsonProcessingException {
		mockComment();
			
		var persistedComment = given().spec(specification)
				.config(
						RestAssuredConfig
							.config()
							.encoderConfig(EncoderConfig.encoderConfig()
								.encodeContentTypeAs(
									TestConfigs.CONTENT_TYPE_YML,
									ContentType.TEXT)))
				.contentType(TestConfigs.CONTENT_TYPE_YML)
				.accept(TestConfigs.CONTENT_TYPE_YML)
					.pathParam("id", comment.getKey())
					.when()
					.get("{id}")
				.then()
					.statusCode(200)
						.extract()
						.body()
						.as(CommentVO.class, objectMapper);
		
		//CommentVO persistedComment = objectMapper.readValue(content, CommentVO.class);
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
	@Order(5)
	public void testFindByIdWithWrongOrigin() throws JsonMappingException, JsonProcessingException {
		mockComment();	
		
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
					.pathParam("id", comment.getKey())
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
	@Order(6)
	public void testFindAll() throws JsonMappingException, JsonProcessingException {
		
		var content = given().spec(specification)
				.config(
						RestAssuredConfig
							.config()
							.encoderConfig(EncoderConfig.encoderConfig()
								.encodeContentTypeAs(
									TestConfigs.CONTENT_TYPE_YML,
									ContentType.TEXT)))
				.contentType(TestConfigs.CONTENT_TYPE_YML)
				.accept(TestConfigs.CONTENT_TYPE_YML)
					.when()
					.get()
				.then()
					.statusCode(200)
						.extract()
						.body()
						.as(CommentVO[].class, objectMapper);
		
		List<CommentVO> c = Arrays.asList(content);
		
		CommentVO founCommentOne = c.get(0);
		
		assertNotNull(founCommentOne.getKey());
		assertNotNull(founCommentOne.getContent());
		assertTrue(founCommentOne.getStatus());
		assertNotNull(founCommentOne.getCreatedAt());
		assertNotNull(founCommentOne.getPost());
		assertNotNull(founCommentOne.getUser());
		
		assertEquals(1, founCommentOne.getKey());
		
		assertEquals("Great article! Thanks for sharing.", founCommentOne.getContent());	

		assertEquals(1L, founCommentOne.getPost().getId());
		assertEquals(1L, founCommentOne.getUser().getId());
		
		CommentVO foundCommentThree = c.get(3);
		
		assertNotNull(foundCommentThree.getKey());
		assertNotNull(foundCommentThree.getContent());
		assertTrue(foundCommentThree.getStatus());
		assertNotNull(foundCommentThree.getCreatedAt());
		assertNotNull(foundCommentThree.getPost());
		assertNotNull(foundCommentThree.getUser());
		
		assertEquals(4, foundCommentThree.getKey());
		
		assertEquals("Great article! Thanks for sharing.", foundCommentThree.getContent());		

		assertEquals(1L, foundCommentThree.getPost().getId());
		assertEquals(1L, foundCommentThree.getUser().getId());
	}
	
	@Test
	@Order(7)
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
	    user.setBio("Software developer and tech enthusiast.");
	    user.setCreatedAt(now);
	    user.setFirstName("Alana");
	    user.setLastName("Barbosa");
	    user.setUserName("alana");
	    user.setEnabled(true);
	    
	    List<Role> roles = new ArrayList<>();
	    Role role = new Role();
	    role.setId(1L);
	    role.setDescription("Admin");
	    roles.add(role);
	    user.setRoles(roles);

	    comment.setPost(post);
	    comment.setUser(user);
	}
}