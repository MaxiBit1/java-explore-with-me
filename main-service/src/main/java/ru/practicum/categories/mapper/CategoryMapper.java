package ru.practicum.categories.mapper;

import ru.practicum.categories.dto.CategoryDtoIn;
import ru.practicum.categories.dto.CategoryDtoOut;
import ru.practicum.categories.model.Category;

public class CategoryMapper {

    public static Category toEntity(CategoryDtoIn categoryDtoIn) {
        return Category.builder()
                .name(categoryDtoIn.getName())
                .build();
    }

    public static CategoryDtoOut toCategoryOut(Category category) {
        return CategoryDtoOut.builder()
                .id(category.getId())
                .name(category.getName())
                .build();
    }
}
