package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UserControllerTest {

    private UserController  userController;

    // Creates a mock object here
    private UserRepository userRepository = mock(UserRepository.class);

    private CartRepository cartRepository = mock(CartRepository.class);

    private BCryptPasswordEncoder encoder = mock(BCryptPasswordEncoder.class);

    @Before
    public void setUp() {
        userController = new UserController();
        TestUtils.injectObjects(userController, "userRepository", userRepository);
        TestUtils.injectObjects(userController, "cartRepository", cartRepository);
        TestUtils.injectObjects(userController, "bCryptPasswordEncoder", encoder);
    }

    // Happy path is the positive use case (sanity test)
    @Test
    public void createUserHappyPath() throws Exception {
        when(encoder.encode("testPassword")).thenReturn("thisIsHashed"); // Stubbing example

        CreateUserRequest r = new CreateUserRequest();
        r.setUsername("test");
        r.setPassword("testPassword");
        r.setConfirmPassword("testPassword");

        final ResponseEntity<User> response = userController.createUser(r);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        User u = response.getBody();
        assertNotNull(u);
        assertEquals(0, u.getId());
        assertEquals("test", u.getUsername());
        assertEquals("thisIsHashed", u.getPassword());
    }

    @Test
    public void testFindById() {
        User u = new User();
        u.setId(1);
        u.setUsername("testName");
        u.setPassword("hashedPassword");
        u.setSalt("testSalt");
        when(userRepository.findById(1L)).thenReturn(Optional.of(u));

        final ResponseEntity<User> response = userController.findById(1L);

        User result = response.getBody();
        assertNotNull(result);
        assertEquals("testName", result.getUsername());
        assertEquals("hashedPassword", result.getPassword());
    }

    @Test
    public void testFindByUserName() {
        User u = new User();
        u.setId(1);
        u.setUsername("testName");
        u.setPassword("hashedPassword");
        u.setSalt("testSalt");
        when(userRepository.findByUsername("testName")).thenReturn(u);

        final ResponseEntity<User> response = userController.findByUserName("testName");

        User result = response.getBody();
        assertNotNull(result);
        assertEquals("testName", result.getUsername());
        assertEquals("hashedPassword", result.getPassword());
    }

}
