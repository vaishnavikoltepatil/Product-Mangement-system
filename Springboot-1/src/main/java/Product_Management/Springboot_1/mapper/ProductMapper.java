package Product_Management.Springboot_1.mapper;

import Product_Management.Springboot_1.dto.ProductDto;
import Product_Management.Springboot_1.entity.Product;
import Product_Management.Springboot_1.entity.Category;
import Product_Management.Springboot_1.Repository.CategoryRepository;

public class ProductMapper {

    public static ProductDto mapToProductDto(Product product) {
        return new ProductDto(
                product.getId(),
                product.getName(),
                product.getQuantity(),
                product.getPrice(),
                product.getCategory() != null ? product.getCategory().getId() : null,
                product.getCategory() != null ? product.getCategory().getName() : null
        );
    }

    public static Product mapToProduct(ProductDto productDto, CategoryRepository categoryRepository) {
        Product product = new Product();
        product.setId(productDto.getId());
        product.setName(productDto.getName());
        product.setQuantity(productDto.getQuantity());
        product.setPrice(productDto.getPrice());

        if (productDto.getCategoryId() != null) {
            Category category = categoryRepository.findById(productDto.getCategoryId())
                    .orElseThrow(() -> new RuntimeException("Category not found")); // Custom exception recommended
            product.setCategory(category);
        }

        return product;
    }
}