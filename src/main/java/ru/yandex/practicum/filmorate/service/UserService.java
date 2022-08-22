package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;

import javax.security.auth.login.AccountNotFoundException;
import java.util.ArrayList;
import java.util.List;

@Service
public class UserService {
    private final InMemoryUserStorage userStorage;

    @Autowired
    public UserService(InMemoryUserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public void addFriend(long id, long idFriend) throws AccountNotFoundException {
        if(idFriend <= 0) {
            throw new AccountNotFoundException();
        } else {
            userStorage.getUserFromId(id).addFriend(idFriend);
            userStorage.getUserFromId(idFriend).addFriend(id);
        }
    }

    public List<User> getFriends(long id) throws AccountNotFoundException {
        List<User> listFriends = new ArrayList<>();
        for (long idFriend : userStorage.getUserFromId(id).getListFriends()){
            listFriends.add(userStorage.getUserFromId(idFriend));
        }
        return listFriends;
    }

    public List<User> getListOfMutualFriends(long id, long otherId) throws AccountNotFoundException {
        List<User> mutualFriends = new ArrayList<>();
        if (userStorage.getUserFromId(id).getListFriends() == null || userStorage.getUserFromId(otherId)
                .getListFriends() == null) {
            return null;
        } else {
            for (Long idUser : userStorage.getUserFromId(id).getListFriends()) {
                for (Long idOtherUser : userStorage.getUserFromId(otherId).getListFriends()) {
                    if (idUser.equals(idOtherUser)) {
                        mutualFriends.add(userStorage.getUserFromId(idUser));
                    }
                }
            }
            return mutualFriends;
        }
    }

    public void deleteFriend(long id, long friendId) throws AccountNotFoundException {
        userStorage.getUserFromId(id).getListFriends().remove(friendId);
    }

}
