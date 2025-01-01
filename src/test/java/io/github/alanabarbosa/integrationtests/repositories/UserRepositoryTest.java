package io.github.alanabarbosa.integrationtests.repositories;

import static org.junit.Assert.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

import io.github.alanabarbosa.integrationtests.testcontainers.AbstractIntegrationTest;
import io.github.alanabarbosa.model.User;
import io.github.alanabarbosa.repositories.UserRepository;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestMethodOrder(OrderAnnotation.class)
public class UserRepositoryTest extends AbstractIntegrationTest {
	@Autowired
	public UserRepository repository;
	
	private static User user;
	
	@BeforeAll
	public static void setup() {
		user = new User();
	}
	
	@Test
	@Order(1)
	public void testFindByName() throws JsonMappingException, JsonProcessingException {
		
		Pageable pageable = PageRequest.of(0, 6, Sort.by(Direction.ASC, "firstName"));
		user = repository.findUsersByName("alana", pageable).getContent().get(0);
		
		assertNotNull(user.getId());
		assertNotNull(user.getFirstName());

		assertTrue(user.getEnabled());		
		assertEquals(1, user.getId());		
		assertEquals("Alana", user.getFirstName());
	}
	
	@Test
	@Order(2)
	public void testDisablePost() throws JsonMappingException, JsonProcessingException {
		
		repository.disableUser(user.getId());
		
		Pageable pageable = PageRequest.of(0, 6, Sort.by(Direction.ASC, "firstName"));
		user = repository.findUsersByName("alana", pageable).getContent().get(0);
		
		assertNotNull(user.getId());
		assertNotNull(user.getFirstName());

		assertFalse(user.getEnabled());		
		assertEquals(1, user.getId());		
		assertEquals("Alana", user.getFirstName());
	}
}
