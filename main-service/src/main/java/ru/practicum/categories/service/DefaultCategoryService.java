package ru.practicum.categories.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.categories.dto.CategoryDtoIn;
import ru.practicum.categories.dto.CategoryDtoOut;
import ru.practicum.categories.mapper.CategoryMapper;
import ru.practicum.categories.model.Category;
import ru.practicum.categories.repository.CategoryRepository;
import ru.practicum.event.model.Event;
import ru.practicum.event.repository.EventsRepository;
import ru.practicum.exception.model.BadRequestException;
import ru.practicum.exception.model.ConflictException;
import ru.practicum.exception.model.NotFoundException;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class DefaultCategoryService implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final EventsRepository eventsRepository;

    @Override
    public CategoryDtoOut saveCategory(CategoryDtoIn categoryDtoIn) {
        if (checkName(categoryDtoIn)) {
            throw new ConflictException("Категория с таким именем уже существует");
        }
        return CategoryMapper.toCategoryOut(categoryRepository.save(CategoryMapper.toEntity(categoryDtoIn)));
    }

    @Override
    public CategoryDtoOut updateCategory(CategoryDtoIn categoryDtoIn, long id) {
        if (checkNameEvent(categoryDtoIn, id)) {
            throw new ConflictException("Категория с таким именем уже существует");
        }
        Category oldCategory = categoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Категория с id = %d не найдена", id)));
        oldCategory.setName(categoryDtoIn.getName());
        return CategoryMapper.toCategoryOut(categoryRepository.save(oldCategory));
    }

    @Override
    public void deleteCategory(long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Категория с id = %d не найдена", id)));
        boolean check = eventsRepository.findAll().stream()
                .map(Event::getCategory)
                .map(Category::getId)
                .anyMatch(ids -> id == ids);
        if (check) {
            throw new ConflictException("Нельзя удалять категорию");
        }
        categoryRepository.delete(category);

    }

    @Override
    public CategoryDtoOut getCategory(long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Категория с id = %d не найдена", id)));
        return CategoryMapper.toCategoryOut(category);
    }

    @Override
    public List<CategoryDtoOut> getCategories(int from, int size) {
        if (from < 0 && size < 0) {
            throw new BadRequestException("Не может from и size быть меньше 0");
        }
        List<Category> categories = categoryRepository.findAll();
        Pageable pageable = PageRequest.of(from, size);
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), categories.size());
        return new PageImpl<>(categories.subList(start, end), pageable, categories.size()).getContent().stream()
                .map(CategoryMapper::toCategoryOut)
                .collect(Collectors.toList());
    }

    private boolean checkName(CategoryDtoIn categoryDtoIn) {
        return categoryRepository.findAll().stream()
                .map(Category::getName)
                .anyMatch(name -> name.equals(categoryDtoIn.getName()));
    }

    private boolean checkNameEvent(CategoryDtoIn categoryDtoIn, Long ids) {
        return categoryRepository.findAll().stream()
                .anyMatch(category -> category.getName().equals(categoryDtoIn.getName())
                        && !Objects.equals(category.getId(), ids));
    }
}
