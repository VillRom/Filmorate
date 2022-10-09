package ru.yandex.practicum.filmorate.storage;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.*;
import java.sql.Date;
import java.util.*;

@Component("UserDb")
public class UserDbStorage implements UserStorage {
    private final JdbcTemplate jdbcTemplate;

    public UserDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public User createUser(User user) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        String sql = "INSERT INTO users (email, login, name, birthday_date)" + "VALUES (?, ?, ?, ?)";
        jdbcTemplate.update(new PreparedStatementCreator() {
            @Override
            public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
                PreparedStatement statement = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                statement.setString(1, user.getEmail());
                statement.setString(2, user.getLogin());
                statement.setString(3, user.getName());
                statement.setDate(4, Date.valueOf(user.getBirthday()));
                return statement;
            }
        }, keyHolder);
        user.setId(keyHolder.getKey().longValue());
        return user;
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
        String sql = "UPDATE users SET email = ?, login = ?, name = ?, birthday_date = ? where user_id = ?";
        jdbcTemplate.update(sql,user.getEmail(), user.getLogin(), user.getName(), user.getBirthday(), user.getId());
        return user;
    }

    @Override
    public void deleteUser(long idUser) {
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

    private User mapRowToUser(ResultSet resultSet, int rowNum) throws SQLException {
        return User.builder()
                .id(resultSet.getLong("user_id"))
                .email(resultSet.getString("email"))
                .login(resultSet.getString("login"))
                .name(resultSet.getString("name"))
                .birthday(resultSet.getDate("birthday_date").toLocalDate())
                .build();
    }
    @Override
    public Collection<Long> getRecommendations(Long id) {

        String sql = "SELECT id FROM likes l WHERE l.user_id IN (SELECT u.user_id FROM"
                + " (SELECT l.user_id, COUNT(l.id) CNT FROM likes l, "
                + " (SELECT l.user_id, COUNT(l.id) CNT FROM likes l GROUP BY l.user_id) m"
                + " WHERE l.user_id = m.user_id AND l.id IN (SELECT id FROM likes WHERE user_id = ?)"
                + " AND l.user_id <> ? GROUP BY l.user_id ORDER BY CNT DESC, m.CNT DESC) u LIMIT 1)"
                + " AND l.id NOT IN (SELECT id FROM likes WHERE user_id = ?)";
        List<Long> filmIds = new ArrayList<>();
        SqlRowSet rs = jdbcTemplate.queryForRowSet(sql, id, id, id);
        while (rs.next()) {
            filmIds.add(rs.getLong("id"));
        }
        return filmIds;

    }
}
