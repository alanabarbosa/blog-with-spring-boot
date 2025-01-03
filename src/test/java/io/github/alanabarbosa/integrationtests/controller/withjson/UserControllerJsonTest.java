package io.github.alanabarbosa.integrationtests.controller.withjson;

import static io.restassured.RestAssured.given;
import static org.junit.Assert.assertNotNull;
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
import io.github.alanabarbosa.integrationtests.vo.TokenVO;
import io.github.alanabarbosa.integrationtests.vo.UserVO;
import io.github.alanabarbosa.integrationtests.vo.wrappers.WrapperUserVO;
import io.github.alanabarbosa.model.Role;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.specification.RequestSpecification;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestMethodOrder(OrderAnnotation.class)
public class UserControllerJsonTest extends AbstractIntegrationTest{
	
	private static RequestSpecification specification ;
	private static ObjectMapper objectMapper;
	private static UserVO user;

	LocalDateTime now = LocalDateTime.now();
	
	@BeforeAll
	public static void setup() {
		objectMapper = new ObjectMapper();
		objectMapper.registerModule(new JavaTimeModule());
		objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
		objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
		objectMapper.enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT);

	    user = new UserVO();
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
				.setBasePath("/api/user/v1")
				.setPort(TestConfigs.SERVER_PORT)
					.addFilter(new RequestLoggingFilter(LogDetail.ALL))
					.addFilter(new ResponseLoggingFilter(LogDetail.ALL))
				.build();
	}

	@Test
	@Order(1)
	public void testCreate() throws JsonMappingException, JsonProcessingException {
		mockUser();
		
		var content = given().spec(specification)
				.contentType(TestConfigs.CONTENT_TYPE_JSON)
					.body(user)
					.when()
					.post()
				.then()
					.statusCode(200)
						.extract()
						.body()
							.asString();
		
		user = objectMapper.readValue(content, UserVO.class);
		
		assertNotNull(user.getKey());
		assertNotNull(user.getFirstName());
		assertNotNull(user.getLastName());
		assertNotNull(user.getUserName());
		assertNotNull(user.getBio());
		assertNotNull(user.getPassword());
		assertNotNull(user.getAccountNonExpired());
		assertNotNull(user.getAccountNonLocked());
		assertNotNull(user.getCredentialsNonExpired());
		assertNotNull(user.getEnabled());
		assertNotNull(user.getCreatedAt());
		assertNotNull(user.getRoles());
		
		assertTrue(user.getKey() > 0);
		
		assertEquals("Son", user.getFirstName());
		assertEquals("Goku", user.getLastName());
		assertEquals("songokuuu", user.getUserName());
		assertEquals("This is a biograph", user.getBio());
		
		assertEquals(true, user.getAccountNonExpired());
		assertEquals(true, user.getAccountNonLocked());
		assertEquals(true, user.getCredentialsNonExpired());
		assertEquals(true, user.getEnabled());

		//assertEquals(4L, persistedUser.getRoles().get(0));
		//assertEquals(1L, persistedUser.getUser().getKey());
	}

	@Test
	@Order(2)
	public void testCreateAllWithoutToken() throws JsonMappingException, JsonProcessingException {
		
		RequestSpecification specificationWithoutToken = new RequestSpecBuilder()
				.setBasePath("/api/user/v1")
				.setPort(TestConfigs.SERVER_PORT)
					.addFilter(new RequestLoggingFilter(LogDetail.ALL))
					.addFilter(new ResponseLoggingFilter(LogDetail.ALL))
				.build();
			
			given().spec(specificationWithoutToken)
				.contentType(TestConfigs.CONTENT_TYPE_JSON)
					.when()
					.put()
				.then()
					.statusCode(403);
	}
	
	@Test
	@Order(3)
	public void testUpdate() throws JsonMappingException, JsonProcessingException {
		user.setFirstName("Son");
		
	    ObjectMapper objectMapper = new ObjectMapper();
	    objectMapper.registerModule(new JavaTimeModule());
	    objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
		
		var content = given().spec(specification)
				.contentType(TestConfigs.CONTENT_TYPE_JSON)
					.header(TestConfigs.HEADER_PARAM_ORIGIN, TestConfigs.ORIGIN_ALANA)
					.body(user)
					.when()
					.post()
				.then()
					.statusCode(200)
						.extract()
						.body()
							.asString();
		
		
		UserVO persistedUser = objectMapper.readValue(content, UserVO.class);
		
		user = persistedUser;
		
		assertNotNull(persistedUser);
		
		assertNotNull(persistedUser.getKey());
		assertNotNull(persistedUser.getFirstName());
		assertNotNull(persistedUser.getLastName());
		assertNotNull(persistedUser.getUserName());
		assertNotNull(persistedUser.getBio());
		assertNotNull(persistedUser.getPassword());
		assertNotNull(persistedUser.getAccountNonExpired());
		assertNotNull(persistedUser.getAccountNonLocked());
		assertNotNull(persistedUser.getCredentialsNonExpired());
		assertNotNull(persistedUser.getEnabled());
		assertNotNull(persistedUser.getCreatedAt());
		assertNotNull(persistedUser.getRoles());
		
		assertEquals(user.getKey(), persistedUser.getKey());
		
		assertEquals("Son", persistedUser.getFirstName());
		assertEquals("Goku", persistedUser.getLastName());
		assertEquals("songokuuu", persistedUser.getUserName());
		assertEquals("This is a biograph", persistedUser.getBio());
		
		assertEquals(true, persistedUser.getAccountNonExpired());
		assertEquals(true, persistedUser.getAccountNonLocked());
		assertEquals(true, persistedUser.getCredentialsNonExpired());
		assertEquals(true, persistedUser.getEnabled());

		//assertEquals(4L, persistedUser.getRoles().get(0));
		//assertEquals(1L, persistedUser.getUser().getKey());
	}
	
	@Test
	@Order(4)
	public void testFindById() throws JsonMappingException, JsonProcessingException {
		mockUser();
			
		var content = given().spec(specification)
				.contentType(TestConfigs.CONTENT_TYPE_JSON)
				.accept(TestConfigs.CONTENT_TYPE_JSON)
						.pathParam("id", user.getKey())
						.when()
						.get("{id}")
					.then()
						.statusCode(200)
							.extract()
							.body()
								.asString();
		
		UserVO persistedUser = objectMapper.readValue(content, UserVO.class);
		user = persistedUser;
		
		System.out.println(user);
		
		assertNotNull(persistedUser);
		
		assertNotNull(persistedUser.getKey());
		assertNotNull(persistedUser.getFirstName());
		assertNotNull(persistedUser.getLastName());
		assertNotNull(persistedUser.getUserName());
		assertNotNull(persistedUser.getBio());		
		//assertNotNull(persistedUser.getPassword());
		//assertNotNull(persistedUser.getAccountNonExpired());
		//assertNotNull(persistedUser.getAccountNonLocked());
		//assertNotNull(persistedUser.getCredentialsNonExpired());
		assertNotNull(persistedUser.getEnabled());
		assertNotNull(persistedUser.getCreatedAt());
		
		assertTrue(persistedUser.getKey() > 0);
		
		assertEquals("Son", persistedUser.getFirstName());
		assertEquals("Goku", persistedUser.getLastName());
		assertEquals("songokuuu", persistedUser.getUserName());
		assertEquals("This is a biograph", persistedUser.getBio());
		
		//assertEquals(true, persistedUser.getAccountNonExpired());
		//assertEquals(true, persistedUser.getAccountNonLocked());
		//assertEquals(true, persistedUser.getCredentialsNonExpired());
		assertEquals(true, persistedUser.getEnabled());
	} 
	
	@Test
	@Order(5)
	public void testFindAll() throws JsonMappingException, JsonProcessingException {
		
		var content = given().spec(specification)
				.contentType(TestConfigs.CONTENT_TYPE_JSON)
				.queryParams("page", 0, "size", 10, "direction", "asc")
				.accept(TestConfigs.CONTENT_TYPE_JSON)
					.when()
					.get()
				.then()
					.statusCode(200)
						.extract()
						.body()
							.asString();
		
		WrapperUserVO wrapper = objectMapper
				.readValue(content, WrapperUserVO.class);
		
		var user = wrapper.getEmbedded().getUsers();
		
		UserVO founUserOne = user.get(0);
		
		assertNotNull(founUserOne.getKey());
		assertNotNull(founUserOne.getFirstName());		
		assertEquals(238, founUserOne.getKey());		
		assertEquals("Addia", founUserOne.getFirstName());
		
		UserVO foundUserTwo = user.get(2);
		
		assertNotNull(foundUserTwo.getKey());
		assertNotNull(foundUserTwo.getFirstName());		
		assertEquals(224, foundUserTwo.getKey());		
		assertEquals("Adelle", foundUserTwo.getFirstName());
	}
	
	@Test
	@Order(6)
	public void testHATEOAS() throws JsonMappingException, JsonProcessingException {
		
		var content = given().spec(specification)
				.contentType(TestConfigs.CONTENT_TYPE_JSON)
				.accept(TestConfigs.CONTENT_TYPE_JSON)
				.queryParams("page", 2, "size", 12, "direction", "asc")
					.when()
					.get()
				.then()
					.statusCode(200)
						.extract()
						.body()
							.asString();
		
		assertTrue(content.contains("\"_links\":{\"user-details\":{\"href\":\"http://localhost:8888/api/user/v1/111\"}}}"));
		assertTrue(content.contains("\"_links\":{\"user-details\":{\"href\":\"http://localhost:8888/api/user/v1/113\"}}}"));
		assertTrue(content.contains("\"_links\":{\"user-details\":{\"href\":\"http://localhost:8888/api/user/v1/101\"}}}"));		
		
		assertTrue(content.contains("{\"first\":{\"href\":\"http://localhost:8888/api/user/v1?direction=asc&page=0&size=12&sort=firstName,asc\"}"));
		assertTrue(content.contains("\"prev\":{\"href\":\"http://localhost:8888/api/user/v1?direction=asc&page=1&size=12&sort=firstName,asc\"}"));
		assertTrue(content.contains("\"self\":{\"href\":\"http://localhost:8888/api/user/v1?page=2&size=12&direction=asc\"}"));
		assertTrue(content.contains("\"next\":{\"href\":\"http://localhost:8888/api/user/v1?direction=asc&page=3&size=12&sort=firstName,asc\"}"));
		assertTrue(content.contains("\"last\":{\"href\":\"http://localhost:8888/api/user/v1?direction=asc&page=25&size=12&sort=firstName,asc\"}}"));
		
		assertTrue(content.contains("\"page\":{\"size\":12,\"totalElements\":305,\"totalPages\":26,\"number\":2}}"));
	}
	
	@Test
	@Order(7)
	public void testFindByName() throws JsonMappingException, JsonProcessingException {
		
		var content = given().spec(specification)
				.contentType(TestConfigs.CONTENT_TYPE_JSON)
				.accept(TestConfigs.CONTENT_TYPE_JSON)
				.pathParam("firstName", "alana")
				.queryParams("page", 0, "size", 10, "direction", "asc")
					.when()
					.get("findUserByName/{firstName}")
				.then()
					.statusCode(200)
						.extract()
						.body()
							.asString();
		
		WrapperUserVO wrapper = objectMapper
				.readValue(content, WrapperUserVO.class);
		
		var user = wrapper.getEmbedded().getUsers();
		
		UserVO founUserOne = user.get(0);
		
		assertNotNull(founUserOne.getKey());
		assertNotNull(founUserOne.getFirstName());		
		assertEquals(1, founUserOne.getKey());		
		assertEquals("Alana", founUserOne.getFirstName());
	}
	
	@Test
	@Order(8)
	public void testDisableUserById() throws JsonMappingException, JsonProcessingException {
			
		var content = given().spec(specification)
				.contentType(TestConfigs.CONTENT_TYPE_JSON)
					.pathParam("id", user.getKey())
					.when()
					.patch("{id}")
				.then()
					.statusCode(200)
						.extract()
						.body()
							.asString();
		
		UserVO persistedUser = objectMapper.readValue(content, UserVO.class);
		
		System.out.println(user);
		
		assertNotNull(persistedUser);
		
		assertNotNull(persistedUser.getKey());
		assertNotNull(persistedUser.getFirstName());
		assertNotNull(persistedUser.getLastName());
		assertNotNull(persistedUser.getUserName());
		assertNotNull(persistedUser.getBio());		
		//assertNotNull(persistedUser.getPassword());
		//assertNotNull(persistedUser.getAccountNonExpired());
		//assertNotNull(persistedUser.getAccountNonLocked());
		//assertNotNull(persistedUser.getCredentialsNonExpired());
		assertNotNull(persistedUser.getEnabled());
		assertNotNull(persistedUser.getCreatedAt());
		
		assertTrue(persistedUser.getKey() > 0);
		
		assertEquals("Son", persistedUser.getFirstName());
		assertEquals("Goku", persistedUser.getLastName());
		assertEquals("songokuuu", persistedUser.getUserName());
		assertEquals("This is a biograph", persistedUser.getBio());
		
		//assertEquals(true, persistedUser.getAccountNonExpired());
		//assertEquals(true, persistedUser.getAccountNonLocked());
		//assertEquals(true, persistedUser.getCredentialsNonExpired());
		assertEquals(true, persistedUser.getEnabled());
	}	
	
	@Test
	@Order(9)
	public void testDelete() throws JsonMappingException, JsonProcessingException {

		given().spec(specification)
			.contentType(TestConfigs.CONTENT_TYPE_JSON)
				.pathParam("id", user.getKey())
				.when()
				.delete("{id}")
			.then()
				.statusCode(204);
	}

	private void mockUser() {
	    user.setFirstName("Son");
	    user.setLastName("Goku");
	    user.setUserName("songokuuu");
	    user.setPassword("admin123"); 
	    user.setBio("This is a biograph");
	    
	    user.setAccountNonExpired(true);
	    user.setAccountNonLocked(true);
	    user.setCredentialsNonExpired(true);
	    user.setEnabled(true);
	    
	    //user.setCreatedAt(now);
	    
	    Role role = new Role();
	    role.setId(1L);
	    
	    List<Role> roles = new ArrayList<>();
	    roles.add(role);
	    
	    user.setRoles(roles);
	    
	    System.out.println("User [firstName=" + user.getFirstName() +
	                       ", lastName=" + user.getLastName() +
	                       ", userName=" + user.getUserName() +
	                       ", password=" + user.getPassword() +
	                       ", accountNonExpired=" + user.getAccountNonExpired() +
	                       ", accountNonLocked=" + user.getAccountNonLocked() +
	                       ", credentialsNonExpired=" + user.getCredentialsNonExpired() +
	                       ", bio=" + user.getBio() +
	                       ", createdAt=" + user.getCreatedAt() +
	                       ", enabled=" + user.getEnabled() +
	                       ", roles=" + user.getRoles() + "]");
	}
}