package Product_Management.Springboot_1.Service;

import Product_Management.Springboot_1.Exception.ResultNotFoundException;
import Product_Management.Springboot_1.Repository.CategoryRepository;
import Product_Management.Springboot_1.dto.CategoryDto;
import Product_Management.Springboot_1.entity.Category;
import Product_Management.Springboot_1.mapper.CategoryMapper;
import Product_Management.Springboot_1.service.CategoryServiceImpl;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CategoryServiceImplTest {

    @Mock
    private CategoryRepository categoryRepository;


    @InjectMocks
    private CategoryServiceImpl categoryService;

    @Mock
    private CategoryMapper categoryMapper;


    private Category category;
    private CategoryDto categoryDto;

    @BeforeEach
    void setUp() {
        category = new Category();
        category.setId(1L);
        category.setName("Electronics");

        categoryDto = new CategoryDto();
        categoryDto.setId(1L);
        categoryDto.setName("Electronics");
    }

    @Test
    void testCreateCategory() {
        when(categoryRepository.save(any(Category.class))).thenReturn(category);

        CategoryDto savedCategoryDto = categoryService.createCategory(categoryDto);

        assertNotNull(savedCategoryDto);
        assertEquals("Electronics", savedCategoryDto.getName());
        assertEquals(1L, savedCategoryDto.getId());
        verify(categoryRepository, times(1)).save(any(Category.class));
    }

    @Test
    void testGetCategoryById() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));

        CategoryDto foundCategoryDto = categoryService.getCategoryById(1L);

        assertNotNull(foundCategoryDto);
        assertEquals("Electronics", foundCategoryDto.getName());
        assertEquals(1L, foundCategoryDto.getId());
        verify(categoryRepository, times(1)).findById(1L);
    }

    @Test
    void testGetCategoryById_NotFound() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResultNotFoundException.class, () -> {
            categoryService.getCategoryById(1L);
        });

        verify(categoryRepository, times(1)).findById(1L);
    }

    @Test
    void testDeleteCategory() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));

        categoryService.deleteCategory(1L);

        verify(categoryRepository, times(1)).findById(1L);
        verify(categoryRepository, times(1)).deleteById(1L);
        verifyNoMoreInteractions(categoryRepository);
    }

    @Test
    void testUpdateCategory() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(categoryRepository.save(any(Category.class))).thenReturn(category);

        CategoryDto updatedCategoryDto = categoryService.updateCategory(1L, categoryDto);

        assertNotNull(updatedCategoryDto);
        assertEquals("Electronics", updatedCategoryDto.getName());
        verify(categoryRepository, times(1)).findById(1L);
        verify(categoryRepository, times(1)).save(any(Category.class));
        verifyNoMoreInteractions(categoryRepository);
    }

    @Test
    void testGetCategoriesPaginatedAndSorted() {
        Pageable pageable = PageRequest.of(0, 10);
        List<Category> categories = new ArrayList<>();
        categories.add(category);  // Add your `category` mock object
        Page<Category> categoryPage = new PageImpl<>(categories);

        when(categoryRepository.findAll(pageable)).thenReturn(categoryPage);

        Page<CategoryDto> resultPage = categoryService.getCategoriesPaginatedAndSorted(pageable);

        assertNotNull(resultPage);
        assertEquals(1, resultPage.getTotalElements());
        assertEquals(1L, resultPage.getContent().get(0).getId());
        assertEquals("Electronics", resultPage.getContent().get(0).getName());

        verify(categoryRepository, times(1)).findAll(pageable);
        verifyNoMoreInteractions(categoryRepository);
    }

    @Test
    void testFindCategoryWithSorting() {
        List<Category> categories = new ArrayList<>();
        categories.add(category);  // Add your `category` mock object

        when(categoryRepository.findAll(Sort.by(Sort.Direction.DESC, "name"))).thenReturn(categories);

        List<CategoryDto> sortedCategories = categoryService.findCategoryWithSorting("name");

        assertNotNull(sortedCategories);
        assertEquals(1, sortedCategories.size());
        assertEquals(1L, sortedCategories.get(0).getId());
        assertEquals("Electronics", sortedCategories.get(0).getName());

        verify(categoryRepository, times(1)).findAll(Sort.by(Sort.Direction.DESC, "name"));
        verifyNoMoreInteractions(categoryRepository);
    }

    @Test
    void testFindCategoriesWithPagination() {
        Pageable pageable = PageRequest.of(0, 10);
        List<Category> categories = new ArrayList<>();
        categories.add(category);  // Add your `category` mock object
        Page<Category> categoryPage = new PageImpl<>(categories);

        when(categoryRepository.findAll(pageable)).thenReturn(categoryPage);

        Page<CategoryDto> resultPage = categoryService.findCatogriesWithPagination(0, 10);

        assertNotNull(resultPage);
        assertEquals(1, resultPage.getTotalElements());
        assertEquals(1L, resultPage.getContent().get(0).getId());
        assertEquals("Electronics", resultPage.getContent().get(0).getName());

        verify(categoryRepository, times(1)).findAll(pageable);
        verifyNoMoreInteractions(categoryRepository);
    }

    @Test
    void testFindCategoryWithPaginationAndSorting() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by("name"));
        List<Category> categories = new ArrayList<>();
        categories.add(category);  // Add your `category` mock object
        Page<Category> categoryPage = new PageImpl<>(categories);

        when(categoryRepository.findAll(pageable)).thenReturn(categoryPage);

        Page<CategoryDto> resultPage = categoryService.findCatogryWithPaginationAndSorting(0, 10, "name");

        assertNotNull(resultPage);
        assertEquals(1, resultPage.getTotalElements());
        assertEquals(1L, resultPage.getContent().get(0).getId());
        assertEquals("Electronics", resultPage.getContent().get(0).getName());

        verify(categoryRepository, times(1)).findAll(pageable);
        verifyNoMoreInteractions(categoryRepository);
    }

    @Test
    void testGetCategoryByName() {
        when(categoryRepository.findByName("Electronics")).thenReturn(Optional.of(category));

        CategoryDto foundCategoryDto = categoryService.getCatgoryByName("Electronics");

        assertNotNull(foundCategoryDto);
        assertEquals(1L, foundCategoryDto.getId());
        assertEquals("Electronics", foundCategoryDto.getName());

        verify(categoryRepository, times(1)).findByName("Electronics");
        verifyNoMoreInteractions(categoryRepository);
    }

    @Test
    void testGetCategoryByName_NotFound() {
        when(categoryRepository.findByName("Electronics")).thenReturn(Optional.empty());

        assertThrows(ResultNotFoundException.class, () -> {
            categoryService.getCatgoryByName("Electronics");
        });

        verify(categoryRepository, times(1)).findByName("Electronics");
        verifyNoMoreInteractions(categoryRepository);
    }

    @Test
    void testGetAllCategories() {
        List<Category> categories = new ArrayList<>();
        categories.add(category);  // Use your mock `category` object

        when(categoryRepository.findAll()).thenReturn(categories);

        List<CategoryDto> categoryDtos = categoryService.getAllCategories();

        assertNotNull(categoryDtos);
        assertEquals(1, categoryDtos.size());
        assertEquals(1L, categoryDtos.get(0).getId());
        assertEquals("Electronics", categoryDtos.get(0).getName());

        verify(categoryRepository, times(1)).findAll();
        verifyNoMoreInteractions(categoryRepository);
    }


}
