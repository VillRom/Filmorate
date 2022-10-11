package ru.yandex.practicum.filmorate.storage;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Director;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

@Component
public class DirectorDbStorage implements DirectorStorage {

    private final JdbcTemplate jdbcTemplate;

    public DirectorDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Director getDirector(long directorId) {
        SqlRowSet directorRows = jdbcTemplate.queryForRowSet("SELECT * FROM directors WHERE id = ?", directorId);
        if(directorRows.next()) {
            Director director = Director.builder()
                    .id(directorRows.getLong(1))
                    .name(directorRows.getString(2))
                    .build();
            return director;
        }
        return null;
    }

    @Override
    public List<Director> getAllDirectors() {
        SqlRowSet directorRows = jdbcTemplate.queryForRowSet("SELECT * FROM directors");
        List<Director> directorList = new ArrayList<>();
        while (directorRows.next()) {
            directorList.add(Director.builder()
                    .id(directorRows.getLong(1))
                    .name(directorRows.getString(2))
                    .build());
        }
        return directorList;
    }

    @Override
    public Director createDirector(Director director) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        String sql = "INSERT INTO directors (name)" + " VALUES (?)";
        jdbcTemplate.update(new PreparedStatementCreator() {
            @Override
            public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
                PreparedStatement statement = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                statement.setString(1, director.getName());
                return statement;
            }
        }, keyHolder);
        director.setId(keyHolder.getKey().longValue());
        return director;
    }

    @Override
    public Director updateDirectorById(Director director) {
        String sql = "UPDATE directors SET " + "name = ?" + "where id = ?";
        jdbcTemplate.update(sql, director.getName(), director.getId());
        return director;
    }

    @Override
    public void deleteDirector(long id) {
        String sql = "DELETE FROM directors WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }
}
