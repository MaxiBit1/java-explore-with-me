package ru.practicum.event.dto;

import lombok.*;
import ru.practicum.request.model.StatusRequest;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class EventRequestStatusUpdateDto {

    private List<Long> requestIds;
    private StatusRequest status;
}
