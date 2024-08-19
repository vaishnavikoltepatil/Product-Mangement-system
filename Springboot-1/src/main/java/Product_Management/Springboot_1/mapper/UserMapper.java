package Product_Management.Springboot_1.mapper;

import Product_Management.Springboot_1.dto.ProductDto;
import Product_Management.Springboot_1.dto.UserDto;
import Product_Management.Springboot_1.entity.Product;
import Product_Management.Springboot_1.entity.User;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class UserMapper {

    public static UserDto mapToUserDto(User user) {
        List<ProductDto> productDtos = user.getProducts().stream()
                .map(ProductMapper::mapToProductDto)
                .collect(Collectors.toList());

        return new UserDto(user.getId(), user.getUsername(), user.getEmail(), productDtos);
    }

    public static User mapToUser(UserDto userDto, Set<Product> products) {
        return new User(userDto.getUsername(), userDto.getEmail(), products);
    }
}