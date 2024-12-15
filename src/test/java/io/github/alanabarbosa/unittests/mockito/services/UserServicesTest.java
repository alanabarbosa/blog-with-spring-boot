package io.github.alanabarbosa.unittests.mockito.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import io.github.alanabarbosa.data.vo.v1.UserVO;
import io.github.alanabarbosa.exceptions.RequiredObjectIsNullException;
import io.github.alanabarbosa.model.Role;
import io.github.alanabarbosa.model.User;
import io.github.alanabarbosa.repositories.RoleRepository;
import io.github.alanabarbosa.repositories.UserRepository;
import io.github.alanabarbosa.services.UserServices;
import io.github.alanabarbosa.unittests.mapper.mocks.MockUser;

@TestInstance(Lifecycle.PER_CLASS)
@ExtendWith(MockitoExtension.class)
class UserServicesTest {

    MockUser input;

    @InjectMocks
    private UserServices service;

    @Mock
    UserRepository repository;
    
    @Mock
    private RoleRepository roleRepository;    

    @BeforeEach
    void setUpMocks() throws Exception {
        input = new MockUser();
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testFindById() throws Exception {
        User user = input.mockEntity();
        user.setId(1L);

        LocalDateTime now = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);

        user.setCreatedAt(now);

        when(repository.findById(1L)).thenReturn(Optional.of(user));
        var result = service.findById(1L);
        assertNotNull(result);
        assertNotNull(result.getKey());
        assertTrue(result.toString().contains("[</api/user/v1/1>;rel=\"self\"]"));
        assertEquals("This is a first name.0", result.getFirstName());
        assertEquals("This is a first name.0", result.getFirstName());
        assertEquals("This is a last name.0", result.getLastName());
        //assertEquals("This is a password.0", result.getPassword());
        assertEquals("This is a bio.0", result.getBio());
       // assertEquals(true, result.getAccountNonExpired());
       // assertEquals(true, result.getAccountNonLocked());
        //assertEquals(true, result.getCredentialsNonExpired());
       // assertEquals(true, result.getEnabled());        
        assertEquals(now, result.getCreatedAt().truncatedTo(ChronoUnit.SECONDS));
    }

    @Test
    void testFindAll() {
        List<User> list = input.mockEntityList();

        LocalDateTime now = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);

        when(repository.findAll()).thenReturn(list);

        var user = service.findAll();

        assertNotNull(user);
       //assertEquals(14, user.size());
        assertEquals(7, user.size());

        var userOne = user.get(1);
        assertNotNull(userOne);
        assertNotNull(userOne.getKey());
        assertNotNull(userOne.getLinks());
        assertTrue(userOne.toString().contains("[</api/user/v1/2>;rel=\"self\"]"));
        assertEquals("This is a first name.2", userOne.getFirstName());
        assertEquals("This is a last name.2", userOne.getLastName());
        //assertEquals("This is a password.2", userOne.getPassword());
        assertEquals("This is a bio.2", userOne.getBio());
        //assertEquals(true, userOne.getAccountNonExpired());
       // assertEquals(true, userOne.getAccountNonLocked());
       // assertEquals(true, userOne.getCredentialsNonExpired());
        assertEquals(true, userOne.getEnabled());
        assertEquals(now, userOne.getCreatedAt().truncatedTo(ChronoUnit.SECONDS));

        var userFour = user.get(4);
        assertNotNull(userFour);
        assertNotNull(userFour.getKey());
        assertNotNull(userFour.getLinks());
        
        System.out.println("Links to String: " + userFour.toString());
        
        assertTrue(userFour.toString().contains("[</api/user/v1/8>;rel=\"self\"]"));
        assertEquals("This is a first name.8", userFour.getFirstName());
        assertEquals("This is a last name.8", userFour.getLastName());
        //assertEquals("This is a password.8", userFour.getPassword());
        assertEquals("This is a bio.8", userFour.getBio());
        assertEquals(now, userFour.getCreatedAt().truncatedTo(ChronoUnit.SECONDS));
       // assertEquals(true, userFour.getAccountNonExpired());
        //assertEquals(true, userFour.getAccountNonLocked());
        //assertEquals(true, userFour.getCredentialsNonExpired());
        assertEquals(true, userFour.getEnabled());
        
        var userSeven = user.get(6);
        assertNotNull(userSeven);
        assertNotNull(userSeven.getKey());
        assertNotNull(userSeven.getLinks());
        assertTrue(userSeven.toString().contains("[</api/user/v1/12>;rel=\"self\"]"));
        assertEquals("This is a first name.12", userSeven.getFirstName());
        assertEquals("This is a last name.12", userSeven.getLastName());
       // assertEquals("This is a password.12", userSeven.getPassword());
        assertEquals("This is a bio.12", userSeven.getBio());
        assertEquals(now, userSeven.getCreatedAt().truncatedTo(ChronoUnit.SECONDS));
       // assertEquals(true, userSeven.getAccountNonExpired());
       // assertEquals(true, userSeven.getAccountNonLocked());
       // assertEquals(true, userSeven.getCredentialsNonExpired());
        assertEquals(true, userSeven.getEnabled());        
    }

    @Test
    void testCreate() throws Exception {
        User entity = input.mockEntity(1);
        entity.setId(1L);

        User persisted = entity;
        persisted.setId(1L);

        UserVO vo = input.mockVO(1);
        vo.setKey(1L);

        LocalDateTime now = LocalDateTime.now();

        vo.setCreatedAt(now);

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);

        when(repository.save(captor.capture())).thenReturn(persisted);
       // when(repository.save(captor.capture())).thenReturn(entity);
        when(roleRepository.findById(any())).thenReturn(Optional.of(new Role()));

        var result = service.create(vo);

        assertNotNull(result);
        assertNotNull(result.getKey());

        assertTrue(result.toString().contains("[</api/user/v1/1>;rel=\"self\"]"));
        assertEquals("This is a first name.1", result.getFirstName());
        assertEquals("This is a last name.1", result.getLastName());
       // assertEquals("This is a password.1", result.getPassword());
        assertEquals("This is a bio.1", result.getBio());
        assertEquals(true, result.getAccountNonExpired());
        assertEquals(true, result.getAccountNonLocked());
        assertEquals(true, result.getCredentialsNonExpired());
        assertEquals(false, result.getEnabled());         
        assertEquals(now.truncatedTo(ChronoUnit.SECONDS), result.getCreatedAt().truncatedTo(ChronoUnit.SECONDS));
        assertEquals(false, result.getEnabled());
        assertEquals("ROLE_USER_1", result.getRoles().get(0).getName());

        User capturedUser = captor.getValue();
        assertEquals(entity.getId(), capturedUser.getId());
        assertEquals(entity.getFirstName(), capturedUser.getFirstName());
        assertEquals(entity.getLastName(), capturedUser.getLastName());
     //   assertEquals(entity.getPassword(), capturedUser.getPassword());
        assertEquals(entity.getBio(), capturedUser.getBio());
        assertEquals(entity.getAccountNonExpired(), capturedUser.getAccountNonExpired());
        assertEquals(entity.getAccountNonLocked(), capturedUser.getAccountNonLocked());

        assertEquals(entity.getCredentialsNonExpired(), capturedUser.getCredentialsNonExpired());
        assertEquals(now.truncatedTo(ChronoUnit.SECONDS), result.getCreatedAt().truncatedTo(ChronoUnit.SECONDS));
        assertEquals(entity.getEnabled(), capturedUser.getEnabled());
    }

    @Test
    void testCreateWithNullUser() throws Exception {

        Exception exception = assertThrows(RequiredObjectIsNullException.class, () -> {
            service.create(null);
        });

        //String expectedMessage = "It is not allowed to persist a null object";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(actualMessage));
    }

    @Test
    void testUpdate() throws Exception {
        User existingEntity = input.mockEntity(1);
        existingEntity.setId(1L);

        UserVO vo = input.mockVO(1);
        vo.setKey(1L);

        LocalDateTime now = LocalDateTime.now();
        vo.setCreatedAt(now);

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        
        //when(repository.findById(1L)).thenReturn(Optional.of(existingEntity));
        when(repository.save(captor.capture())).thenReturn(existingEntity);
        when(roleRepository.findById(any())).thenReturn(Optional.of(new Role()));

        var result = service.create(vo);

        assertNotNull(result);
        assertNotNull(result.getKey());

        assertTrue(result.toString().contains("[</api/user/v1/1>;rel=\"self\"]"));
        assertEquals("This is a first name.1", result.getFirstName());
        assertEquals("This is a last name.1", result.getLastName());
       // assertEquals("This is a password.1", result.getPassword());
        assertEquals("This is a bio.1", result.getBio());
        assertEquals(true, result.getAccountNonExpired());
        assertEquals(true, result.getAccountNonLocked());
        assertEquals(true, result.getCredentialsNonExpired());
        assertEquals(false, result.getEnabled());         
        assertEquals(now.truncatedTo(ChronoUnit.SECONDS), result.getCreatedAt().truncatedTo(ChronoUnit.SECONDS));
        assertEquals(false, result.getEnabled());
        assertEquals("ROLE_USER_1", result.getRoles().get(0).getName());


        User capturedUser = captor.getValue();

        assertEquals(existingEntity.getId(), capturedUser.getId());
        assertEquals(existingEntity.getFirstName(), capturedUser.getFirstName());
        assertEquals(existingEntity.getLastName(), capturedUser.getLastName());
     //   assertEquals(entity.getPassword(), capturedUser.getPassword());
        assertEquals(existingEntity.getBio(), capturedUser.getBio());
       // System.out.println("capturedUser.getAccountNonExpired(): " + capturedUser.getAccountNonExpired());
       // assertEquals(false, capturedUser.getAccountNonExpired());
        assertEquals(existingEntity.getAccountNonExpired(), capturedUser.getAccountNonExpired());
        assertEquals(existingEntity.getAccountNonLocked(), capturedUser.getAccountNonLocked());

        assertEquals(existingEntity.getCredentialsNonExpired(), capturedUser.getCredentialsNonExpired());
        assertEquals(now.truncatedTo(ChronoUnit.SECONDS), result.getCreatedAt().truncatedTo(ChronoUnit.SECONDS));
        assertEquals(existingEntity.getEnabled(), capturedUser.getEnabled());
    }

    @Test
    void testUpdateWithNullUser() throws Exception {

        Exception exception = assertThrows(RequiredObjectIsNullException.class, () -> {
            service.update(null);
        });

        //String expectedMessage = "It is not allowed to persist a null object";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(actualMessage));
    }

    @Test
    void testDelete() {
        User entity = input.mockEntity(1);
        entity.setId(1L);
        when(repository.findById(1L)).thenReturn(Optional.of(entity));
        service.delete(1L);
    }

}
