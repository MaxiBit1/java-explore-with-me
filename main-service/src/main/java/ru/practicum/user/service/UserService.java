package ru.practicum.user.service;

import ru.practicum.user.dto.UserDtoIn;
import ru.practicum.user.dto.UserDtoOut;

import java.util.List;

public interface UserService {

    UserDtoOut save(UserDtoIn userDtoIn);

    List<UserDtoOut> getUsers(List<Long> ids, int from, int size);

    void deleteUser(Long id);
}
