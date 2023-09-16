package ru.practicum.categories.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CategoryDtoOut {
    private Long id;
    private String name;
}
