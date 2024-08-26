package Product_Management.Springboot_1.Controller;


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
    public ResponseEntity<String> register(@RequestBody RegisterDto registerDto) {
        return new ResponseEntity<>(userService.register(registerDto), HttpStatus.OK);
    }

    @PutMapping("/verify-account")
    public ResponseEntity<String> verifyAccount(@RequestParam String email,
                                                @RequestParam String otp) {
        return new ResponseEntity<>(userService.verifyAccount(email, otp), HttpStatus.OK);
    }
    @PutMapping("/regenerate-otp")
    public ResponseEntity<String> regenerateOtp(@RequestParam String email) {
        return new ResponseEntity<>(userService.regenerateOtp(email), HttpStatus.OK);
    }
    @PutMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginDto loginDto) {
        return new ResponseEntity<>(userService.login(loginDto), HttpStatus.OK);
    }

    @PutMapping("/forgot-password")
    public ResponseEntity<String>forgotPassword(@RequestParam String email){
        return new ResponseEntity<>(userService.forgotPassword(email),HttpStatus.OK);
    }

    @PutMapping("/set-password")
    public ResponseEntity<String>setPassword(@RequestParam String email,@RequestHeader String newPassword){

        return new ResponseEntity<>(userService.setPassword(email,newPassword),HttpStatus.OK);
    }

}
