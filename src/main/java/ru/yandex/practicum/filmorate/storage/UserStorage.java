package ru.yandex.practicum.filmorate.storage;

import org.springframework.http.ResponseEntity;
import ru.yandex.practicum.filmorate.model.User;

import javax.security.auth.login.AccountNotFoundException;
import java.util.List;

public interface UserStorage {
    public ResponseEntity<User> createUser(User user);

    public ResponseEntity<User> updateUser(User user);

    public void deleteUser(int idUser);

    public List<User> getUsers();
    public User getUserFromId(long userId) throws AccountNotFoundException;
}
