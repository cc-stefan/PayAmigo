package com.db.payamigo.services;

import com.db.payamigo.entity.User;
import com.db.payamigo.repository.UserRepository;
import com.db.payamigo.service.UserService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@RunWith(MockitoJUnitRunner.Silent.class)
public class UsersServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    private User user1, user2;

    @Before
    public void initializeUserMocks() {
        user1 = new User(1, "Daniel", "daniel@gmail.com", "1234");
        user2 = new User(2, "Mihai", "mihai@gmail.com", "5678");
    }

    @Test
    public void testCreateUser() {
        when(userRepository.save(user1)).thenReturn(user1);

        User createdUser = userService.createUser(user1);
        assertEquals(user1, createdUser);
    }

    @Test
    public void testGetAllUsers() {
        when(userRepository.findAll()).thenReturn(Arrays.asList(user1, user2));

        List<User> users = userService.getAllUsers();
        assertEquals(2, users.size());
    }

    @Test
    public void testGetUserById() {
        when(userRepository.findById(1)).thenReturn(Optional.of(user1));

        Optional<User> foundUser = userService.getUserById(1);
        assertTrue(foundUser.isPresent());
        assertEquals(user1, foundUser.get());
    }

    @Test
    public void testUpdateUser() {
        User updatedUser = new User();
        updatedUser.setName("Daniel Popa");
        updatedUser.setEmail("daniel@icloud.com");
        updatedUser.setPassword("12345678");

        when(userRepository.findById(1)).thenReturn(Optional.of(user1));
        when(userRepository.save(user1)).thenReturn(updatedUser);

        User result = userService.updateUser(1, updatedUser);
        assertEquals(updatedUser, result);
    }

    @Test
    public void testDeleteUser() {
        when(userRepository.findById(1)).thenReturn(Optional.of(user1));
        userService.deleteUser(1);
        verify(userRepository, times(1)).deleteById(1);

        when(userRepository.findById(1)).thenReturn(Optional.empty());
        Optional<User> deletedUser = userService.getUserById(1);
        assertEquals(Optional.empty(), deletedUser);
    }

    @Test
    public void testCreateUserWithExistingEmail() {
        user2.setEmail(user1.getEmail());

        List<User> existingUsers = new ArrayList<>();
        existingUsers.add(user1);

        when(userRepository.findAll()).thenReturn(existingUsers);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            userService.createUser(user2);
        });

        assertEquals("Email is already in use", exception.getMessage());
    }
}

