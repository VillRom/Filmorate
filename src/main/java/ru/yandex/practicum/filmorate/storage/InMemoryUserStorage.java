package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class InMemoryUserStorage implements UserStorage{
    private long userId = 1;
    private final Map<Long, User> users = new HashMap<>();

    @Override
    public User createUser(User user) {
        user.setId(userId);
        userId++;
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User updateUser(User user) {
        users.put(user.getId(), user);
        return user;
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
    public User getUserFromId(long userId){
        return users.get(userId);
    }
}
