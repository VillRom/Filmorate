package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.AccountNotFound;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.FeedEvent;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.Event;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import validation.Validation;

import java.util.ArrayList;
import java.util.List;
@Slf4j
@Service
public class UserService {
    private final Validation validation = new Validation();
    private final UserStorage userStorage;
    private final Event event;

    @Autowired
    public UserService(@Qualifier("UserDb") UserStorage userStorage, Event event) {
        this.userStorage = userStorage;
        this.event = event;
    }

    public User createUser(User user) {
        if (user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        validation.validationUser(user);
        userStorage.createUser(user);
        log.info("Добавлен user: {}", user);
        return user;
    }

    public User updateUser(User user) throws AccountNotFound {
        if (userStorage.getUserFromId(user.getId()) != null) {
            validation.validationUser(user);
            if (user.getName().isBlank()) {
                user.setName(user.getLogin());
            }
            if (user.getId() < 0) {
                throw new AccountNotFound("Пользователь с id = " + user.getId() + " не найден");
            }
            userStorage.updateUser(user);
            log.info("Обновлен пользователь user: {}", userStorage.getUserFromId(user.getId()));
        } else {
            throw new AccountNotFound("Пользователь с id = " + user.getId() + " не найден");
        }
        return user;
    }

    public List<User> getUsers() {
        return userStorage.getUsers();
    }

    public User getUserFromId(long userId) throws AccountNotFound {
        if (userStorage.getUserFromId(userId) == null) {
            throw new AccountNotFound("Пользователь с id = " + userId + " не найден");
        }
        return userStorage.getUserFromId(userId);
    }

    public void addFriend(long id, long idFriend) throws AccountNotFound {
        if(id <= 0 || idFriend <= 0) {
            throw new AccountNotFound("Пользователи с id = " + id + " " + idFriend + " не найдены");
        } else {
            event.addEvent(id, "FRIEND", "ADD", idFriend);
            userStorage.addFriend(id, idFriend);
        }
    }

    public List<User> getFriends(long id) {
        if (userStorage.getUserFromId(id) == null) {
            throw new NotFoundException("Пользователь с id = " + id + " не найден");
        }
        List<User> listFriends = new ArrayList<>();
        for (long idFriend : userStorage.getSetListFriends(id)){
            listFriends.add(userStorage.getUserFromId(idFriend));
        }
        return listFriends;
    }

    public List<User> getListOfMutualFriends(long id, long otherId) {
        List<User> mutualFriends = new ArrayList<>();
        if (userStorage.getUserFromId(id).getListFriends() == null || userStorage.getUserFromId(otherId)
                .getListFriends() == null) {
            return null;
        } else {
            for (Long idUser : userStorage.getSetListFriends(id)) {
                for (Long idOtherUser : userStorage.getSetListFriends(otherId)) {
                    if (idUser.equals(idOtherUser)) {
                        mutualFriends.add(userStorage.getUserFromId(idUser));
                    }
                }
            }
            return mutualFriends;
        }
    }

    public void deleteFriend(long id, long friendId) {
        userStorage.deleteFriend(id, friendId);
        event.addEvent(id, "FRIEND", "REMOVE", friendId);
    }

    public User deleteUserById(long id) throws AccountNotFound {
        if (userStorage.getUserFromId(id) == null) {
            throw new AccountNotFound("Пользователь с id = " + id + " не найден");
        }
        User user = userStorage.getUserFromId(id);
        userStorage.deleteUser(id);
        log.info("Удален пользователь user: {}", user);
        return user;
    }

    public List<FeedEvent> getEventByUserId(long userId) {
        return  event.getEventById(userId);
    }
}
