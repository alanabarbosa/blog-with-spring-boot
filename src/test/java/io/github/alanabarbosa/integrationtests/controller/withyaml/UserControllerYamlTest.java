package io.github.alanabarbosa.integrationtests.controller.withyaml;

import static io.restassured.RestAssured.given;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
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
import io.github.alanabarbosa.integrationtests.controller.withyaml.mapper.YMLMapper;
import io.github.alanabarbosa.integrationtests.testcontainers.AbstractIntegrationTest;
import io.github.alanabarbosa.integrationtests.vo.AccountCredentialsVO;
import io.github.alanabarbosa.integrationtests.vo.CommentVO;
import io.github.alanabarbosa.integrationtests.vo.TokenVO;
import io.github.alanabarbosa.integrationtests.vo.UserVO;
import io.github.alanabarbosa.integrationtests.vo.pagedmodels.PagedModelComment;
import io.github.alanabarbosa.integrationtests.vo.pagedmodels.PagedModelUser;
import io.github.alanabarbosa.model.Role;
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
public class UserControllerYamlTest extends AbstractIntegrationTest{
	
	private static RequestSpecification specification;
	private static YMLMapper objectMapper;
	private static UserVO user;

	LocalDateTime now = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
	
	@BeforeAll
    public static void setup() {
        objectMapper = new YMLMapper();
        user = new UserVO();
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
		
		//objectMapper.registerModule(new JavaTimeModule());
	    //objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
		
        user = given()
	        .config(
	                RestAssuredConfig
	                    .config()
	                    .encoderConfig(EncoderConfig.encoderConfig()
	                            .encodeContentTypeAs(TestConfigs.CONTENT_TYPE_YML, ContentType.TEXT)))
	            .spec(specification)
	        .contentType(TestConfigs.CONTENT_TYPE_YML)
			.accept(TestConfigs.CONTENT_TYPE_YML)
	            .body(user, objectMapper)
	            .when()
	            .post()
	        .then()
	            .statusCode(200)
	                .extract()
	                .body()
	                    .as(UserVO.class, objectMapper);
		
		
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
		assertNotNull(user.getRoles());
		
		assertTrue(user.getKey() > 0);
		assertEquals("Son", user.getFirstName());
		assertEquals("Goku", user.getLastName());
		assertEquals("songoku", user.getUserName());
		assertEquals("This is a biograph", user.getBio());
		
		assertNull(user.getFile());
		assertEquals(true, user.getAccountNonExpired());
		assertEquals(true, user.getAccountNonLocked());
		assertEquals(true, user.getCredentialsNonExpired());
		assertEquals(true, user.getEnabled());
	}
	
	@Test
	@Order(2)
	public void testCreateWithWrongOrigin() throws JsonMappingException, JsonProcessingException {
		mockUser();		
		
		var persistedUser = given().spec(specification)
				.config(
						RestAssuredConfig
							.config()
							.encoderConfig(EncoderConfig.encoderConfig()
								.encodeContentTypeAs(
									TestConfigs.CONTENT_TYPE_YML,
									ContentType.TEXT)))
                .contentType(TestConfigs.CONTENT_TYPE_YML)
					.header(TestConfigs.HEADER_PARAM_ORIGIN, TestConfigs.ORIGIN_ICLASS)
					.pathParam("id", user.getKey())
					.when()
					.get("{id}")
				.then()
					.statusCode(403)
						.extract()
						.body()
							.asString();
		
	
		assertNotNull(persistedUser);
		assertEquals("Invalid CORS request", persistedUser);	
	}
	
	@Test
	@Order(3)
	public void testFindById() throws JsonMappingException, JsonProcessingException {
		 var foundUser = given()
                .config(
                    RestAssuredConfig
                        .config()
                        .encoderConfig(EncoderConfig.encoderConfig()
                                .encodeContentTypeAs(TestConfigs.CONTENT_TYPE_YML, ContentType.TEXT)))
                .spec(specification)
            .contentType(TestConfigs.CONTENT_TYPE_YML)
			.accept(TestConfigs.CONTENT_TYPE_YML)
                .pathParam("id", user.getKey())
                .when()
                .get("{id}")
            .then()
                .statusCode(200)
                    .extract()
                    .body()
                    .as(UserVO.class, objectMapper);
		
		
		assertNotNull(foundUser.getKey());
		assertNotNull(foundUser.getFirstName());
		assertNotNull(foundUser.getLastName());
		assertNotNull(foundUser.getUserName());
		assertNotNull(foundUser.getBio());		
		assertNotNull(foundUser.getEnabled());
		assertNotNull(foundUser.getCreatedAt());
		
		assertTrue(foundUser.getKey() > 0);
		
		assertEquals("Son", foundUser.getFirstName());
		assertEquals("Goku", foundUser.getLastName());
		assertEquals("songoku", foundUser.getUserName());
		assertEquals("This is a biograph", foundUser.getBio());
		
		assertEquals(true, foundUser.getEnabled());
	} 
	
	@Test
	@Order(4)
	public void testFindByIdWithWrongOrigin() throws JsonMappingException, JsonProcessingException {
		mockUser();
		
		var persistedUser = given().spec(specification)
				.config(
						RestAssuredConfig
							.config()
							.encoderConfig(EncoderConfig.encoderConfig()
								.encodeContentTypeAs(
									TestConfigs.CONTENT_TYPE_YML,
									ContentType.TEXT)))
                .contentType(TestConfigs.CONTENT_TYPE_YML)
					.header(TestConfigs.HEADER_PARAM_ORIGIN, TestConfigs.ORIGIN_ICLASS)
					.pathParam("id", user.getKey())
					.when()
					.get("{id}")
				.then()
					.statusCode(403)
						.extract()
						.body()
							.asString();
		
	
		assertNotNull(persistedUser);
		assertEquals("Invalid CORS request", persistedUser);
	}
	
	@Test
	@Order(7)
	public void testFindAll() throws JsonMappingException, JsonProcessingException {
		
		var wrapper = given()
                .config(
                    RestAssuredConfig
                        .config()
                        .encoderConfig(EncoderConfig.encoderConfig()
                                .encodeContentTypeAs(TestConfigs.CONTENT_TYPE_YML, ContentType.TEXT)))
                .spec(specification)
            .contentType(TestConfigs.CONTENT_TYPE_YML)
			.accept(TestConfigs.CONTENT_TYPE_YML)
			.queryParams("page", 0 , "limit", 12, "direction", "asc")
					.when()
					.get()
				.then()
					.statusCode(200)
						.extract()
						.body()
							.as(PagedModelUser.class, objectMapper); 
		
		var u = wrapper.getContent();
		
		UserVO foundUserOne = u.get(0);
		
		assertNotNull(foundUserOne.getKey());
		assertNotNull(foundUserOne.getFirstName());		
		assertEquals(238, foundUserOne.getKey());		
		assertEquals("Addia", foundUserOne.getFirstName());
		
		UserVO foundUserTwo = u.get(2);
		
		assertNotNull(foundUserTwo.getKey());
		assertNotNull(foundUserTwo.getFirstName());		
		assertEquals(224, foundUserTwo.getKey());		
		assertEquals("Adelle", foundUserTwo.getFirstName());
	}
	
	@Test
	@Order(8)
	public void testFindAllWithoutToken() throws JsonMappingException, JsonProcessingException {
		
		RequestSpecification specificationWithoutToken = new RequestSpecBuilder()
			.setBasePath("/api/user/v1")
			.setPort(TestConfigs.SERVER_PORT)
				.addFilter(new RequestLoggingFilter(LogDetail.ALL))
				.addFilter(new ResponseLoggingFilter(LogDetail.ALL))
			.build();
		
		given().spec(specificationWithoutToken)
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
				.put()
			.then()
				.statusCode(403);
	}
	

	@Test
	@Order(9)
	public void testHATEOAS() throws JsonMappingException, JsonProcessingException {
		
		var unthreatedContent = given().spec(specification)
				.config(
						RestAssuredConfig
							.config()
							.encoderConfig(EncoderConfig.encoderConfig()
								.encodeContentTypeAs(
									TestConfigs.CONTENT_TYPE_YML,
									ContentType.TEXT)))
				.contentType(TestConfigs.CONTENT_TYPE_YML)
				.accept(TestConfigs.CONTENT_TYPE_YML)
				.queryParams("page", 2, "size", 12, "direction", "asc")
				.when()
					.get()
				.then()
					.statusCode(200)
						.extract()
						.body()
							.asString();
		
		var content = unthreatedContent.replace("\n", "").replace("\r", "");
		
		assertTrue(content.contains("rel: \"user-details\"    href: \"http://localhost:8888/api/user/v1/111\""));
		assertTrue(content.contains("rel: \"user-details\"    href: \"http://localhost:8888/api/user/v1/113\""));
		assertTrue(content.contains("rel: \"user-details\"    href: \"http://localhost:8888/api/user/v1/101\""));
		
		assertTrue(content.contains("rel: \"first\"  href: \"http://localhost:8888/api/user/v1?direction=asc&page=0&size=12&sort=firstName,asc\""));
		assertTrue(content.contains("rel: \"prev\"  href: \"http://localhost:8888/api/user/v1?direction=asc&page=1&size=12&sort=firstName,asc\""));
		assertTrue(content.contains("rel: \"self\"  href: \"http://localhost:8888/api/user/v1?page=2&size=12&direction=asc\""));
		assertTrue(content.contains("rel: \"next\"  href: \"http://localhost:8888/api/user/v1?direction=asc&page=3&size=12&sort=firstName,asc\""));
		assertTrue(content.contains("rel: \"last\"  href: \"http://localhost:8888/api/user/v1?direction=asc&page=25&size=12&sort=firstName,asc\""));
		
		assertTrue(content.contains("page:  size: 12  totalElements: 304  totalPages: 26  number: 2"));
	}
	
	@Test
	@Order(10)
	public void testDisableUserById() throws JsonMappingException, JsonProcessingException {
			
		var persistedUser = given().spec(specification)
				.config(
					RestAssuredConfig
						.config()
						.encoderConfig(EncoderConfig.encoderConfig()
							.encodeContentTypeAs(
								TestConfigs.CONTENT_TYPE_YML,
								ContentType.TEXT)))
				.contentType(TestConfigs.CONTENT_TYPE_YML)
				.accept(TestConfigs.CONTENT_TYPE_YML)
					.pathParam("id", user.getKey())
					.when()
					.patch("{id}")
				.then()
					.statusCode(200)
						.extract()
							.body()
								.as(UserVO.class, objectMapper);
		
		user = persistedUser;
		
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
		assertEquals("songoku", persistedUser.getUserName());
		assertEquals("This is a biograph", persistedUser.getBio());
		
		//assertEquals(true, persistedUser.getAccountNonExpired());
		//assertEquals(true, persistedUser.getAccountNonLocked());
		//assertEquals(true, persistedUser.getCredentialsNonExpired());
		assertEquals(true, persistedUser.getEnabled());
	}		
	
	@Test
	@Order(11)
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
			.accept(TestConfigs.CONTENT_TYPE_YML)
				.pathParam("id", user.getKey())
				.when()
				.delete("{id}")
			.then()
				.statusCode(204);
	}

	private void mockUser() {
	    user.setFirstName("Son");
	    user.setLastName("Goku");
	    user.setUserName("songoku");
	    user.setPassword("password123");
	    user.setBio("This is a biograph");
	    user.setAccountNonExpired(true);
	    user.setAccountNonLocked(true);
	    user.setCredentialsNonExpired(true);
	    user.setEnabled(true);
	    
	    List<Role> roles = new ArrayList<>();
	    Role role = new Role();
	    role.setId(1L);
	    role.setDescription("Admin");
	    roles.add(role);
	    user.setRoles(roles);
	    
	    user.setFile(null);

	    System.out.println("User [firstName=" + user.getFirstName() +
	        ", lastName=" + user.getLastName() +
	        ", userName=" + user.getUserName() +
	        ", password=" + user.getPassword() +
	        ", accountNonExpired=" + user.getAccountNonExpired() +
	        ", accountNonLocked=" + user.getAccountNonLocked() +
	        ", credentialsNonExpired=" + user.getCredentialsNonExpired() +
	        ", bio=" + user.getBio() +
	        ", enabled=" + user.getEnabled() +
	        ", roles=" + user.getRoles() + "]");
	}
}