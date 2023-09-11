package ru.practicum.complication.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Size;
import java.util.List;

@Getter
@Setter
@Builder
public class UpdateComplicationDtoIn {

    @Size(min = 1, max = 50)
    private String title;
    private List<Long> events;
    private Boolean pinned;
}
