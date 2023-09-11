package ru.practicum.user.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.user.dto.UserDtoIn;
import ru.practicum.user.dto.UserDtoOut;
import ru.practicum.user.service.UserService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/admin/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {
    public final UserService userService;

    @PostMapping
    public ResponseEntity<UserDtoOut> saveUser(@RequestBody @Valid UserDtoIn userDtoIn) {
        log.info("User with name: " + userDtoIn.getName() + " and email :" + userDtoIn.getEmail() + " saved.");
        return new ResponseEntity<>(userService.save(userDtoIn), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<UserDtoOut>> getUsers(@RequestParam(value = "ids", required = false) List<Long> ids,
                                                     @RequestParam(value = "from", defaultValue = "0") int from,
                                                     @RequestParam(value = "size", defaultValue = "10") int size) {
        log.info("Get users from: " + from + " size: " + size);
        return new ResponseEntity<>(userService.getUsers(ids, from, size), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable("id") long id) {
        log.info("User with id: " + id + " deleted");
        userService.deleteUser(id);
    }


}
