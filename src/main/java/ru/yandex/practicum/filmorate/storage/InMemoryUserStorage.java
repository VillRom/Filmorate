package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import validation.Validation;

import javax.security.auth.login.AccountNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class InMemoryUserStorage implements UserStorage{

    private final Validation validation = new Validation();

    private long userId = 1;

    private final Map<Long, User> users = new HashMap<>();

    @Override
    public ResponseEntity<User> createUser(User user) {
        try {
            if (user.getName().isEmpty()) {
                user.setName(user.getLogin());
            }
            validation.validationUser(user);
            user.setId(userId);
            userId++;
            users.put(user.getId(), user);
            log.info("Добавлен user: {}", user.toString());
        } catch (ValidationException e) {
            log.warn("Исключение! ValidationException User: {}", e.getMessage());
            return ResponseEntity.badRequest().body(user);
        }
        return ResponseEntity.ok(user);
    }

    @Override
    public ResponseEntity<User> updateUser(User user) {
        try {
            if (users.containsKey(user.getId())) {
                validation.validationUser(user);
                if (user.getName().isEmpty()) {
                    user.setName(user.getLogin());
                }
                if (user.getId() < 0) {
                    throw new AccountNotFoundException();
                }
                users.put(user.getId(), user);
                log.info("Обновлен пользователь user: {}", users.get(user.getId()));
            } else {
                throw new AccountNotFoundException();
            }
        } catch (ValidationException e) {
            log.warn("Исключение! ValidationException User: {}", e.getMessage());
            return ResponseEntity.badRequest().body(user);
        } catch (AccountNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(user);
    }

    @Override
    public void deleteUser(int idUser) {
        users.remove(idUser);
    }

    @Override
    public List<User> getUsers() {
        return new ArrayList<>(users.values());
    }
    @Override
    public User getUserFromId(long userId) throws AccountNotFoundException {
        if (!users.containsKey(userId) || userId <= 0) {
            throw new AccountNotFoundException();
        }
        return users.get(userId);
    }
}
