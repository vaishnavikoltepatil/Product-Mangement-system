package Product_Management.Springboot_1.Service;
import Product_Management.Springboot_1.Exception.ResultNotFoundException;
import Product_Management.Springboot_1.Repository.CategoryRepository;
import Product_Management.Springboot_1.Repository.ProductRepository;
import Product_Management.Springboot_1.dto.ProductDto;
import Product_Management.Springboot_1.entity.Category;
import Product_Management.Springboot_1.entity.Product;
import Product_Management.Springboot_1.service.ProductServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ProductServiceImplTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private ProductServiceImpl productService;

    private ProductDto productDto;
    private Product product;
    private Category category;

    @BeforeEach
    public void setUp() {
        category = new Category();
        category.setId(1L);
        category.setName("Category1");

        productDto = new ProductDto(1L, "Product1", 10, 100.0, 1L, "Category1");
        product = new Product("Product1", 10, 100.0, category);
        product.setId(1L);
    }

    @Test
    public void testCreateProduct_Success() {
        when(categoryRepository.findById(anyLong())).thenReturn(Optional.of(category));
        when(productRepository.save(any(Product.class))).thenReturn(product);

        ProductDto createdProductDto = productService.createProduct(productDto, 1L);

        assertEquals(1L, createdProductDto.getId());
        assertEquals("Product1", createdProductDto.getName());
    }

    @Test
    public void testGetProductById_Success() {
        when(productRepository.findById(anyLong())).thenReturn(Optional.of(product));

        ProductDto retrievedProductDto = productService.getProductById(1L);

        assertEquals(1L, retrievedProductDto.getId());
        assertEquals("Product1", retrievedProductDto.getName());
    }

    @Test
    public void testGetProductById_NotFound() {
        when(productRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(ResultNotFoundException.class, () -> productService.getProductById(1L));
    }

    @Test
    public void testGetAllProducts() {
        List<Product> products = List.of(product);
        when(productRepository.findAll()).thenReturn(products);

        List<ProductDto> productDtos = productService.getAllProducts();

        assertEquals(1, productDtos.size());
        assertEquals("Product1", productDtos.get(0).getName());
    }

    @Test
    public void testUpdateProduct_Success() {
        ProductDto updatedProductDto = new ProductDto(1L, "UpdatedProduct", 20, 200.0, 1L, "Category1");
        when(productRepository.findById(anyLong())).thenReturn(Optional.of(product));
        when(productRepository.save(any(Product.class))).thenReturn(product);

        ProductDto result = productService.updateProduct(1L, updatedProductDto);

        assertEquals("UpdatedProduct", result.getName());
    }

    @Test
    public void testUpdateProduct_NotFound() {
        ProductDto updatedProductDto = new ProductDto(1L, "UpdatedProduct", 20, 200.0, 1L, "Category1");
        when(productRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(ResultNotFoundException.class, () -> productService.updateProduct(1L, updatedProductDto));
    }

    @Test
    public void testDeleteProduct_Success() {
        when(productRepository.findById(anyLong())).thenReturn(Optional.of(product));

        productService.deleteProduct(1L);
    }

    @Test
    public void testDeleteProduct_NotFound() {
        when(productRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(ResultNotFoundException.class, () -> productService.deleteProduct(1L));
    }

    @Test
    public void testGetProductsPaginatedAndSorted() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Order.desc("id")));
        Page<Product> page = new PageImpl<>(List.of(product), pageable, 1);
        when(productRepository.findAll(pageable)).thenReturn(page);

        Page<ProductDto> productDtos = productService.getProductsPaginatedAndSorted(pageable);

        assertEquals(1, productDtos.getTotalElements());
        assertEquals("Product1", productDtos.getContent().get(0).getName());
    }

    @Test
    public void testFindProductsWithSorting() {
        Sort sort = Sort.by(Sort.Direction.DESC, "name");
        List<Product> products = List.of(product);
        when(productRepository.findAll(sort)).thenReturn(products);

        List<ProductDto> productDtos = productService.findProductsWithSorting("name");

        assertEquals(1, productDtos.size());
        assertEquals("Product1", productDtos.get(0).getName());
    }

    @Test
    public void testFindProductsWithPagination() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Product> page = new PageImpl<>(List.of(product), pageable, 1);
        when(productRepository.findAll(pageable)).thenReturn(page);

        Page<ProductDto> productDtos = productService.findProductsWithPagination(0, 10);

        assertEquals(1, productDtos.getTotalElements());
        assertEquals("Product1", productDtos.getContent().get(0).getName());
    }

    @Test
    public void testFindProductsWithPaginationAndSorting() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "name"));
        Page<Product> page = new PageImpl<>(List.of(product), pageable, 1);
        when(productRepository.findAll(pageable)).thenReturn(page);

        Page<ProductDto> productDtos = productService.findProductsWithPaginationAndSorting(0, 10, "name");

        assertEquals(1, productDtos.getTotalElements());
        assertEquals("Product1", productDtos.getContent().get(0).getName());
    }

    @Test
    public void testGetProductByName_Success() {
        // Given
        when(productRepository.findByName("Product1")).thenReturn(Optional.of(product));

        // When
        ProductDto retrievedProductDto = productService.getProductByName("Product1");

        // Then
        assertEquals(1L, retrievedProductDto.getId());
        assertEquals("Product1", retrievedProductDto.getName());
    }

    @Test
    public void testGetProductByName_NotFound() {
        // Given
        when(productRepository.findByName("Product1")).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResultNotFoundException.class, () -> productService.getProductByName("Product1"));
    }


    @Test
    public void testGetAllProductsSortedByIdDesc() {
        // Given
        Product product1 = new Product("Product1", 10, 100.0, null);
        product1.setId(2L);
        Product product2 = new Product("Product2", 5, 200.0, null);
        product2.setId(1L);
        List<Product> products = List.of(product1, product2);

        when(productRepository.findAllSortedByIdDesc()).thenReturn(products);

        // When
        List<ProductDto> productDtos = productService.getAllProductsSortedByIdDesc();

        // Then
        assertEquals(2, productDtos.size());
        assertEquals("Product1", productDtos.get(0).getName()); // Product with ID 2 should come first
        assertEquals("Product2", productDtos.get(1).getName()); // Product with ID 1 should come second
    }

}
