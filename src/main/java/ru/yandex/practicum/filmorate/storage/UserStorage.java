package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Set;

public interface UserStorage {
    public User createUser(User user);
    public User updateUser(User user);
    public void deleteUser(int idUser);
    public List<User> getUsers();
    public User getUserFromId(long userId);
    public void addFriend(long id, long friendId);
    public Set<Long> getSetListFriends(long id);
    public void deleteFriend(long id, long friendId);
}
