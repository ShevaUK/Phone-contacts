package com.example.Phonecontacts.service.impl;

import com.example.Phonecontacts.dto.UserDTO;
import com.example.Phonecontacts.exception.BaseException;
import com.example.Phonecontacts.model.User;
import com.example.Phonecontacts.repository.UserRepository;
import com.example.Phonecontacts.utils.BaseResponseDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;






public class UserServiceImplTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void registerAccount_ShouldReturnSuccessResponse() {
        // Arrange
        UserDTO userDTO = new UserDTO();
        userDTO.setUsername("testuser");
        userDTO.setPassword("password");

        User user = new User();
        user.setUsername(userDTO.getUsername());
        user.setPassword("encoded_password");

        BaseResponseDTO expectedResponse = new BaseResponseDTO();
        expectedResponse.setCode(String.valueOf(HttpStatus.OK.value()));
        expectedResponse.setMessage("Create account successfully");

        when(userRepository.save(any(User.class))).thenReturn(user);
        when(passwordEncoder.encode(userDTO.getPassword())).thenReturn("encoded_password");

        // Act
        BaseResponseDTO response = userService.registerAccount(userDTO);

        // Assert
        assertEquals(expectedResponse.getCode(), response.getCode());
        assertEquals(expectedResponse.getMessage(), response.getMessage());
        verify(userRepository, times(1)).save(any(User.class));
        verify(passwordEncoder, times(1)).encode(userDTO.getPassword());
    }

    @Test
    void registerAccount_ShouldReturnServiceUnavailableResponse_WhenExceptionOccurs() {
        // Arrange
        UserDTO userDTO = new UserDTO();
        userDTO.setUsername("testuser");
        userDTO.setPassword("password");

        BaseResponseDTO expectedResponse = new BaseResponseDTO();
        expectedResponse.setCode(String.valueOf(HttpStatus.SERVICE_UNAVAILABLE.value()));
        expectedResponse.setMessage("Service unavailable");

        when(userRepository.save(any(User.class))).thenThrow(new RuntimeException());

        // Act
        BaseResponseDTO response = userService.registerAccount(userDTO);

        // Assert
        assertEquals(expectedResponse.getCode(), response.getCode());
        assertEquals(expectedResponse.getMessage(), response.getMessage());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void validateAccount_ShouldThrowException_WhenUserDTOIsEmpty() {
        // Arrange
        UserDTO userDTO = null;

        // Act & Assert
        assertThrows(BaseException.class, () -> userService.validateAccount(userDTO));
    }

    @Test
    void validateAccount_ShouldThrowException_WhenUsernameExists() {
        // Arrange
        UserDTO userDTO = new UserDTO();
        userDTO.setUsername("existinguser");

        User existingUser = new User();
        existingUser.setUsername("existinguser");

        when(userRepository.findByUsername(userDTO.getUsername())).thenReturn(existingUser);

        // Act & Assert
        assertThrows(BaseException.class, () -> userService.validateAccount(userDTO));
        verify(userRepository, times(1)).findByUsername(userDTO.getUsername());
    }

    @Test
    void validateAccount_ShouldNotThrowException_WhenUsernameDoesNotExist() {
        // Arrang
        UserDTO userDTO = new UserDTO();
        userDTO.setUsername("newuser");

        when(userRepository.findByUsername(userDTO.getUsername())).thenReturn(null);

        // Act & Assert
        assertDoesNotThrow(() -> userService.validateAccount(userDTO));
        verify(userRepository, times(1)).findByUsername(userDTO.getUsername());
    }
}

