package ru.practicum.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.Constant;
import ru.practicum.categories.dto.CategoryDtoOut;
import ru.practicum.event.model.EventState;
import ru.practicum.event.model.Location;
import ru.practicum.user.dto.UserDtoOutWithoutEmail;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class EventDtoOutShort {
    private String annotation;
    private CategoryDtoOut category;
    private Long confirmedRequests;
    @JsonFormat(pattern = Constant.FORMAT)
    private LocalDateTime eventDate;
    private Long id;
    private String description;
    private UserDtoOutWithoutEmail initiator;
    private Boolean paid;
    private Location location;
    private String title;
    private EventState state;
    private Long views;
}
