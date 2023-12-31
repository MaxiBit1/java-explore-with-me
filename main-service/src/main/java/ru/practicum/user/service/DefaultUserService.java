package ru.practicum.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.exception.model.BadRequestException;
import ru.practicum.exception.model.ConflictException;
import ru.practicum.exception.model.NotFoundException;
import ru.practicum.user.dto.UserDtoIn;
import ru.practicum.user.dto.UserDtoOut;
import ru.practicum.user.dto.UserDtoWithFollowers;
import ru.practicum.user.mapper.UserMapper;
import ru.practicum.user.model.User;
import ru.practicum.user.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class DefaultUserService implements UserService {

    private final UserRepository userRepository;

    @Override
    public UserDtoOut save(UserDtoIn userDtoIn) {
        boolean check = userRepository.findAll().stream()
                .map(User::getName)
                .anyMatch(user -> user.equals(userDtoIn.getName()));
        if (check) {
            throw new ConflictException("Юзер с таким именем есть");
        }
        return UserMapper.toUserDtoOut(userRepository.save(UserMapper.toEntity(userDtoIn)));
    }

    @Override
    public List<UserDtoOut> getUsers(List<Long> ids, int from, int size) {
        if (from < 0 && size < 0) {
            throw new BadRequestException("Не может from и size быть меньше 0");
        }
        List<User> users = userRepository.findAll();
        if (ids != null) {
            List<User> idsUsers = new ArrayList<>();
            for (Long id : ids) {
                Optional<User> userOptional = userRepository.findById(id);
                userOptional.ifPresent(idsUsers::add);
            }
            if (idsUsers.isEmpty()) {
                return new ArrayList<>();
            }
            users = users.stream().filter(idsUsers::contains).collect(Collectors.toList());
        }
        Pageable pageable = PageRequest.of(from, size);
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), users.size());
        return new PageImpl<>(users.subList(start, end), pageable, users.size()).getContent().stream()
                .map(UserMapper::toUserDtoOut)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteUser(Long id) {
        User user = userRepository
                .findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Пользователь с id = %d не найден", id)));
        userRepository.delete(user);
    }

    @Override
    public UserDtoWithFollowers addFollow(Long userId, Long followerId) {
        checkFollower(userId, followerId);

        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException(String.format("Пользователь с id = %d не найден", userId)));
        User follower = userRepository.findById(followerId).orElseThrow(() -> new NotFoundException(String.format("Пользователь с id = %d не найден", followerId)));

        if (user.getFollower().contains(follower)) {
            throw new ConflictException("Пользователь уже подписан");
        }
        user.getFollower().add(follower);
        return UserMapper.toUserFollowers(userRepository.save(user));
    }

    @Override
    public void deleteFollow(Long userId, Long followerId) {
        checkFollower(userId, followerId);

        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException(String.format("Пользователь с id = %d не найден", userId)));
        User follower = userRepository.findById(followerId).orElseThrow(() -> new NotFoundException(String.format("Пользователь с id = %d не найден", followerId)));

        if (!user.getFollower().contains(follower)) {
            throw new ConflictException("Пользователь не подписан");
        }
        user.getFollower().remove(follower);
        userRepository.save(user);
    }

    private void checkFollower(Long userId, Long followerId) {
        if (userId.equals(followerId)) {
            throw new ConflictException("Пользователь не может подписаться на себя же");
        }
    }
}
