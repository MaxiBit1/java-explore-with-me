package ru.practicum.categories.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.categories.dto.CategoryDtoIn;
import ru.practicum.categories.dto.CategoryDtoOut;
import ru.practicum.categories.service.CategoryService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping("/admin/categories")
    public ResponseEntity<CategoryDtoOut> saveCategory(@RequestBody @Valid CategoryDtoIn categoryDtoIn) {
        log.info("Category with name: " + categoryDtoIn.getName() + " saved.");
        return new ResponseEntity<>(categoryService.saveCategory(categoryDtoIn), HttpStatus.CREATED);
    }

    @DeleteMapping("/admin/categories/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCategory(@PathVariable("id") long id) {
        log.info("Category with id: " + id + " deleted.");
        categoryService.deleteCategory(id);
    }

    @PatchMapping("/admin/categories/{catId}")
    public ResponseEntity<CategoryDtoOut> updateCategory(@PathVariable("catId") long id,
                                                         @RequestBody @Valid CategoryDtoIn categoryDtoIn) {
        log.info("Category with id: " + id + " updated.");
        return new ResponseEntity<>(categoryService.updateCategory(categoryDtoIn, id), HttpStatus.OK);
    }

    @GetMapping("/categories/{id}")
    public ResponseEntity<CategoryDtoOut> getCategory(@PathVariable("id") long id) {
        log.info("Category with id: " + id + " got.");
        return new ResponseEntity<>(categoryService.getCategory(id), HttpStatus.OK);
    }

    @GetMapping("/categories")
    public ResponseEntity<List<CategoryDtoOut>> getCategories(@RequestParam(value = "from", defaultValue = "0") int from,
                                                              @RequestParam(value = "size", defaultValue = "10") int size) {
        log.info("Get categories from: " + from + " size: " + size);
        return new ResponseEntity<>(categoryService.getCategories(from, size), HttpStatus.OK);
    }
}
