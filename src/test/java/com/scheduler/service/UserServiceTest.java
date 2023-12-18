package com.scheduler.service;

import com.scheduler.repository.UserRepository;
import com.scheduler.service.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class UserServiceTest {
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;
    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }
    @Test
    void testFindAllUsers() {
        User user1 = new User(); // Configura user1 con los datos necesarios
        User user2 = new User(); // Configura user2 con los datos necesarios

        when(userRepository.findAll()).thenReturn(Arrays.asList(user1, user2));

        List<User> users = userService.findAllUsers();

        assertNotNull(users, "La lista de usuarios no debe ser nula");
        assertEquals(2, users.size(), "La lista de usuarios debe contener dos elementos");
        assertTrue(users.contains(user1), "La lista de usuarios debe contener user1");
        assertTrue(users.contains(user2), "La lista de usuarios debe contener user2");
    }
}