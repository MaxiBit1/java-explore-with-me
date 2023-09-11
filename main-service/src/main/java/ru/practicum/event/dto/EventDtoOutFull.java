package ru.practicum.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.Constant;
import ru.practicum.categories.dto.CategoryDtoOut;
import ru.practicum.event.model.EventState;
import ru.practicum.event.model.Location;
import ru.practicum.user.dto.UserDtoOut;

import java.time.LocalDateTime;


@Getter
@Setter
@Builder
public class EventDtoOutFull {
    private Long id;
    private String annotation;
    private CategoryDtoOut category;
    @JsonFormat(pattern = Constant.FORMAT)
    private LocalDateTime createdOn;
    private String description;
    @JsonFormat(pattern = Constant.FORMAT)
    private LocalDateTime eventDate;
    private UserDtoOut initiator;
    private Boolean paid;
    private String title;
    private Integer participantLimit;
    private Location location;
    private Boolean requestModeration;
    @JsonFormat(pattern = Constant.FORMAT)
    private LocalDateTime publishedOn;
    private EventState state;
    private Long views;
    private Long confirmedRequests;
}
