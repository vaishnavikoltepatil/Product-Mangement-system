package Product_Management.Springboot_1.service;

import Product_Management.Springboot_1.dto.CategoryDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;


public interface CategoryService {

    CategoryDto createCategory(CategoryDto categoryDto);

    CategoryDto getCategoryById(Long categoryId);

    List<CategoryDto> getAllCategories();

    CategoryDto updateCategory(Long categoryId, CategoryDto updateCategory);

    void deleteCategory(Long categoryId);

    Page<CategoryDto> getCategoriesPaginatedAndSorted(Pageable pageable);

    List<CategoryDto> findCategoryWithSorting(String field);

    Page<CategoryDto> findCatogriesWithPagination(int offset, int pageSize);

    Page<CategoryDto> findCatogryWithPaginationAndSorting(int offset, int pageSize, String field);

    CategoryDto getCatgoryByName(String name);



}
