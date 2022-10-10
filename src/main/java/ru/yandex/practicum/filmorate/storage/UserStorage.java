package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;
import java.util.List;
import java.util.Set;

public interface UserStorage {

    // Добавляем пользователя
    User createUser(User user);

    // Обновляем информацию о пользователе
    User updateUser(User user);

    // Удаляем пользователя по идентификатору
    void deleteUser(long idUser);

    // Возвращаем список всех пользователей
    List<User> getUsers();

    // Возвращаем пользователя по идентификатору
    User getUserFromId(long userId);

    // Добавляем друга пользователя id
    void addFriend(long id, long friendId);

    // Возвращаем список друзей пользователя
    Set<Long> getSetListFriends(long id);

    // Удаляем пользователя friendId из друзей id
    void deleteFriend(long id, long friendId);

    // Возвращаем список индетификаторов рекомендуемых фильмов пользователю id
    List<Long> getRecommendations(Long id);
}
