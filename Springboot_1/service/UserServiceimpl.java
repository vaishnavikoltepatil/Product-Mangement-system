package Product_Management.Springboot_1.service;

import Product_Management.Springboot_1.Exception.ResultNotFoundException;
import Product_Management.Springboot_1.Repository.ProductRepository;
import Product_Management.Springboot_1.Repository.UserRepository;
import Product_Management.Springboot_1.Util.Emailutil;
import Product_Management.Springboot_1.Util.OtpUtil;
import Product_Management.Springboot_1.dto.LoginDto;
import Product_Management.Springboot_1.dto.RegisterDto;
import Product_Management.Springboot_1.dto.UserDto;
import Product_Management.Springboot_1.entity.Product;
import Product_Management.Springboot_1.entity.User;
import Product_Management.Springboot_1.mapper.UserMapper;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class UserServiceimpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OtpUtil otpUtil;

    @Autowired
    private Emailutil emailUtil;

    // Email pattern for basic validation
    private static final String EMAIL_PATTERN = "^[A-Za-z0-9+_.-]+@(.+)$";

    @Override
    public UserDto createUser(UserDto userDto) {
        // Check if the email already exists
        if (userRepository.findByEmail(userDto.getEmail()).isPresent()) {
            throw new IllegalArgumentException("User already exists with this email: " + userDto.getEmail());
        }

        // Validate email format
        if (!Pattern.matches(EMAIL_PATTERN, userDto.getEmail())) {
            throw new IllegalArgumentException("Invalid email format: " + userDto.getEmail());
        }

        Set<Product> products = userDto.getProducts().stream()
                .map(dto -> {
                    if (dto.getId() != null) {
                        return productRepository.findById(dto.getId())
                                .orElseThrow(() -> new ResultNotFoundException("Product with ID " + dto.getId() + " not found"));
                    } else {
                        return null; // Handle new products as per your requirements
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        User user = UserMapper.mapToUser(userDto, products);
        User savedUser = userRepository.save(user);
        return UserMapper.mapToUserDto(savedUser);
    }

    @Override
    public UserDto getUserById(Long userId) {
        User user = userRepository.findByIdAndDeletedFalse(userId)
                .orElseThrow(() -> new ResultNotFoundException("User with ID " + userId + " not found"));
        return UserMapper.mapToUserDto(user);
    }

    @Override
    public List<UserDto> getAllUsers() {
        List<User> users = userRepository.findAllByDeletedFalse();
        return users.stream().map(UserMapper::mapToUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto updateUser(Long userId, UserDto updateUser) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResultNotFoundException("User with ID " + userId + " not found"));

        user.setUsername(updateUser.getUsername());
        User updatedUser = userRepository.save(user);
        return UserMapper.mapToUserDto(updatedUser);
    }

    @Override
    public void deleteUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResultNotFoundException("User with ID " + userId + " not found"));

        user.setDeleted(true); // Mark as deleted
        userRepository.save(user); // Save the user to update the deleted flag
    }

    @Override
    public Page<UserDto> getUserPaginatedAndSorted(Pageable pageable) {
        return userRepository.findAll(pageable)
                .map(UserMapper::mapToUserDto);
    }

    @Override
    public List<UserDto> findUserWithSorting(String field) {
        return userRepository.findAll(Sort.by(Sort.Direction.DESC, field)).stream()
                .map(UserMapper::mapToUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public Page<UserDto> findUserWithPagination(int offset, int pageSize) {
        Pageable pageable = PageRequest.of(offset, pageSize);
        return userRepository.findAll(pageable).map(UserMapper::mapToUserDto);
    }

    @Override
    public Page<UserDto> findUserWithPaginationAndSorting(int offset, int pageSize, String field) {
        Pageable pageable = PageRequest.of(offset, pageSize).withSort(Sort.by(field));
        return userRepository.findAll(pageable).map(UserMapper::mapToUserDto);
    }

    @Override
    public UserDto getUserByName(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResultNotFoundException("User with name '" + username + "' not found"));
        return UserMapper.mapToUserDto(user);
    }

    public String register(RegisterDto registerDto) {
        // Check if user already exists
        if (userRepository.findByEmail(registerDto.getEmail()).isPresent()) {
            throw new IllegalArgumentException("User already exists with this email: " + registerDto.getEmail());
        }

        // Validate email format
        if (!Pattern.matches(EMAIL_PATTERN, registerDto.getEmail())) {
            throw new IllegalArgumentException("Invalid email format: " + registerDto.getEmail());
        }

        String otp = otpUtil.generateOtp();
        try {
            emailUtil.sendOtpEmail(registerDto.getEmail(), otp);
        } catch (MessagingException e) {
            throw new RuntimeException("Unable to send OTP, please try again");
        }

        User user = new User();
        user.setUsername(registerDto.getUsername());
        user.setEmail(registerDto.getEmail());
        user.setPassword(registerDto.getPassword());
        user.setOtp(otp);
        user.setOtpGeneratedTime(LocalDateTime.now());
        userRepository.save(user);

        return "User registration successful";
    }

    public String verifyAccount(String email, String otp) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with this email: " + email));

        if (user.getOtp().equals(otp) && Duration.between(user.getOtpGeneratedTime(), LocalDateTime.now()).getSeconds() < (1 * 60)) {
            user.setActive(true);
            userRepository.save(user);
            return "OTP verified, you can log in";
        }
        return "Please regenerate OTP and try again";
    }

    public String regenerateOtp(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with this email: " + email));

        String otp = otpUtil.generateOtp();
        try {
            emailUtil.sendOtpEmail(email, otp);
        } catch (MessagingException e) {
            throw new RuntimeException("Unable to send OTP, please try again");
        }

        user.setOtp(otp);
        user.setOtpGeneratedTime(LocalDateTime.now());
        userRepository.save(user);

        return "Email sent, please verify your account within 1 minute";
    }

    public String login(LoginDto loginDto) {
        User user = userRepository.findByEmail(loginDto.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found with this email: " + loginDto.getEmail()));

        if (!loginDto.getPassword().equals(user.getPassword())) {
            return "Password is incorrect";
        } else if (!user.isActive()) {
            return "Your account is not verified";
        }
        return "Login successful";
    }

    @Override
    public String forgotPassword(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with this email: " + email));
        try {
            emailUtil.sendSetPasswordEmail(email);
        } catch (MessagingException e) {
            throw new RuntimeException("Unable to send password email, please try again");
        }

        return "Please check your email to set a new password for your account";
    }

    @Override
    public String setPassword(String email, String newPassword) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with this email: " + email));
        user.setPassword(newPassword);
        userRepository.save(user);
        return "New password set successfully, log in with the new password!";
    }
}
