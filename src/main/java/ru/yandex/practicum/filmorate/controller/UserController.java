package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import validation.Validation;

import javax.security.auth.login.AccountNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@Slf4j
@RequestMapping("/users")
public class UserController {
    private final Validation validation = new Validation();

    private final Map<Integer, User> users = new HashMap<>();

    @GetMapping
    public ResponseEntity<List<User>> getFilms() {
        return ResponseEntity.ok(new ArrayList<>(users.values()));
    }

    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody User user) {
        try {
            validation.validationUser(user);
            if (user.getName().isEmpty()) {
                user.setName(user.getLogin());
            }
            if (user.getId() < 0) {
                throw new AccountNotFoundException();
            }
            users.put(user.getId(), user);
            log.info("Добавлен user: {}", user.toString());
        } catch (ValidationException e) {
            log.warn("Выпало исключение ValidationException User: {}", e.getMessage());
            return ResponseEntity.badRequest().body(user);
        } catch (AccountNotFoundException e) {
            return ResponseEntity.internalServerError().body(user);
        }
        return ResponseEntity.ok(user);
    }

    @PutMapping
    public ResponseEntity<User> updateUser(@RequestBody User user) {
        try {
            if (users.containsKey(user.getId())) {
                validation.validationUser(user);
                if (!users.get(user.getId()).getLogin().equals(user.getLogin())) {
                    users.get(user.getId()).setLogin(user.getLogin());
                    log.info("Логин пользователя c id {} изменён на {}", user.getId(), user.getLogin());
                }
                if (!users.get(user.getId()).getEmail().equals(user.getEmail())) {
                    users.get(user.getId()).setEmail(user.getEmail());
                    log.info("email пользователя c id {} изменён на {}", user.getId(), user.getEmail());
                }
                if (!users.get(user.getId()).getName().equals(user.getName())) {
                    users.get(user.getId()).setName(user.getName());
                    log.info("имя пользователя c id {} изменено на {}", user.getId(), user.getName());
                }
                if (!users.get(user.getId()).getBirthday().equals(user.getBirthday())) {
                    users.get(user.getId()).setBirthday(user.getBirthday());
                    log.info("дата рождения пользователя c id {} изменено на {}", user.getId(), user.getBirthday());
                }
            } else {
                throw new AccountNotFoundException();
            }
        } catch (ValidationException e) {
            log.warn("Выпало исключение ValidationException User: {}", e.getMessage());
            return ResponseEntity.badRequest().body(user);
        } catch (AccountNotFoundException e) {
            return ResponseEntity.internalServerError().body(user);
        }
        return ResponseEntity.ok(user);
    }
}
