package ru.practicum.user.mapper;

import ru.practicum.user.dto.UserDtoIn;
import ru.practicum.user.dto.UserDtoOut;
import ru.practicum.user.dto.UserDtoWithFollowers;
import ru.practicum.user.model.User;

import java.util.stream.Collectors;

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

    public static UserDtoWithFollowers toUserFollowers(User user) {
        return UserDtoWithFollowers.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .followers(user.getFollower().stream().map(UserMapper::toUserDtoOut).collect(Collectors.toList()))
                .build();
    }
}
