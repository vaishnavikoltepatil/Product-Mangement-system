package Product_Management.Springboot_1.mapper;

import Product_Management.Springboot_1.dto.CategoryDto;
import Product_Management.Springboot_1.entity.Category;

public class CategoryMapper {

    public static CategoryDto mapToCategoryDto(Category category) {
        return new CategoryDto(
                category.getId(),
                category.getName());
    }

    public static Category mapToCategory(CategoryDto categoryDto) {
        return new Category(
                categoryDto.getId(),
                categoryDto.getName(), null);
    }
}
