package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;

@Component("InMemory")
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
    public void deleteFriend(long id, long friendId) {
        users.get(id).getListFriends().remove(friendId);
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

    @Override
    public void addFriend(long id, long friendId) {
        users.get(id).addFriend(friendId);
        users.get(friendId).addFriend(id);
    }

    @Override
    public Set<Long> getSetListFriends(long id) {
        return users.get(id).getListFriends();
    }
}
