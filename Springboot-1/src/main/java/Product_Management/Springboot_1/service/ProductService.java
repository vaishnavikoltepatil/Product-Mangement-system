package Product_Management.Springboot_1.service;

import Product_Management.Springboot_1.dto.ProductDto;
import Product_Management.Springboot_1.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ProductService {

    ProductDto createProduct(ProductDto productDto, Long categoryId);

    ProductDto getProductById(Long productId);

    List<ProductDto> getAllProducts();

    ProductDto updateProduct(Long productId, ProductDto updateProduct);

    void deleteProduct(Long productId);

    Page<ProductDto> getProductsPaginatedAndSorted(Pageable pageable);

    List<ProductDto> findProductsWithSorting(String field);

    Page<ProductDto> findProductsWithPagination(int offset, int pageSize);

    Page<ProductDto> findProductsWithPaginationAndSorting(int offset, int pageSize, String field);

    ProductDto getProductByName(String name);

    List<ProductDto> getAllProductsSortedByIdDesc();
}
