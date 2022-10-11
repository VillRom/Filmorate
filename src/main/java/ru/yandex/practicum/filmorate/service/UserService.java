package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.FeedEvent;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.EventFeedStorage;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import validation.Validation;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserService {

    private final Validation validation = new Validation();

    private final UserStorage userStorage;

    private final FilmStorage filmStorage;

    private final EventFeedStorage eventFeedStorage;


    @Autowired
    public UserService(@Qualifier("UserDb") UserStorage userStorage, FilmStorage filmStorage,
                       EventFeedStorage eventFeedStorage) {
        this.userStorage = userStorage;
        this.filmStorage = filmStorage;
        this.eventFeedStorage = eventFeedStorage;
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

    public User updateUser(User user) {
        if (userStorage.getUserFromId(user.getId()) != null && user.getId() > 0) {
            validation.validationUser(user);
            if (user.getName().isBlank()) {
                user.setName(user.getLogin());
            }
            userStorage.updateUser(user);
            log.info("Обновлен пользователь user: {}", userStorage.getUserFromId(user.getId()));
        } else {
            throw new NotFoundException("Пользователь с id = " + user.getId() + " не найден");
        }
        return user;
    }

    public List<User> getUsers() {
        return userStorage.getUsers();
    }

    public User getUserFromId(long userId) {
        if (userStorage.getUserFromId(userId) == null) {
            throw new NotFoundException("Пользователь с id = " + userId + " не найден");
        }
        return userStorage.getUserFromId(userId);
    }

    public void addFriend(long id, long idFriend) {
        if(id <= 0 || idFriend <= 0) {
            throw new NotFoundException("Пользователи с id = " + id + " " + idFriend + " не найдены");
        } else {
            eventFeedStorage.addEvent(id, "FRIEND", "ADD", idFriend);
            userStorage.addFriend(id, idFriend);
            log.info("Пользователь с id " + idFriend + " добавлен в друзья пользователя с id " + id);
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
        log.info("Пользователь с id " + friendId + " удален из друзей пользователя с id " + id);
        eventFeedStorage.addEvent(id, "FRIEND", "REMOVE", friendId);
    }

    public User deleteUserById(long id) {
        if (userStorage.getUserFromId(id) == null) {
            throw new NotFoundException("Пользователь с id = " + id + " не найден");
        }
        User user = userStorage.getUserFromId(id);
        userStorage.deleteUser(id);
        log.info("Удален пользователь user: {}", user);
        return user;
    }

    public List<FeedEvent> getEventByUserId(long userId) {
        return  eventFeedStorage.getEventById(userId);
    }

    public Collection<Film> getRecommendations(Long id) {
        if (userStorage.getUserFromId(id) == null) {
            throw new NotFoundException("Пользователь с id = " + id + " не найден");
        }
        List<Long> filmsId = userStorage.getRecommendations(id);
        return filmsId.stream()
                .map(filmStorage::getFilmById)
                .collect(Collectors.toSet());
    }
}
