package ru.practicum.event.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.Constant;
import ru.practicum.categories.mapper.CategoryMapper;
import ru.practicum.categories.model.Category;
import ru.practicum.event.dto.EventDtoIn;
import ru.practicum.event.dto.EventDtoOutFull;
import ru.practicum.event.dto.EventDtoOutShort;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.Location;
import ru.practicum.user.dto.UserDtoOutWithoutEmail;
import ru.practicum.user.mapper.UserMapper;
import ru.practicum.user.model.User;

import java.time.LocalDateTime;

@UtilityClass
public class EventMapper {
    public static Event toEntity(EventDtoIn eventDtoIn, Category category, User user) {
        return Event.builder()
                .annotation(eventDtoIn.getAnnotation())
                .category(category)
                .description(eventDtoIn.getDescription())
                .initiator(user)
                .paid(eventDtoIn.getPaid())
                .title(eventDtoIn.getTitle())
                .eventDate(LocalDateTime.parse(eventDtoIn.getEventDate(), Constant.FORMATTER))
                .participantLimit(eventDtoIn.getParticipantLimit())
                .lat(eventDtoIn.getLocation().getLat())
                .lon(eventDtoIn.getLocation().getLon())
                .requestModeration(eventDtoIn.getRequestModeration())
                .build();
    }

    public static EventDtoOutFull toOutFull(Event event) {
        return EventDtoOutFull.builder()
                .id(event.getId())
                .annotation(event.getAnnotation())
                .category(CategoryMapper.toCategoryOut(event.getCategory()))
                .description(event.getDescription())
                .createdOn(event.getCreatedOn())
                .eventDate(event.getEventDate())
                .initiator(UserMapper.toUserDtoOut(event.getInitiator()))
                .paid(event.getPaid())
                .title(event.getTitle())
                .participantLimit(event.getParticipantLimit())
                .location(Location.builder()
                        .lat(event.getLat())
                        .lon(event.getLon())
                        .build())
                .requestModeration(event.getRequestModeration())
                .publishedOn(event.getPublishedOn())
                .state(event.getState())
                .build();
    }

    public static EventDtoOutShort toOutShort(Event event) {
        return EventDtoOutShort.builder()
                .id(event.getId())
                .annotation(event.getAnnotation())
                .category(CategoryMapper.toCategoryOut(event.getCategory()))
                .description(event.getDescription())
                .eventDate(event.getEventDate())
                .initiator(UserDtoOutWithoutEmail.builder()
                        .id(event.getInitiator().getId())
                        .name(event.getInitiator().getName())
                        .build())
                .paid(event.getPaid())
                .title(event.getTitle())
                .location(Location.builder()
                        .lat(event.getLat())
                        .lon(event.getLon())
                        .build())
                .state(event.getState())
                .build();
    }
}
