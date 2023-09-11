package ru.practicum.categories.dto;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Builder
public class CategoryDtoOut {
    private Long id;
    private String name;
}
