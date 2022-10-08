package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.AccountNotFound;
import ru.yandex.practicum.filmorate.model.FeedEvent;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.Collection;
import java.util.List;
import java.util.Set;


@RestController
@Slf4j
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public Collection<User> getUsers() {
        return userService.getUsers();
    }

    @GetMapping("/{id}")
    public User getUserFromId(@PathVariable long id) throws AccountNotFound {
        return userService.getUserFromId(id);
    }

    @GetMapping("/{id}/friends")
    public Collection<User> getFriends(@PathVariable long id) {
        return userService.getFriends(id);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public Collection<User> getListOfMutualFriends(@PathVariable long id, @PathVariable long otherId) {
        return userService.getListOfMutualFriends(id, otherId);
    }

    @PostMapping
    public User createUser(@RequestBody User user) {
        return userService.createUser(user);
    }

    @PutMapping
    public User updateUser(@RequestBody User user) throws AccountNotFound {
        return userService.updateUser(user);
    }

    @PutMapping("/{id}/friends/{friendId}")
    public User addFriend(@PathVariable long id, @PathVariable long friendId) throws AccountNotFound {
        userService.addFriend(id, friendId);
        return userService.getUserFromId(friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public User deleteFriend(@PathVariable long id, @PathVariable long friendId)
            throws AccountNotFound {
        User delete = userService.getUserFromId(friendId);
        userService.deleteFriend(id, friendId);
        return delete;
    }

    @DeleteMapping("/{id}")
    public User deleteUserById(@PathVariable long id) throws AccountNotFound {
        return userService.deleteUserById(id);
    }

    @GetMapping("/{id}/feed")
    public List<FeedEvent> getFeedEventByUserId(@PathVariable Long id) {
        return userService.getEventByUserId(id);
    }

    @GetMapping("{id}/recommendations")
    public Set<Film> getRecommendations(@PathVariable Long id,
                                        @RequestParam(required = false, defaultValue = "10") Integer count) {
        return userService.getRecommendations(id, count);
    }
}
