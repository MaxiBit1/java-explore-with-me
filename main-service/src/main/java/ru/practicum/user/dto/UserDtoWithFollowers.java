package ru.practicum.user.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class UserDtoWithFollowers {
    private Long id;
    private String name;
    private String email;
    private List<UserDtoOut> followers;
}
