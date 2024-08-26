package Product_Management.Springboot_1.service;

import Product_Management.Springboot_1.Exception.ResultNotFoundException;
import Product_Management.Springboot_1.Repository.CategoryRepository;
import Product_Management.Springboot_1.dto.CategoryDto;
import Product_Management.Springboot_1.entity.Category;
import Product_Management.Springboot_1.mapper.CategoryMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryServiceImpl implements CategoryService{

    @Autowired
    private CategoryRepository categoryRepository;

    @Override
    public CategoryDto createCategory(CategoryDto categoryDto) {
        Category category = CategoryMapper.mapToCategory(categoryDto);
        Category savedCategory = categoryRepository.save(category);
        return CategoryMapper.mapToCategoryDto(savedCategory);

    }

    @Override
    public CategoryDto getCategoryById(Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResultNotFoundException("Category with ID " + categoryId + " not found"));
        return CategoryMapper.mapToCategoryDto(category);
    }


    @Override
    public List<CategoryDto> getAllCategories() {
        List<Category> categories=categoryRepository.findAll();
        return categories.stream().map(CategoryMapper::mapToCategoryDto)
                .collect(Collectors.toList());
    }

    @Override
    public CategoryDto updateCategory(Long categoryId, CategoryDto updateCategory) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResultNotFoundException("Category with ID " + categoryId + " not found"));

        category.setName(updateCategory.getName());


        Category updatedCategory = categoryRepository.save(category);
        return CategoryMapper.mapToCategoryDto(updatedCategory);
    }


    @Override
    public void deleteCategory(Long categoryId) {
    Category category=categoryRepository.findById(categoryId)
            .orElseThrow(() -> new ResultNotFoundException("Product with Id "+categoryId+"not found"));
       categoryRepository.deleteById(categoryId);

    }

    @Override
    public Page<CategoryDto> getCategoriesPaginatedAndSorted(Pageable pageable) {
        return categoryRepository.findAll(pageable)
                .map(CategoryMapper ::mapToCategoryDto);


    }

    @Override
    public List<CategoryDto> findCategoryWithSorting(String field) {
        return categoryRepository.findAll(Sort.by(Sort.Direction.DESC, field)).stream()
                .map(CategoryMapper::mapToCategoryDto)
                .collect(Collectors.toList());
    }

    @Override
    public Page<CategoryDto> findCatogriesWithPagination(int offset, int pageSize) {
       Pageable pageable= PageRequest.of(offset,pageSize);

        return categoryRepository.findAll(pageable).map(CategoryMapper::mapToCategoryDto);
    }

    @Override
    public Page<CategoryDto> findCatogryWithPaginationAndSorting(int offset, int pageSize, String field) {
        Pageable pageable = PageRequest.of(offset, pageSize).withSort(Sort.by(field));
        return categoryRepository.findAll(pageable).map(CategoryMapper::mapToCategoryDto);

    }

    @Override
    public CategoryDto getCatgoryByName(String name) {
        Category category=categoryRepository.findByName(name)
                .orElseThrow(()-> new ResultNotFoundException("Category with name '" + name + "' not found"));
        return CategoryMapper.mapToCategoryDto(category);

    }

}