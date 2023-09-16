package ru.practicum.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import ru.practicum.Constant;
import ru.practicum.event.model.Location;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class EventDtoIn {
    @NotBlank
    @Size(min = 20, max = 2000, message = "Длина аннотации должна быть от 20 до 2000.")
    private String annotation;

    private Long category;

    @NotBlank
    @Size(min = 20, max = 7000, message = "Длина полного описания должда быть от 20 до 7000.")
    private String description;
    @NotNull
    @JsonFormat(pattern = Constant.FORMAT)
    private String eventDate;
    @NotNull
    private Location location;
    private Boolean paid;
    @Size(min = 3, max = 120, message = "Длина заголовка от 3 до 120 символов.")
    @NotBlank
    private String title;
    private Integer participantLimit;
    private Boolean requestModeration;
}
