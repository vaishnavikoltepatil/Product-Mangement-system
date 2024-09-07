package Product_Management.Springboot_1.Controller;


import Product_Management.Springboot_1.Exception.*;
import Product_Management.Springboot_1.dto.LoginDto;
import Product_Management.Springboot_1.dto.RegisterDto;
import Product_Management.Springboot_1.dto.UserDto;

import Product_Management.Springboot_1.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/api/users")
public class UserController {



    @Autowired
    private UserService userService;

    @Autowired
    private PagedResourcesAssembler<UserDto> pagedResourcesAssembler;


    @PostMapping
    public ResponseEntity<UserDto> createUser(@RequestBody UserDto userDto) {
        UserDto savedUser = userService.createUser(userDto);
        return new ResponseEntity<>(savedUser, HttpStatus.CREATED);
    }

    @GetMapping("{id}")
    public ResponseEntity<UserDto> getUserById(@PathVariable("id") Long userId) {
        UserDto userDto = userService.getUserById(userId);
        return ResponseEntity.ok(userDto);
    }

    @GetMapping
    public ResponseEntity<PagedModel<EntityModel<UserDto>>> getAllUsers(Pageable pageable) {
        Page<UserDto> page = userService.getUserPaginatedAndSorted(pageable);
        PagedModel<EntityModel<UserDto>> pagedModel = pagedResourcesAssembler.toModel(page,
                userDto -> EntityModel.of(userDto));
        return ResponseEntity.ok(pagedModel);
    }

    @PutMapping("{id}")
    public ResponseEntity<UserDto> updateUser(@PathVariable("id") Long userId, @RequestBody UserDto updatedUser) {
        UserDto userDto = userService.updateUser(userId, updatedUser);
        return ResponseEntity.ok(userDto);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<String> deleteUser(@PathVariable("id") Long userId) {
        userService.deleteUser(userId);
        return ResponseEntity.ok("User deleted successfully!");
    }

    @GetMapping("/sort/{field}")
    public ResponseEntity<List<UserDto>> getUserWithSort(@PathVariable String field) {
        List<UserDto> sortedUser = userService.findUserWithSorting(field);
        return ResponseEntity.ok(sortedUser);
    }

    @GetMapping("/pagination/{offset}/{pageSize}")
    public ResponseEntity<Page<UserDto>> getUsersWithPagination(@PathVariable int offset, @PathVariable int pageSize) {
        Page<UserDto> paginatedUser = userService.findUserWithPagination(offset, pageSize);
        return ResponseEntity.ok(paginatedUser);
    }

    @GetMapping("/paginationAndSort/{offset}/{pageSize}/{field}")
    public ResponseEntity<Page<UserDto>> getUserWithPaginationAndSort(@PathVariable int offset, @PathVariable int pageSize, @PathVariable String field) {
        Page<UserDto> paginatedAndSortedUser = userService.findUserWithPaginationAndSorting(offset, pageSize, field);
        return ResponseEntity.ok(paginatedAndSortedUser);
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<UserDto> getUserByName(@PathVariable("name") String name) {
        UserDto userDto = userService.getUserByName(name);
        return ResponseEntity.ok(userDto);
    }


    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody RegisterDto registerDto) {
        try {
            userService.register(registerDto);
            return ResponseEntity.ok("User registered successfully. OTP sent to email.");
        } catch (InvalidEmailFormatException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid email format.");
        } catch (UserAlreadyExistsException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User already exists with this email.");
        }
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<String> verifyOtp(@RequestParam String email, @RequestParam String otp) {
        try {
            userService.verifyAccount(email, otp);
            return ResponseEntity.ok("User verified successfully.");
        } catch (EmailNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Email not found.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid OTP.");
        }
    }


    @PostMapping("/regenerate-otp")
    public ResponseEntity<String> regenerateOtp(@RequestParam String email) {
        try {
            userService.regenerateOtp(email);
            return ResponseEntity.ok("OTP regenerated and sent to email.");
        } catch (EmailNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Email not found.");
        }
    }


    @PostMapping("/login")
    public ResponseEntity<String> loginUser(@RequestBody LoginDto loginDto) {
        try {
            userService.login(loginDto);  // Attempt to log in
            return ResponseEntity.ok("Login successful.");
        } catch (EmailNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Email not found.");
        } catch (ResultNotFoundException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Password does not match!!");
        }
    }


    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@RequestParam String email) {
        try {
            String message = userService.forgotPassword(email);
            return ResponseEntity.ok(message);
        } catch (EmailNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Email not found.");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error sending OTP, please try again.");
        }
    }


    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestParam String email, @RequestParam String otp, @RequestParam String newPassword) {
        try {
            userService.verifyOtpAndSetPassword(email, otp, newPassword);
            return ResponseEntity.ok("Password reset successfully.");
        } catch (EmailNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Email not found.");
        } catch (InvalidOtpException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid or expired OTP.");
        }
    }
}
