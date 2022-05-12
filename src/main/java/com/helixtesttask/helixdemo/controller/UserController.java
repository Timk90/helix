package com.helixtesttask.helixdemo.controller;

import com.helixtesttask.helixdemo.dto.UserDto;
import com.helixtesttask.helixdemo.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.helixtesttask.helixdemo.controller.UserController.USER_API_URL;

@Slf4j
@RestController
@RequestMapping(path = USER_API_URL)
@RequiredArgsConstructor
@Validated
public class UserController {
    public static final String USER_API_URL = "users";

    private final UserService userService;

    @GetMapping
    public List<UserDto> getUsers() {
        log.info("Get users");
        return userService.getAll();
    }

    @GetMapping(path = "/{id}")
    public UserDto getUser(@PathVariable Long id) {
        log.info("Get user by id {}", id);
        return userService.get(id);
    }

    @PostMapping
    public UserDto addUser(@RequestBody UserDto userDto) {
        log.info("Save new user {}", userDto);
        return userService.save(userDto);
    }

    @PutMapping
    public UserDto updateUser(@RequestBody UserDto userDto) {
        log.info("Update user {}", userDto);
        return userService.update(userDto);
    }

    @DeleteMapping(path = "/{id}")
    public void deleteUser(@PathVariable Long id) {
        log.info("Delete user by id {}", id);
        userService.delete(id);
    }
}
