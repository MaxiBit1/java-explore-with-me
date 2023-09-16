package ru.practicum.categories.service;

import ru.practicum.categories.dto.CategoryDtoIn;
import ru.practicum.categories.dto.CategoryDtoOut;

import java.util.List;

public interface CategoryService {
    CategoryDtoOut saveCategory(CategoryDtoIn categoryDtoIn);

    CategoryDtoOut updateCategory(CategoryDtoIn categoryDtoIn, long id);

    void deleteCategory(long id);

    CategoryDtoOut getCategory(long id);

    List<CategoryDtoOut> getCategories(int from, int size);

}
