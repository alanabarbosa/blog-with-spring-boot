package io.github.alanabarbosa.integrationtests.controller.withxml;

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
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import io.github.alanabarbosa.configs.TestConfigs;
import io.github.alanabarbosa.integrationtests.testcontainers.AbstractIntegrationTest;
import io.github.alanabarbosa.integrationtests.vo.AccountCredentialsVO;
import io.github.alanabarbosa.integrationtests.vo.PostVO;
import io.github.alanabarbosa.integrationtests.vo.TokenVO;
import io.github.alanabarbosa.integrationtests.vo.pagedmodels.PagedModelPost;
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

	@Test
	@Order(2)
	public void testCreateWithoutToken() throws JsonMappingException, JsonProcessingException {
		
		RequestSpecification specificationWithoutToken = new RequestSpecBuilder()
			.setBasePath("/api/post/v1")
			.setPort(TestConfigs.SERVER_PORT)
				.addFilter(new RequestLoggingFilter(LogDetail.ALL))
				.addFilter(new ResponseLoggingFilter(LogDetail.ALL))
			.build();
		
		given().spec(specificationWithoutToken)
			.contentType(TestConfigs.CONTENT_TYPE_XML)
			.accept(TestConfigs.CONTENT_TYPE_XML)
				.when()
				.post()
			.then()
				.statusCode(403);
	}
	
	@Test
	@Order(3)
	public void testUpdate() throws JsonMappingException, JsonProcessingException {
		post.setTitle("Content Negotiation using Spring MVC");
		
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
		assertNotNull(persistedPost.getCategory());
		assertNotNull(persistedPost.getUser());
		
	    assertEquals(post.getId(), persistedPost.getId());
		assertEquals("Content Negotiation using Spring MVC", persistedPost.getTitle());
		assertEquals("In this post I want to discuss how to configure and use content", persistedPost.getContent());
		assertEquals(1L, persistedPost.getCategory().getId());
		assertEquals(1L, persistedPost.getUser().getId());				
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
	public void findPostsByUserId() throws JsonMappingException, JsonProcessingException {
		mockPost();
		
		var content = given().spec(specification)
				.contentType(TestConfigs.CONTENT_TYPE_XML)
				.accept(TestConfigs.CONTENT_TYPE_XML)
				.pathParam("id", 1)
				.queryParams("page", 0, "size", 10, "direction", "asc")
					.when()
					.get("user/{id}")
				.then()
					.statusCode(200)
						.extract()
						.body()
							.asString();
		
		PagedModelPost wrapper = objectMapper
				.readValue(content, PagedModelPost.class);
		
		var post = wrapper.getContent();
		
		PostVO foundPostOne = post.get(0);
		
		assertNotNull(foundPostOne.getId());
		assertEquals(91, foundPostOne.getId());		
		assertEquals("Jane Austen Book Club, The", foundPostOne.getTitle());
	}
	
	@Test
	@Order(6)
	public void findPostsByCategoryId() throws JsonMappingException, JsonProcessingException {
		mockPost();
		
		var content = given().spec(specification)
				.contentType(TestConfigs.CONTENT_TYPE_XML)
				.accept(TestConfigs.CONTENT_TYPE_XML)
				.pathParam("id", 1)
				.queryParams("page", 0, "size", 10, "direction", "asc")
					.when()
					.get("category/{id}")
				.then()
					.statusCode(200)
						.extract()
						.body()
							.asString();
		
		PagedModelPost wrapper = objectMapper
				.readValue(content, PagedModelPost.class);
		
		var post = wrapper.getContent();
		
		PostVO foundPostOne = post.get(0);
		
		assertNotNull(foundPostOne.getId());
		assertEquals(3, foundPostOne.getId());		
		assertEquals("Sebastian", foundPostOne.getTitle());
	}
	
	@Test
	@Order(7)
	public void testHATEOAS() throws JsonMappingException, JsonProcessingException {
		
		var content = given().spec(specification)
				.contentType(TestConfigs.CONTENT_TYPE_XML)
				.accept(TestConfigs.CONTENT_TYPE_XML)
				.queryParams("page", 2, "size", 12, "direction", "asc")
					.when()
					.get()
				.then()
					.statusCode(200)
						.extract()
						.body()
							.asString();
		
		assertTrue(content.contains("<links><rel>post-details</rel><href>http://localhost:8888/api/post/v1/464</href></links>"));
		assertTrue(content.contains("<links><rel>post-details</rel><href>http://localhost:8888/api/post/v1/29</href></links>"));
		assertTrue(content.contains("<links><rel>post-details</rel><href>http://localhost:8888/api/post/v1/745</href></links>"));
		
		assertTrue(content.contains("<links><rel>first</rel><href>http://localhost:8888/api/post/v1?direction=asc&amp;page=0&amp;size=12&amp;sort=title,asc</href></links>"));
		assertTrue(content.contains("<links><rel>prev</rel><href>http://localhost:8888/api/post/v1?direction=asc&amp;page=1&amp;size=12&amp;sort=title,asc</href></links>"));
		assertTrue(content.contains("<links><rel>self</rel><href>http://localhost:8888/api/post/v1?page=2&amp;size=12&amp;direction=asc</href></links>"));
		assertTrue(content.contains("<links><rel>next</rel><href>http://localhost:8888/api/post/v1?direction=asc&amp;page=3&amp;size=12&amp;sort=title,asc</href></links>"));
		assertTrue(content.contains("<links><rel>last</rel><href>http://localhost:8888/api/post/v1?direction=asc&amp;page=83&amp;size=12&amp;sort=title,asc</href></links>"));
		assertTrue(content.contains("<page><size>12</size><totalElements>1001</totalElements><totalPages>84</totalPages><number>2</number></page>"));
	}
	
	@Test
	@Order(8)
	public void testFindAll() throws JsonMappingException, JsonProcessingException {
		
		var content = given().spec(specification)
				.contentType(TestConfigs.CONTENT_TYPE_XML)
				.queryParams("page", 0, "size", 10, "direction", "asc")
				.accept(TestConfigs.CONTENT_TYPE_XML)
					.when()
					.get()
				.then()
					.statusCode(200)
						.extract()
						.body()
							.asString();
		
		PagedModelPost wrapper = objectMapper
				.readValue(content, PagedModelPost.class);
		
		var post = wrapper.getContent();
		
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
				.contentType(TestConfigs.CONTENT_TYPE_XML)
				.accept(TestConfigs.CONTENT_TYPE_XML)
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
		assertNotNull(persistedPost.getCategory());
		assertNotNull(persistedPost.getUser());
		/*assertNull(persistedPost.getImageDesktop());
	    assertNull(persistedPost.getImageMobile());*/
		
		assertEquals(post.getId(), persistedPost.getId());
		
		assertEquals("Content Negotiation using Spring MVC", persistedPost.getTitle());
		assertEquals("In this post I want to discuss how to configure and use content", persistedPost.getContent());
	}	
	
	@Test
	@Order(10)
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