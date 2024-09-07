package Product_Management.Springboot_1.service;

import Product_Management.Springboot_1.Exception.ResultNotFoundException;
import Product_Management.Springboot_1.Repository.CategoryRepository;
import Product_Management.Springboot_1.Repository.ProductRepository;
import Product_Management.Springboot_1.dto.ProductDto;
import Product_Management.Springboot_1.entity.Category;
import Product_Management.Springboot_1.entity.Product;
import Product_Management.Springboot_1.mapper.ProductMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Override
    public ProductDto createProduct(ProductDto productDto, Long categoryId) {
        // Convert DTO to Entity
        Product product = ProductMapper.mapToProduct(productDto, categoryRepository);

        // Find the category by ID and set it
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResultNotFoundException("Category with ID " + categoryId + " not found"));
        product.setCategory(category);

        // Save the product
        Product savedProduct = productRepository.save(product);

        // Convert saved product to DTO
        return ProductMapper.mapToProductDto(savedProduct);
    }

    @Override
    public ProductDto getProductById(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResultNotFoundException("Product with ID " + productId + " not found"));
        return ProductMapper.mapToProductDto(product);
    }

    @Override
    public List<ProductDto> getAllProducts() {
        List<Product> products = productRepository.findAll();
        return products.stream().map(ProductMapper::mapToProductDto)
                .collect(Collectors.toList());
    }

    @Override
    public ProductDto updateProduct(Long productId, ProductDto updateProduct) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResultNotFoundException("Product with ID " + productId + " not found"));

        product.setName(updateProduct.getName());
        product.setQuantity(updateProduct.getQuantity());
        product.setPrice(updateProduct.getPrice());

        Product updatedProduct = productRepository.save(product);
        return ProductMapper.mapToProductDto(updatedProduct);
    }

    @Override
    public void deleteProduct(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResultNotFoundException("Product with ID " + productId + " not found"));
        productRepository.deleteById(productId);
    }

    @Override
    public Page<ProductDto> getProductsPaginatedAndSorted(Pageable pageable) {
        // Apply sorting based on pageable
        return productRepository.findAll(PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by(Sort.Order.desc("id"))))
                .map(ProductMapper::mapToProductDto);
    }

    @Override
    public List<ProductDto> findProductsWithSorting(String field) {
        Sort sort = Sort.by(Sort.Direction.DESC, field);
        List<Product> products = productRepository.findAll(sort);
        return products.stream()
                .map(ProductMapper::mapToProductDto)
                .collect(Collectors.toList());
    }


    @Override
    public Page<ProductDto> findProductsWithPagination(int offset, int pageSize) {
        Pageable pageable = PageRequest.of(offset, pageSize);
        return productRepository.findAll(pageable).map(ProductMapper::mapToProductDto);
    }

    @Override
    public Page<ProductDto> findProductsWithPaginationAndSorting(int offset, int pageSize, String field) {
        Pageable pageable = PageRequest.of(offset, pageSize, Sort.by(Sort.Direction.DESC, field));
        return productRepository.findAll(pageable).map(ProductMapper::mapToProductDto);
    }


    @Override
    public ProductDto getProductByName(String name) {
        Product product = productRepository.findByName(name)
                .orElseThrow(() -> new ResultNotFoundException("Product with name '" + name + "' not found"));
        return ProductMapper.mapToProductDto(product);
    }


    //handle this without view
    @Override
    public List<ProductDto> getAllProductsSortedByIdDesc() {
        // Use the custom query method that ensures descending order
        List<Product> products = productRepository.findAllSortedByIdDesc();
        return products.stream()
                .map(ProductMapper::mapToProductDto)
                .collect(Collectors.toList());
    }
}
