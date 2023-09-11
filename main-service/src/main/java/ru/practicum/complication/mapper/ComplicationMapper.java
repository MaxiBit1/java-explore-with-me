package ru.practicum.complication.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.complication.dto.ComplicationDtoIn;
import ru.practicum.complication.dto.ComplicationDtoOut;
import ru.practicum.complication.model.Complication;
import ru.practicum.event.dto.EventDtoOutFull;
import ru.practicum.event.model.Event;

import java.util.List;

@UtilityClass
public class ComplicationMapper {

    public static Complication toEntity(ComplicationDtoIn complicationDtoIn, List<Event> eventList) {
        return Complication.builder()
                .title(complicationDtoIn.getTitle())
                .events(eventList)
                .pinned(complicationDtoIn.getPinned())
                .build();
    }

    public static ComplicationDtoOut complicationDtoOut(Complication complication, List<EventDtoOutFull> events) {
        return ComplicationDtoOut.builder()
                .id(complication.getId())
                .pinned(complication.getPinned())
                .title(complication.getTitle())
                .events(events)
                .build();
    }
}
