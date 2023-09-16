package ru.practicum.request.dto;

import lombok.*;
import ru.practicum.request.model.StatusRequest;

import java.time.LocalDateTime;


@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class RequestDto {
    private Long id;

    private Long event;

    private LocalDateTime created;

    private Long requester;

    private StatusRequest status;
}
