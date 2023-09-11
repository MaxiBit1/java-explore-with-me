package ru.practicum.user.dto;


import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class UserDtoOut {
    private Long id;
    private String name;
    private String email;
}
