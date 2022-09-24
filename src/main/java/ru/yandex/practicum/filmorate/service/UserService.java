package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import validation.Validation;

import javax.security.auth.login.AccountNotFoundException;
import java.util.ArrayList;
import java.util.List;
@Slf4j
@Service
public class UserService {
    private final Validation validation = new Validation();
    private final UserStorage userStorage;

    @Autowired
    public UserService(@Qualifier("UserDb") UserStorage userStorage) {
        this.userStorage = userStorage;
    }
    public User createUser(User user) {
        if (user.getName().isEmpty()) {
            user.setName(user.getLogin());
        }
        validation.validationUser(user);
        userStorage.createUser(user);
        log.info("Добавлен user: {}", user);
        return user;
    }

    public User updateUser(User user) throws AccountNotFoundException {
        if (userStorage.getUserFromId(user.getId()) != null) {
            validation.validationUser(user);
            if (user.getName().isEmpty()) {
                user.setName(user.getLogin());
            }
            if (user.getId() < 0) {
                throw new AccountNotFoundException();
            }
            userStorage.updateUser(user);
            log.info("Обновлен пользователь user: {}", userStorage.getUserFromId(user.getId()));
        } else {
            throw new AccountNotFoundException();
        }
        return user;
    }

    public List<User> getUsers() {
        return userStorage.getUsers();
    }

    public User getUserFromId(long userId) throws AccountNotFoundException {
        if (userStorage.getUserFromId(userId) == null || userId <= 0) {
            throw new AccountNotFoundException();
        }
        return userStorage.getUserFromId(userId);
    }

    public void addFriend(long id, long idFriend) throws AccountNotFoundException {
        if(id <= 0 || idFriend <= 0) {
            throw new AccountNotFoundException();
        } else {
            userStorage.addFriend(id, idFriend);
        }
    }

    public List<User> getFriends(long id) {
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
    }

}
