package Product_Management.Springboot_1.service;

import Product_Management.Springboot_1.dto.CategoryDto;
import Product_Management.Springboot_1.dto.ProductDto;
import Product_Management.Springboot_1.dto.UserDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface UserService {

    UserDto createUser(UserDto userDto);

    UserDto getUserById(Long userId);

    List<UserDto> getAllUsers();

    UserDto updateUser(Long userId, UserDto updateUser);

    void deleteUser(Long userId);

    Page<UserDto> getUserPaginatedAndSorted(Pageable pageable);

    List<UserDto> findUserWithSorting(String field);

    Page<UserDto> findUserWithPagination(int offset, int pageSize);

    Page<UserDto> findUserWithPaginationAndSorting(int offset, int pageSize, String field);

    UserDto getUserByName(String username);
}
