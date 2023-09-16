package ru.practicum.categories.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
public class CategoryDtoIn {
    @Size(min = 1, max = 50)
    @NotNull
    @NotBlank
    private String name;
}
