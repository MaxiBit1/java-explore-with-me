package ru.practicum.complication.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class ComplicationDtoIn {
    @NotBlank
    @NotNull
    @Size(min = 1, max = 50)
    private String title;
    private List<Long> events;
    private Boolean pinned;
}
