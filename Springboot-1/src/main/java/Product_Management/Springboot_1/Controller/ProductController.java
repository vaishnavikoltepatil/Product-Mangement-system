package Product_Management.Springboot_1.Controller;

import Product_Management.Springboot_1.dto.ProductDto;
import Product_Management.Springboot_1.entity.Product;
import Product_Management.Springboot_1.service.ProductService;
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
@RequestMapping("/api/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    @Autowired
    private PagedResourcesAssembler<ProductDto> pagedResourcesAssembler;

    @PostMapping
    public ResponseEntity<ProductDto> createProduct(
            @RequestBody ProductDto productDto) { // categoryId included in request body
        Long categoryId = productDto.getCategoryId(); // Extract categoryId from DTO
        ProductDto createdProduct = productService.createProduct(productDto, categoryId);
        return new ResponseEntity<>(createdProduct, HttpStatus.CREATED);
    }

    @GetMapping("{id}")
    public ResponseEntity<ProductDto> getProductById(@PathVariable("id") Long productId) {
        ProductDto productDto = productService.getProductById(productId);
        return ResponseEntity.ok(productDto);
    }

    @GetMapping
    public ResponseEntity<PagedModel<EntityModel<ProductDto>>> getAllProducts(Pageable pageable) {
        Page<ProductDto> page = productService.getProductsPaginatedAndSorted(pageable);
        PagedModel<EntityModel<ProductDto>> pagedModel = pagedResourcesAssembler.toModel(page,
                productDto -> EntityModel.of(productDto));
        return ResponseEntity.ok(pagedModel);
    }

    @PutMapping("{id}")
    public ResponseEntity<ProductDto> updateProduct(@PathVariable("id") Long productId, @RequestBody ProductDto updatedProduct) {
        ProductDto productDto = productService.updateProduct(productId, updatedProduct);
        return ResponseEntity.ok(productDto);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<String> deleteProduct(@PathVariable("id") Long productId) {
        productService.deleteProduct(productId);
        return ResponseEntity.ok("Product deleted successfully!");
    }

    @GetMapping("/sort/{field}")
    public ResponseEntity<List<ProductDto>> getProductsWithSort(@PathVariable String field) {
        List<ProductDto> sortedProducts = productService.findProductsWithSorting(field);
        return ResponseEntity.ok(sortedProducts);
    }

    @GetMapping("/pagination/{offset}/{pageSize}")
    public ResponseEntity<Page<ProductDto>> getProductsWithPagination(@PathVariable int offset, @PathVariable int pageSize) {
        Page<ProductDto> paginatedProducts = productService.findProductsWithPagination(offset, pageSize);
        return ResponseEntity.ok(paginatedProducts);
    }

    @GetMapping("/paginationAndSort/{offset}/{pageSize}/{field}")
    public ResponseEntity<Page<ProductDto>> getProductsWithPaginationAndSort(@PathVariable int offset, @PathVariable int pageSize, @PathVariable String field) {
        Page<ProductDto> paginatedAndSortedProducts = productService.findProductsWithPaginationAndSorting(offset, pageSize, field);
        return ResponseEntity.ok(paginatedAndSortedProducts);
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<ProductDto> getProductByName(@PathVariable("name") String name) {
        ProductDto productDto = productService.getProductByName(name);
        return ResponseEntity.ok(productDto);
    }
}
