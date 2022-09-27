package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.security.auth.login.AccountNotFoundException;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<List<User>> getUsers() {
        return ResponseEntity.ok(userService.getUsers());
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserFromId(@PathVariable long id) {
        try {
            return ResponseEntity.ok(userService.getUserFromId(id));
        } catch (AccountNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/{id}/friends")
    public ResponseEntity<List<User>> getFriends(@PathVariable long id) {
        return ResponseEntity.ok(userService.getFriends(id));
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public ResponseEntity<List<User>> getListOfMutualFriends(@PathVariable long id, @PathVariable long otherId) {
        return ResponseEntity.ok(userService.getListOfMutualFriends(id, otherId));
    }

    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody User user) {
        try {
            return ResponseEntity.ok(userService.createUser(user));
        } catch (ValidationException e) {
            log.warn("Исключение! ValidationException User: {}", e.getMessage());
            return ResponseEntity.badRequest().body(user);
        }
    }

    @PutMapping
    public ResponseEntity<User> updateUser(@RequestBody User user) {
        try {
            return ResponseEntity.ok(userService.updateUser(user));
        } catch (ValidationException e) {
            log.warn("Исключение! ValidationException User: {}", e.getMessage());
            return ResponseEntity.badRequest().body(user);
        } catch (AccountNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}/friends/{friendId}")
    public ResponseEntity<User> addFriend(@PathVariable long id, @PathVariable long friendId) {
        try {
            userService.addFriend(id, friendId);
            return ResponseEntity.ok(userService.getUserFromId(friendId));
        } catch (AccountNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public ResponseEntity<User> deleteFriend(@PathVariable long id, @PathVariable long friendId) {
        try {
            User delete = userService.getUserFromId(friendId);
            userService.deleteFriend(id, friendId);
            return ResponseEntity.ok(delete);
        } catch (AccountNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<User> deleteUserById(@PathVariable long id) {
        try {
            return ResponseEntity.ok(userService.deleteUserById(id));
        } catch (AccountNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
