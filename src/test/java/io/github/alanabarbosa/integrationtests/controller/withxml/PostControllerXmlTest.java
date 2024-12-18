package io.github.alanabarbosa.integrationtests.controller.withxml;

import static io.restassured.RestAssured.given;
import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

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
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
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
public class PostControllerXmlTest extends AbstractIntegrationTest{
	
	private static RequestSpecification specification;
	private static XmlMapper objectMapper;
	private static PostVO post;

	LocalDateTime now = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
	
	@BeforeAll
    public static void setup() {
        objectMapper = new XmlMapper();
	    objectMapper.registerModule(new JavaTimeModule());
	    objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
	    objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
	    objectMapper.enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT);
	    
	    objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSSSSS"));

        post = new PostVO();
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
		
	    objectMapper.registerModule(new JavaTimeModule());
	    objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);		
		
		var content = given().spec(specification)
				.contentType(TestConfigs.CONTENT_TYPE_XML)
				.accept(TestConfigs.CONTENT_TYPE_XML)
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
	
	/*@Test
	@Order(2)
	public void testCreateWithWrongOrigin() throws JsonMappingException, JsonProcessingException {
		mockPost();
		
		var content = given().spec(specification)
				.contentType(TestConfigs.CONTENT_TYPE_XML)
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
	}*/
	
	@Test
	@Order(3)
	public void testUpdate() throws JsonMappingException, JsonProcessingException {
		post.setTitle("Content Negotiation using Spring MVC");
		
		objectMapper.registerModule(new JavaTimeModule());
	    objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);	
	    
		try {
			var content = given().spec(specification)
					.contentType(TestConfigs.CONTENT_TYPE_XML)
					.accept(TestConfigs.CONTENT_TYPE_XML)
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
			
		} catch (Exception e) {
			System.out.println("Erro durante a requisição: " + e.getMessage());
			e.printStackTrace();
			fail("Teste falhou: " + e.getMessage());
		}
		
		System.out.println("Finalizando teste de update");
	}
	
	@Test
	@Order(4)
	public void testFindById() throws JsonMappingException, JsonProcessingException {
		mockPost();
		
		var content = given().spec(specification)
				.contentType(TestConfigs.CONTENT_TYPE_XML)
				.accept(TestConfigs.CONTENT_TYPE_XML)
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
		
		var content = given().spec(specification)
				.contentType(TestConfigs.CONTENT_TYPE_XML)
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
		
		List<PostVO> p = objectMapper.readValue(content, new TypeReference<List<PostVO>>() {});
		
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
	public void testDelete() throws JsonMappingException, JsonProcessingException {

		given().spec(specification)
			.contentType(TestConfigs.CONTENT_TYPE_XML)
				.pathParam("id", post.getId())
				.when()
				.delete("{id}")
			.then()
				.statusCode(204);
	}
	
	private void mockPost() {
	    post.setTitle("Content Negotiation using Spring MVC");
	    post.setContent("In this post I want to discuss how to configure and use content");
	    post.setSlug("content-negotiation-using-spring-mvc");
	    post.setStatus(true);
	    //post.setCreatedAt(now);
	    post.setUpdatedAt(now);
	    post.setPublishedAt(now);
	    
	    Category category = new Category();
	    category.setId(1L);
	    category.setCreatedAt(now);
	    category.setDescription("This is a description a category");
	    category.setName("This is a category");	 

	    User user = new User();
	    user.setId(1L);
	    user.setFirstName("Son");
	    user.setLastName("Goku");
	    user.setUserName("songoku");
	    user.setBio("This is a briograph");
	    user.setCreatedAt(now);
	    user.setEnabled(true);
	    
	    File imageDesktop = new File();
	    imageDesktop.setId(null);

	    File imageMobile = new File();
	    imageMobile.setId(null);
	    
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