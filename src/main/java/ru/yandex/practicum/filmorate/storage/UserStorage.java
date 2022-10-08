package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;
import java.util.Collection;
import java.util.List;
import java.util.Set;

public interface UserStorage {
    User createUser(User user);
    User updateUser(User user);
    void deleteUser(long idUser);
    List<User> getUsers();
    User getUserFromId(long userId);
    void addFriend(long id, long friendId);
    Set<Long> getSetListFriends(long id);
    void deleteFriend(long id, long friendId);
    Collection<Long> getRecommendations(Long id, Integer count);
}
