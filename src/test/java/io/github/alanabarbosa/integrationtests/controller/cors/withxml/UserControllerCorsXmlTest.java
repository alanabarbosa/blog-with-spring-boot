package io.github.alanabarbosa.integrationtests.controller.cors.withxml;

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
import io.github.alanabarbosa.integrationtests.vo.TokenVO;
import io.github.alanabarbosa.integrationtests.vo.UserVO;
import io.github.alanabarbosa.integrationtests.vo.pagedmodels.PagedModelUser;
import io.github.alanabarbosa.model.Role;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.specification.RequestSpecification;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestMethodOrder(OrderAnnotation.class)
public class UserControllerCorsXmlTest extends AbstractIntegrationTest{
	
	private static RequestSpecification specification;
	private static XmlMapper objectMapper;
	private static UserVO user;

	LocalDateTime now = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
	
	@SuppressWarnings("deprecation")
	@BeforeAll
    public static void setup() {
        objectMapper = new XmlMapper();
	    objectMapper.registerModule(new JavaTimeModule());
	    objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
	    objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
	    objectMapper.enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT);
	    objectMapper.setDefaultUseWrapper(false);
	    
	    objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSSSSS"));

        user = new UserVO();
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
		
	    String requestXml = objectMapper.writeValueAsString(user);
	    System.out.println("XML enviado: " + requestXml);	    
		
		var content = given().spec(specification)
				.contentType(TestConfigs.CONTENT_TYPE_XML)
				.accept(TestConfigs.CONTENT_TYPE_XML)
					.body(requestXml)
					.log().all()
					.when()
					.post()
				.then()
				.log().all() 
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
		//assertNotNull(persistedUser.getPassword());
		assertNotNull(persistedUser.getAccountNonExpired());
		assertNotNull(persistedUser.getAccountNonLocked());
		assertNotNull(persistedUser.getCredentialsNonExpired());
		assertNotNull(persistedUser.getEnabled());
		assertNotNull(persistedUser.getCreatedAt());
		
		assertTrue(persistedUser.getKey() > 0);
		
		assertEquals("Son", persistedUser.getFirstName());
		assertEquals("Goku", persistedUser.getLastName());
		assertEquals("songoku", persistedUser.getUserName());
		assertEquals("This is a biograph", persistedUser.getBio());
		
		assertEquals(true, persistedUser.getAccountNonExpired());
		assertEquals(true, persistedUser.getAccountNonLocked());
		assertEquals(true, persistedUser.getCredentialsNonExpired());
		assertEquals(true, persistedUser.getEnabled());
	}
	
	@Test
	@Order(2)
	public void testCreateWithWrongOrigin() throws JsonMappingException, JsonProcessingException {
		mockUser();		
		
		var content = given().spec(specification)
				.contentType(TestConfigs.CONTENT_TYPE_XML)
				.accept(TestConfigs.CONTENT_TYPE_XML)
					.header(TestConfigs.HEADER_PARAM_ORIGIN, TestConfigs.ORIGIN_ICLASS)
					.body(user)
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
		mockUser();
			
		var content = given().spec(specification)
				.contentType(TestConfigs.CONTENT_TYPE_XML)
				.accept(TestConfigs.CONTENT_TYPE_XML)
					.header(TestConfigs.HEADER_PARAM_ORIGIN, TestConfigs.ORIGIN_ALANA)
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
		assertNotNull(persistedUser.getEnabled());
		assertNotNull(persistedUser.getCreatedAt());
		
		assertTrue(persistedUser.getKey() > 0);
		
		assertEquals("Son", persistedUser.getFirstName());
		assertEquals("Goku", persistedUser.getLastName());
		assertEquals("songoku", persistedUser.getUserName());
		assertEquals("This is a biograph", persistedUser.getBio());
		
		assertEquals(true, persistedUser.getEnabled());
		//assertEquals(1L, persistedUser.getRoles().getKey());
	} 
	
	@Test
	@Order(4)
	public void testFindByIdWithWrongOrigin() throws JsonMappingException, JsonProcessingException {
		mockUser();
		
		var content = given().spec(specification)
				.contentType(TestConfigs.CONTENT_TYPE_XML)
				.accept(TestConfigs.CONTENT_TYPE_XML)
					.header(TestConfigs.HEADER_PARAM_ORIGIN, TestConfigs.ORIGIN_ICLASS)
					.pathParam("id", user.getKey())
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
	
	private void mockUser() {
	    user.setFirstName("Son");
	    user.setLastName("Goku");
	    user.setUserName("songoku");
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