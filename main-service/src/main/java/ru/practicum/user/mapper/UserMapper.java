package ru.practicum.user.mapper;

import ru.practicum.user.dto.UserDtoIn;
import ru.practicum.user.dto.UserDtoOut;
import ru.practicum.user.model.User;

public class UserMapper {

    public static User toEntity(UserDtoIn userDtoIn) {
        return User.builder()
                .name(userDtoIn.getName())
                .email(userDtoIn.getEmail())
                .build();
    }

    public static UserDtoOut toUserDtoOut(User user) {
        return UserDtoOut.builder()
                .id(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .build();
    }
}
