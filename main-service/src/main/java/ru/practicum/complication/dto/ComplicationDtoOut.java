package ru.practicum.complication.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.event.dto.EventDtoOutFull;

import java.util.List;

@Getter
@Setter
@Builder
public class ComplicationDtoOut {
    private Long id;
    private String title;
    private List<EventDtoOutFull> events;
    private Boolean pinned;
}
