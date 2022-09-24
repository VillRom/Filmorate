package ru.yandex.practicum.filmorate.storage;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component("UserDb")
public class UserDbStorage implements UserStorage{
    private final JdbcTemplate jdbcTemplate;

    public UserDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public User createUser(User user) {
        String sql = "INSERT INTO users (email, login, name, birthday_date)" + "VALUES (?, ?, ?, ?)";
        jdbcTemplate.update(sql, user.getEmail(), user.getLogin(), user.getName(), user.getBirthday());
        user.setId(getUserMaxId());
        return user;
    }

    private Long getUserMaxId() {
        return jdbcTemplate.queryForObject("SELECT MAX(user_id) FROM users", Long.class);
    }

    @Override
    public void addFriend(long id, long friendId) {
        String sql = "INSERT INTO friends (user_id, friends_id, status)" + "VALUES (?, ?, ?)";
        jdbcTemplate.update(sql, id, friendId, false);
    }

    @Override
    public Set<Long> getSetListFriends(long id) {
        SqlRowSet userRows = jdbcTemplate.queryForRowSet("select friends_id from friends where user_id = ? ", id);
        Set<Long> listFriends = new HashSet<>();
        while (userRows.next()){
            listFriends.add(userRows.getLong(1));
        }
        return listFriends;
    }

    @Override
    public void deleteFriend(long id, long friendId) {
        String sql = "DELETE FROM friends WHERE user_id = ? AND friends_id = ?";
        jdbcTemplate.update(sql, id, friendId);
    }

    @Override
    public User updateUser(User user) {
        String sql = "UPDATE users SET " + "email = ?, login = ?, name = ?, birthday_date = ?" + "where user_id = ?";
        jdbcTemplate.update(sql,user.getEmail(), user.getLogin(), user.getName(), user.getBirthday(), user.getId());
        return user;
    }

    @Override
    public void deleteUser(int idUser) {
        String sql = "DELETE FROM users WHERE user_id = ?";
        jdbcTemplate.update(sql, idUser);
    }

    @Override
    public List<User> getUsers() {
        String sql = "SELECT * FROM users";
        return jdbcTemplate.query(sql, this::mapRowToUser);
    }

    @Override
    public User getUserFromId(long userId) {
        SqlRowSet userRows = jdbcTemplate.queryForRowSet("select * from users where user_id = ?", userId);
        if(userRows.next()) {
            User user = new User(
                    userRows.getLong("user_id"),
                    userRows.getString("email"),
                    userRows.getString("login"),
                    userRows.getString("name"),
                    userRows.getDate("birthday_date").toLocalDate());
            return user;
        } else {
            return null;
        }
    }

    private User mapRowToUser(ResultSet resultSet, int rowNum) throws SQLException{
        return User.builder()
                .id(resultSet.getLong("user_id"))
                .email(resultSet.getString("email"))
                .login(resultSet.getString("login"))
                .name(resultSet.getString("name"))
                .birthday(resultSet.getDate("birthday_date").toLocalDate())
                .build();
    }
}
