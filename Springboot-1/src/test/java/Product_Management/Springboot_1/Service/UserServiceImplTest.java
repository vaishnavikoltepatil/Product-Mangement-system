package Product_Management.Springboot_1.Service;

import Product_Management.Springboot_1.Exception.EmailNotFoundException;
import Product_Management.Springboot_1.Exception.ResultNotFoundException;
import Product_Management.Springboot_1.Exception.InvalidOtpException;
import Product_Management.Springboot_1.Repository.ProductRepository;
import Product_Management.Springboot_1.Repository.UserRepository;
import Product_Management.Springboot_1.Util.Emailutil;
import Product_Management.Springboot_1.Util.OtpUtil;
import Product_Management.Springboot_1.dto.LoginDto;
import Product_Management.Springboot_1.dto.ProductDto;
import Product_Management.Springboot_1.dto.RegisterDto;
import Product_Management.Springboot_1.dto.UserDto;
import Product_Management.Springboot_1.entity.Product;
import Product_Management.Springboot_1.entity.User;
import Product_Management.Springboot_1.service.UserServiceimpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import jakarta.mail.MessagingException;
import org.springframework.data.domain.*;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private OtpUtil otpUtil;



    @Mock
    private Emailutil emailUtil;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private UserServiceimpl userService;

    private final String email = "test@example.com";
    private final String otp = "123456";

    private User user;
    private UserDto userDto;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        user = new User(1L, "testuser", "testuser@gmail.com", "password", true, "otp", LocalDateTime.now(), false, Set.of());
        userDto = new UserDto(1L, "testuser", "testuser@gmail.com", null);
    }

    @Test
    void testGetUserById_UserFound() {
        when(userRepository.findByIdAndDeletedFalse(1L)).thenReturn(Optional.of(user));
        UserDto result = userService.getUserById(1L);
        assertNotNull(result);
        assertEquals(userDto.getId(), result.getId());
        verify(userRepository, times(1)).findByIdAndDeletedFalse(1L);
    }

    @Test
    void testGetUserById_UserNotFound() {
        when(userRepository.findByIdAndDeletedFalse(1L)).thenReturn(Optional.empty());
        Exception exception = assertThrows(ResultNotFoundException.class, () -> userService.getUserById(1L));
        assertEquals("User with ID 1 not found", exception.getMessage());
        verify(userRepository, times(1)).findByIdAndDeletedFalse(1L);
    }

    @Test
    void testCreateUser() {
        // Initialize products list to prevent null pointer
        List<ProductDto> productDtos = new ArrayList<>();
        userDto.setProducts(productDtos); // Ensure that products are initialized

        // Mock repository behavior
        when(userRepository.findByEmail(userDto.getEmail())).thenReturn(Optional.empty());
        when(productRepository.findById(anyLong())).thenReturn(Optional.empty()); // Mock product repository as needed
        when(userRepository.save(any(User.class))).thenReturn(user);

        // Call the service method
        UserDto result = userService.createUser(userDto);

        // Assertions
        assertNotNull(result);
        assertEquals(userDto.getUsername(), result.getUsername());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testDeleteUser() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        userService.deleteUser(1L);
        verify(userRepository, times(1)).save(user);
        assertTrue(user.getDeleted());
    }

    @Test
    void testLogin_Success() {
        String email = "testuser@gmail.com";
        String password = "password";
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        LoginDto loginDto = new LoginDto(email, password);
        assertDoesNotThrow(() -> userService.login(loginDto));
        verify(userRepository, times(1)).findByEmail(email);
    }

    @Test
    void testLogin_Failure_IncorrectPassword() {
        String email = "testuser@gmail.com";
        String incorrectPassword = "wrongpassword";
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        LoginDto loginDto = new LoginDto(email, incorrectPassword);
        assertThrows(ResultNotFoundException.class, () -> userService.login(loginDto));
        verify(userRepository, times(1)).findByEmail(email);
    }

    @Test
    void testLogin_Failure_UserNotFound() {
        String email = "testuser@gmail.com";
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());
        LoginDto loginDto = new LoginDto(email, "password");
        assertThrows(EmailNotFoundException.class, () -> userService.login(loginDto));
        verify(userRepository, times(1)).findByEmail(email);
    }

    // Test for register method
    @Test
    void testRegisterUser_Success() throws MessagingException {
        RegisterDto registerDto = new RegisterDto("testuser", "testuser@gmail.com", "password");

        when(userRepository.findByEmail(registerDto.getEmail())).thenReturn(Optional.empty());
        when(otpUtil.generateOtp()).thenReturn("123456");

        doNothing().when(emailUtil).sendOtpEmail(registerDto.getEmail(), "123456");

        assertDoesNotThrow(() -> userService.register(registerDto));

        verify(userRepository, times(1)).save(any(User.class));
        verify(emailUtil, times(1)).sendOtpEmail(registerDto.getEmail(), "123456");
    }

    @Test
    void testRegisterUser_EmailAlreadyExists() {
        RegisterDto registerDto = new RegisterDto("testuser", "testuser@gmail.com", "password");
        when(userRepository.findByEmail(registerDto.getEmail())).thenReturn(Optional.of(user));

        assertThrows(IllegalArgumentException.class, () -> userService.register(registerDto));
        verify(userRepository, times(1)).findByEmail(registerDto.getEmail());
    }

    @Test
    void testVerifyOtpAndSetPassword_Success() throws Exception {
        String email = "testuser@gmail.com";
        String otp = "123456";
        String newPassword = "newPassword";

        // Mock the user retrieval by email
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        // Use reflection to access the private otpStorage field
        Field otpStorageField = UserServiceimpl.class.getDeclaredField("otpStorage");
        otpStorageField.setAccessible(true);

        @SuppressWarnings("unchecked")
        Map<String, String> otpStorage = (Map<String, String>) otpStorageField.get(userService);

        // Simulate OTP storage behavior by putting OTP in the map
        otpStorage.put(email, otp);

        // Call the method
        userService.verifyOtpAndSetPassword(email, otp, newPassword);

        // Verify that the password was set and the user was saved
        verify(userRepository, times(1)).save(user);
        assertEquals(newPassword, user.getPassword());

        // Verify that the OTP was removed from storage
        assertFalse(otpStorage.containsKey(email));
    }


    @Test
    void testVerifyOtpAndSetPassword_InvalidOtp() throws Exception {
        String email = "testuser@gmail.com";
        String wrongOtp = "654321";
        String correctOtp = "123456";
        String newPassword = "newPassword";

        // Mock the user retrieval by email
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        // Use reflection to access the private otpStorage field
        Field otpStorageField = UserServiceimpl.class.getDeclaredField("otpStorage");
        otpStorageField.setAccessible(true);

        @SuppressWarnings("unchecked")
        Map<String, String> otpStorage = (Map<String, String>) otpStorageField.get(userService);

        // Simulate OTP storage behavior by putting the correct OTP in the map
        otpStorage.put(email, correctOtp);

        // Call the method and expect an InvalidOtpException due to wrong OTP
        assertThrows(InvalidOtpException.class, () -> userService.verifyOtpAndSetPassword(email, wrongOtp, newPassword));


        // Verify that the user was not saved because the OTP was incorrect
        verify(userRepository, times(0)).save(user);

        // Optionally, verify that the OTP is still in storage (since it was not removed due to the invalid OTP)
        assertTrue(otpStorage.containsKey(email));
    }

    // Tests for paginated and sorted user retrieval
    @Test
    void testGetUserPaginatedAndSorted() {
        Pageable pageable = PageRequest.of(0, 10);
        List<User> users = List.of(user);
        Page<User> page = new PageImpl<>(users);
        when(userRepository.findAll(pageable)).thenReturn(page);

        Page<UserDto> result = userService.getUserPaginatedAndSorted(pageable);
        assertEquals(1, result.getTotalElements());
        verify(userRepository, times(1)).findAll(pageable);
    }

    @Test
    void testGetUserByName_UserFound() {
        String username = "testuser";
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

        UserDto result = userService.getUserByName(username);

        assertNotNull(result);
        assertEquals(userDto.getUsername(), result.getUsername());
        verify(userRepository, times(1)).findByUsername(username);
    }

    @Test
    void testGetUserByName_UserNotFound() {
        String username = "testuser";
        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        Exception exception = assertThrows(ResultNotFoundException.class, () -> userService.getUserByName(username));
        assertEquals("User with name 'testuser' not found", exception.getMessage());

        verify(userRepository, times(1)).findByUsername(username);
    }

    @Test
    void testFindUserWithPagination() {
        int offset = 0;
        int pageSize = 10;
        Pageable pageable = PageRequest.of(offset, pageSize);
        List<User> users = List.of(user);
        Page<User> page = new PageImpl<>(users, pageable, users.size());

        when(userRepository.findAll(pageable)).thenReturn(page);

        Page<UserDto> result = userService.findUserWithPagination(offset, pageSize);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(userDto.getUsername(), result.getContent().get(0).getUsername());

        verify(userRepository, times(1)).findAll(pageable);
    }

    @Test
    void testFindUserWithSorting() {
        String field = "username";
        List<User> users = List.of(user);
        when(userRepository.findAll(Sort.by(Sort.Direction.DESC, field))).thenReturn(users);

        List<UserDto> result = userService.findUserWithSorting(field);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(userDto.getUsername(), result.get(0).getUsername());

        verify(userRepository, times(1)).findAll(Sort.by(Sort.Direction.DESC, field));
    }

    @Test
    void testUpdateUser_Success() {
        Long userId = 1L;
        String newUsername = "updatedUser";
        UserDto updatedUserDto = new UserDto(userId, newUsername, "testuser@gmail.com", null);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        UserDto result = userService.updateUser(userId, updatedUserDto);

        assertNotNull(result);
        assertEquals(newUsername, result.getUsername());
        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void testUpdateUser_UserNotFound() {
        Long userId = 1L;
        UserDto updatedUserDto = new UserDto(userId, "updatedUser", "testuser@gmail.com", null);

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        Exception exception = assertThrows(ResultNotFoundException.class, () -> userService.updateUser(userId, updatedUserDto));
        assertEquals("User with ID 1 not found", exception.getMessage());

        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    public void testForgotPassword_UserExists_Success() throws MessagingException {
        // Arrange
        User user = new User();
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(otpUtil.generateOtp()).thenReturn(otp);

        // Act
        String result = userService.forgotPassword(email);

        // Assert
        assertEquals("OTP has been sent to your email. Please use it to reset your password.", result);
        verify(emailUtil).sendOtpEmail(email, otp);
    }

    @Test
    public void testForgotPassword_UserNotFound() {
        // Arrange
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        // Act & Assert
        EmailNotFoundException thrown = assertThrows(
                EmailNotFoundException.class,
                () -> userService.forgotPassword(email),
                "Expected forgotPassword() to throw, but it didn't"
        );
        assertEquals("User not found with this email: " + email, thrown.getMessage());
    }



}
