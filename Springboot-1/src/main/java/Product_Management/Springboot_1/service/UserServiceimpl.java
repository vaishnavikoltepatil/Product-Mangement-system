package Product_Management.Springboot_1.service;

import Product_Management.Springboot_1.Exception.ResultNotFoundException;
import Product_Management.Springboot_1.Repository.ProductRepository;
import Product_Management.Springboot_1.Repository.UserRepository;
import Product_Management.Springboot_1.dto.CategoryDto;
import Product_Management.Springboot_1.dto.UserDto;
import Product_Management.Springboot_1.entity.Category;
import Product_Management.Springboot_1.entity.Product;
import Product_Management.Springboot_1.entity.User;
import Product_Management.Springboot_1.mapper.CategoryMapper;
import Product_Management.Springboot_1.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserServiceimpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @Transactional
    @Override
    public UserDto createUser(UserDto userDto) {
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
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResultNotFoundException("User with ID " + userId + " not found"));
        return UserMapper.mapToUserDto(user);
    }


    @Override
    public List<UserDto> getAllUsers() {
        List<User> users = userRepository.findAll();
        return users.stream().map(UserMapper::mapToUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto updateUser(Long userId, UserDto updateUser) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResultNotFoundException("User with ID " + userId+ " not found"));

         user.setUsername(updateUser.getUsername());


       User updatedUser = userRepository.save(user);
        return UserMapper.mapToUserDto(updatedUser);
    }


    @Override
    public void deleteUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResultNotFoundException("user with  Id " + userId + "not found"));
        userRepository.deleteById(userId);

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
}
