package io.github.alanabarbosa.integrationtests.controller.withyaml;

import static io.restassured.RestAssured.given;
import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.text.SimpleDateFormat;
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
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import io.github.alanabarbosa.configs.TestConfigs;
import io.github.alanabarbosa.integrationtests.controller.withyaml.mapper.YMLMapper;
import io.github.alanabarbosa.integrationtests.testcontainers.AbstractIntegrationTest;
import io.github.alanabarbosa.integrationtests.vo.AccountCredentialsVO;
import io.github.alanabarbosa.integrationtests.vo.CategoryVO;
import io.github.alanabarbosa.integrationtests.vo.PostVO;
import io.github.alanabarbosa.integrationtests.vo.TokenVO;
import io.github.alanabarbosa.model.Category;
import io.github.alanabarbosa.model.File;
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
public class PostControllerYamlTest extends AbstractIntegrationTest{
	
	private static RequestSpecification specification;
	private static YMLMapper objectMapper;
	
	private static PostVO post;
	LocalDateTime now = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);

    @BeforeAll
    public static void setup() {
        objectMapper = new YMLMapper();
        post = new PostVO();
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
	public void testCreate() throws JsonMappingException, JsonProcessingException {
		mockPost();
		
		System.out.println("Category antes do envio: " + post.getCategory().getName());
		
		var persistedPost = given().spec(specification)
        	    .config(
        	        RestAssuredConfig
        	            .config()
        	            .encoderConfig(EncoderConfig.encoderConfig()
        	                .encodeContentTypeAs(
        	                    TestConfigs.CONTENT_TYPE_YML,
        	                    ContentType.TEXT)))
        	    .contentType(TestConfigs.CONTENT_TYPE_YML)
        	    .accept(TestConfigs.CONTENT_TYPE_YML)
        	    	.body(post, objectMapper)
        	    	.when()
        	    	.post()
        	    .then()
        	    	.log().all()
        	        .statusCode(200)
        	        	.extract()
        	        	.body()
        	        		.as(PostVO.class, objectMapper);

		System.out.println("Request Body: " + persistedPost);
		post = persistedPost;
		
		assertNotNull(persistedPost);
		assertNotNull(persistedPost.getId());
		assertNotNull(persistedPost.getTitle());
		assertNotNull(persistedPost.getContent());
		assertNotNull(persistedPost.getSlug());
		assertTrue(persistedPost.getStatus());
		assertNotNull(persistedPost.getCreatedAt());
		assertNotNull(persistedPost.getUpdatedAt());
		assertNotNull(persistedPost.getPublishedAt());		
		assertNotNull(persistedPost.getCategory());
		assertNotNull(persistedPost.getUser());
		//assertNull(persistedPost.getImageDesktop());
		//assertNull(persistedPost.getImageMobile());
		
		assertTrue(persistedPost.getId() > 0);
		assertEquals("Content Negotiation using Spring MVC", persistedPost.getTitle());
		assertEquals("In this post I want to discuss how to configure and use content", persistedPost.getContent());
		assertEquals("content-negotiation-using-spring-mvc", persistedPost.getSlug());
		assertEquals(true, persistedPost.getStatus());
		//assertTrue(persistedPost.getCreatedAt()
		//		.truncatedTo(ChronoUnit.SECONDS)
		//		.isEqual(now
		//				.truncatedTo(ChronoUnit.SECONDS)));
		
		//assertTrue(persistedPost.getPublishedAt()
		//		.truncatedTo(ChronoUnit.SECONDS)
		//		.isEqual(now
		//				.truncatedTo(ChronoUnit.SECONDS)));
		assertEquals(null, persistedPost.getImageDesktop().getId()); 
		assertEquals(null, persistedPost.getImageMobile().getId());
		assertEquals(1L, persistedPost.getCategory().getId());
		assertEquals(1L, persistedPost.getUser().getId());		
	}
	/*
	@Test
	@Order(2)
	public void testCreateWithWrongOrigin() throws JsonMappingException, JsonProcessingException {
		mockPost();
		
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
					.body(post, new YMLMapper())
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
		
		var persistedPost = given().spec(specification)
				.config(
						RestAssuredConfig
							.config()
							.encoderConfig(EncoderConfig.encoderConfig()
								.encodeContentTypeAs(
									TestConfigs.CONTENT_TYPE_YML,
									ContentType.TEXT)))
				.contentType(TestConfigs.CONTENT_TYPE_YML)
				.accept(TestConfigs.CONTENT_TYPE_YML)
					.body(post, objectMapper)
					.when()
					.post()
				.then()
					.statusCode(200)
						.extract()
						.body()
						.as(PostVO.class, objectMapper);
		
		//PostVO persistedPost = objectMapper.readValue(content, PostVO.class);
		
		post = persistedPost;
		
		assertNotNull(persistedPost);
		
		assertNotNull(persistedPost.getId());
		assertNotNull(persistedPost.getTitle());
		assertNotNull(persistedPost.getContent());
		assertNotNull(persistedPost.getSlug());
		assertTrue(persistedPost.getStatus()); 
		assertNotNull(persistedPost.getCreatedAt());
		assertNotNull(persistedPost.getUpdatedAt());
		assertNotNull(persistedPost.getPublishedAt());		
		assertNotNull(persistedPost.getCategory());
		assertNotNull(persistedPost.getUser());
		//assertNull(persistedPost.getImageDesktop());
		//assertNull(persistedPost.getImageMobile());			
		
	    assertEquals(post.getId(), persistedPost.getId());
		
		assertEquals("Content Negotiation using Spring MVC", persistedPost.getTitle());
		assertEquals("In this post I want to discuss how to configure and use content", persistedPost.getContent());
		
		assertEquals("content-negotiation-using-spring-mvc", persistedPost.getSlug());
		assertEquals(true, persistedPost.getStatus());
		
		assertTrue(persistedPost.getCreatedAt()
				.truncatedTo(ChronoUnit.SECONDS)
				.isEqual(now
						.truncatedTo(ChronoUnit.SECONDS)));
		
		assertTrue(persistedPost.getUpdatedAt()
				.truncatedTo(ChronoUnit.SECONDS)
				.isEqual(now
						.truncatedTo(ChronoUnit.SECONDS)));
		
		assertEquals(1L, persistedPost.getCategory().getId());
		assertEquals(1L, persistedPost.getUser().getId());				
	}
	
	@Test
	@Order(4)
	public void testFindById() throws JsonMappingException, JsonProcessingException {
		mockPost();
		
		var persistedPost = given().spec(specification)
				.config(
						RestAssuredConfig
							.config()
							.encoderConfig(EncoderConfig.encoderConfig()
								.encodeContentTypeAs(
									TestConfigs.CONTENT_TYPE_YML,
									ContentType.TEXT)))
				.contentType(TestConfigs.CONTENT_TYPE_YML)
				.accept(TestConfigs.CONTENT_TYPE_YML)
					.pathParam("id", post.getId())
					.when()
					.get("{id}")
				.then()
					.statusCode(200)
						.extract()
						.body()
						.as(PostVO.class, objectMapper);
		
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
		//assertNull(persistedPost.getImageDesktop());
		//assertNull(persistedPost.getImageMobile());
		
	    assertTrue(persistedPost.getId() > 0);
		
		assertEquals("Content Negotiation using Spring MVC", persistedPost.getTitle());
		assertEquals("In this post I want to discuss how to configure and use content", persistedPost.getContent());
				
		//assertNull(null, persistedPost.getImageDesktop());
		//assertNull(null, persistedPost.getImageMobile());
		assertEquals(1L, persistedPost.getCategory().getId());
		assertEquals(1L, persistedPost.getUser().getId());
	} 
	
	@Test
	@Order(5)
	public void testFindByIdWithWrongOrigin() throws JsonMappingException, JsonProcessingException {
		mockPost();	
		
		var persistedPost = given().spec(specification)
				.config(
						RestAssuredConfig
							.config()
							.encoderConfig(EncoderConfig.encoderConfig()
								.encodeContentTypeAs(
									TestConfigs.CONTENT_TYPE_YML,
									ContentType.TEXT)))
                .contentType(TestConfigs.CONTENT_TYPE_YML)
					.header(TestConfigs.HEADER_PARAM_ORIGIN, TestConfigs.ORIGIN_ICLASS)
					.pathParam("id", post.getId())
					.when()
					.get("{id}")
				.then()
					.statusCode(403)
						.extract()
						.body()
							.asString();
		
	
		assertNotNull(persistedPost);
		assertEquals("Invalid CORS request", persistedPost);
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
						.as(PostVO[].class, objectMapper);
		
		List<PostVO> p = Arrays.asList(content);
		
		PostVO foundPostOne = p.get(0);
		
		assertNotNull(foundPostOne.getId());
		assertNotNull(foundPostOne.getTitle());
		assertNotNull(foundPostOne.getContent());
		assertNotNull(foundPostOne.getSlug());
		assertTrue(foundPostOne.getStatus()); 
		assertNotNull(foundPostOne.getCreatedAt());
		assertNotNull(foundPostOne.getUpdatedAt());
		assertNotNull(foundPostOne.getPublishedAt());		
		assertNotNull(foundPostOne.getCategory());
		assertNotNull(foundPostOne.getUser());
		//assertNull(foundPostOne.getImageDesktop());
	    //assertNull(foundPostOne.getImageMobile());
		
		assertEquals(1, foundPostOne.getId());
		
		assertEquals("The Future of AI", foundPostOne.getTitle());
		assertEquals("Artificial Intelligence is growing rapidly...", foundPostOne.getContent());
				
		//assertEquals(null, foundPostOne.getImageDesktop()); 
		//assertEquals(null, foundPostOne.getImageMobile());
		assertEquals(1L, foundPostOne.getCategory().getId());
		assertEquals(1L, foundPostOne.getUser().getId());
		
		PostVO foundPostSix = p.get(3);
		
		assertNotNull(foundPostSix.getId());
		assertNotNull(foundPostSix.getTitle());
		assertNotNull(foundPostSix.getContent());
		assertNotNull(foundPostSix.getSlug());
		assertTrue(foundPostSix.getStatus()); 
		assertNotNull(foundPostSix.getCreatedAt());
		assertNotNull(foundPostSix.getUpdatedAt());
		assertNotNull(foundPostSix.getPublishedAt());		
		assertNotNull(foundPostSix.getCategory());
		assertNotNull(foundPostSix.getUser());
		//assertNull(foundPostSix.getImageDesktop());
	    //assertNull(foundPostSix.getImageMobile());
		
		assertEquals(4, foundPostSix.getId());
		
		assertEquals("Top Tech Trends of 2024", foundPostSix.getTitle());
		assertEquals("Here are the top tech trends to watch out for in 2024...", foundPostSix.getContent());
		
		//assertEquals(null, foundPostOne.getImageDesktop()); 
		//assertEquals(null, foundPostOne.getImageMobile());
		assertEquals(1L, foundPostSix.getCategory().getId());
		assertEquals(1L, foundPostSix.getUser().getId());
	}
	
	@Test
	@Order(7)
	public void testDisablePostById() throws JsonMappingException, JsonProcessingException {
			
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
		assertTrue(persistedPost.getStatus()); 
		assertNotNull(persistedPost.getCreatedAt());
		assertNotNull(persistedPost.getUpdatedAt());
		assertNotNull(persistedPost.getPublishedAt());		
		assertNotNull(persistedPost.getCategory());
		assertNotNull(persistedPost.getUser());
		//assertNull(persistedPost.getImageDesktop());
	   // assertNull(persistedPost.getImageMobile());
		
		assertEquals(post.getId(), persistedPost.getId());
		
		assertEquals("Content Negotiation using Spring MVC", persistedPost.getTitle());
		assertEquals("In this post I want to discuss how to configure and use content", persistedPost.getContent());
	}	
	
	@Test
	@Order(8)
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
			.pathParam("id", post.getId())
			.when()
			.delete("{id}")
		.then()
			.statusCode(204);
	}*/
	
	private void mockPost() {
		post.setId(10L);
		post.setTitle("Content Negotiation using Spring MVC");
	    post.setContent("In this post I want to discuss how to configure and use content");
	    post.setSlug("content-negotiation-using-spring-mvc");
	    post.setStatus(true);
	    post.setCreatedAt(now);
	    post.setUpdatedAt(now);
	    post.setPublishedAt(now);
	    
	    Category category = new Category();
	    category.setId(1L);
	    category.setName("This is a category");	 
	    category.setDescription("This is a description a category");
	    category.setCreatedAt(now);
	    
	    User user = new User();
	    user.setId(1L);
	    user.setFirstName("Alana");
	    user.setLastName("Barbosa");
	    user.setUserName("alana");
	    user.setPassword("admin123");
	    user.setComments(null);
	    user.setFile(null);
	    user.setBio("Software developer and tech enthusiast.");
	    user.setCreatedAt(LocalDateTime.parse("2024-12-19T13:09:03"));
	    user.setEnabled(true);
	    user.setAccountNonExpired(true);
	    user.setAccountNonLocked(true);
	    user.setCredentialsNonExpired(true);
	    
	    File imageDesktop = new File();
	    imageDesktop.setId(null);

	    File imageMobile = new File();
	    imageMobile.setId(null);
	    
	    Role role = new Role();
	    role.setId(1L);
	    role.setName("ADMIN");
	    role.setDescription("Description of a adm");
	    role.setCreatedAt(now);
	    
	    List<Role> roles = new ArrayList<>();
	    roles.add(role);
	    
	    user.setRoles(roles);

	    post.setUser(user);
	    System.out.println("Category antes de set: " + category.getName());
	    post.setCategory(category);
	    System.out.println("Category depois de set: " + post.getCategory().getName());
	    post.setImageDesktop(null);
	    post.setImageMobile(null);
	}	

}