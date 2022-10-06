package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserDbStorage;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserDbStorageTest {
    private final JdbcTemplate jdbcTemplate;
    private final UserDbStorage userDbStorage;
    protected User user;

    @BeforeEach
    private void initDb () {
        if(jdbcTemplate.queryForObject("SELECT COUNT(user_id) FROM users", Integer.class) == 0) {
            String sql = "INSERT INTO users (email, login, name, birthday_date)" + "VALUES (?, ?, ?, ?)";
            jdbcTemplate.update(sql, "user@yandex", "user.Login", "userName",
                    LocalDate.now());
        }
        user = new User(1, "user2@yandex", "user2.Login", "user2Name",
                LocalDate.of(1985, 3, 15));
    }

    @Test
    public void createUserTest() {
        userDbStorage.createUser(user);
        User user1 = userDbStorage.getUserFromId(2);
        Assertions.assertEquals(2, user1.getId(), "Идентификатор не совпадает");
        Assertions.assertEquals("user2@yandex", user1.getEmail(), "Идентификатор не совпадает");
        Assertions.assertEquals("user2.Login", user1.getLogin(), "Идентификатор не совпадает");
        Assertions.assertEquals("user2Name", user1.getName(), "Идентификатор не совпадает");
    }

    @Test
    public void updateUserTest() {
        user.setName("userNameSetTest");
        userDbStorage.updateUser(user);
        User user1 = userDbStorage.getUserFromId(1);
        Assertions.assertEquals(1, user1.getId(), "Идентификатор не совпадает");
        Assertions.assertEquals("user2@yandex", user1.getEmail(), "Идентификатор не совпадает");
        Assertions.assertEquals("user2.Login", user1.getLogin(), "Идентификатор не совпадает");
        Assertions.assertEquals("userNameSetTest", user1.getName(), "Идентификатор не совпадает");
    }

    @Test
    public void getUserFromIdTest() {
        User user = userDbStorage.getUserFromId(1);
        Assertions.assertEquals(1, user.getId(), "Идентификатор не совпадает");

    }

    @Test
    public void getUsersTest() {
        List<User> userList = userDbStorage.getUsers();
        Assertions.assertEquals(2, userList.size(), "Длина списка не совпадает");
    }

    @Test
    public void addFriendTest() {
        String sql = "INSERT INTO users (email, login, name, birthday_date)" + "VALUES (?, ?, ?, ?)";
        jdbcTemplate.update(sql, user.getEmail(), user.getLogin(), user.getName(),
                LocalDate.now());
        userDbStorage.addFriend(1,2);
        SqlRowSet friendRows = jdbcTemplate.queryForRowSet("select * from friends where user_id = ? ", 1);
        if(friendRows.next()) {
            Assertions.assertEquals(2, friendRows.getLong(2), "id друга не совпадает");
            Assertions.assertFalse(friendRows.getBoolean(3));
        }
    }

    @Test
    public void getSetListFriendsTest() {
        Set<Long> listFriends = userDbStorage.getSetListFriends(1);
        Assertions.assertFalse(listFriends.isEmpty(), "Список пустой");
        Assertions.assertEquals(1, listFriends.size(), "Размер списка друзей не совпадает");
        Assertions.assertTrue(listFriends.contains(2L), "Список не содержит id = 2");
    }

    @Test
    public void deleteFriendTest() {
        userDbStorage.deleteFriend(1,2);
        String sql = "SELECT COUNT(user_id) FROM friends";
        Assertions.assertEquals(0, jdbcTemplate.queryForObject(sql, Integer.class),
                "Список друзей не пустой");
    }
}
