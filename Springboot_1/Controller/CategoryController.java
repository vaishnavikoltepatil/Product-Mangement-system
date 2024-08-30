package Product_Management.Springboot_1.Controller;

import Product_Management.Springboot_1.dto.CategoryDto;
import Product_Management.Springboot_1.service.CategoryService;

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
@RequestMapping("/api/categories")
public class CategoryController {


    @Autowired
    private CategoryService categoryService;

    @Autowired
    private PagedResourcesAssembler<CategoryDto> pagedResourcesAssembler;

    @PostMapping
    public ResponseEntity<CategoryDto> createCategory(@RequestBody CategoryDto categoryDto) {
        CategoryDto savedProduct = categoryService.createCategory(categoryDto);
        return new ResponseEntity<>(savedProduct, HttpStatus.CREATED);
    }

    @GetMapping("{id}")
    public ResponseEntity<CategoryDto> getCategoryById(@PathVariable("id") Long categoryId) {
        CategoryDto categoryDto = categoryService.getCategoryById(categoryId);
        return ResponseEntity.ok(categoryDto);
    }

    @GetMapping
    public ResponseEntity<PagedModel<EntityModel<CategoryDto>>> getAllcategores(Pageable pageable) {
        Page<CategoryDto> page = categoryService.getCategoriesPaginatedAndSorted(pageable);
        PagedModel<EntityModel<CategoryDto>> pagedModel = pagedResourcesAssembler.toModel(page,
               categoryDto -> EntityModel.of(categoryDto));
        return ResponseEntity.ok(pagedModel);
    }

    @PutMapping("{id}")
    public ResponseEntity<CategoryDto> updateCategory(@PathVariable("id") Long categoryId, @RequestBody CategoryDto updatedCategory) {
        CategoryDto categoryDto = categoryService.updateCategory(categoryId, updatedCategory);
        return ResponseEntity.ok(categoryDto);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<String> deleteCategory(@PathVariable("id") Long categoryId) {
        categoryService.deleteCategory(categoryId);
        return ResponseEntity.ok("Categories deleted successfully!");
    }

    @GetMapping("/sort/{field}")
    public ResponseEntity<List<CategoryDto>> getCategoryWithSort(@PathVariable String field) {
        List<CategoryDto> sortedCategory = categoryService.findCategoryWithSorting(field);
        return ResponseEntity.ok(sortedCategory);
    }

    @GetMapping("/pagination/{offset}/{pageSize}")
    public ResponseEntity<Page<CategoryDto>> getCategoriesWithPagination(@PathVariable int offset, @PathVariable int pageSize) {
        Page<CategoryDto> paginatedCategory = categoryService.findCatogriesWithPagination(offset, pageSize);
        return ResponseEntity.ok(paginatedCategory);
    }

    @GetMapping("/paginationAndSort/{offset}/{pageSize}/{field}")
    public ResponseEntity<Page<CategoryDto>> getCategoryWithPaginationAndSort(@PathVariable int offset, @PathVariable int pageSize, @PathVariable String field) {
        Page<CategoryDto> paginatedAndSortedCategory = categoryService.findCatogryWithPaginationAndSorting(offset, pageSize, field);
        return ResponseEntity.ok(paginatedAndSortedCategory);
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<CategoryDto> getCategoryByName(@PathVariable("name") String name) {
        CategoryDto categoryDto = categoryService.getCatgoryByName(name);
        return ResponseEntity.ok(categoryDto);
    }


}
