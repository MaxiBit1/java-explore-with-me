package ru.practicum.user.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.user.dto.UserDtoWithFollowers;
import ru.practicum.user.service.UserService;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Slf4j
public class FollowController {

    private final UserService userService;

    @PostMapping("/{userId}/followers/{followerId}")
    public ResponseEntity<UserDtoWithFollowers> addFollowers(@PathVariable("userId") Long userId,
                                                             @PathVariable("followerId") Long followerId) {
        log.info("Add follower: " + followerId + " userId: " + userId);
        return new ResponseEntity<>(userService.addFollow(userId, followerId), HttpStatus.CREATED);
    }

    @DeleteMapping("/{userId}/followers/{followerId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteFollow(@PathVariable("userId") Long userId,
                             @PathVariable("followerId") Long followerId) {
        log.info("Delete follower: " + followerId + " userId: " + userId);
        userService.deleteFollow(userId, followerId);
    }
}
