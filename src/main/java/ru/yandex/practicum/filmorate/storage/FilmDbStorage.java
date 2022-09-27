package ru.yandex.practicum.filmorate.storage;

import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component("FilmDb")
public class FilmDbStorage implements FilmStorage{
    private final JdbcTemplate jdbcTemplate;

    public FilmDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Film addFilm(Film film) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        String sql = "INSERT INTO films (name, description, release_date, duration, mpa_id)" +
                "VALUES (?, ?, ?, ?, ?)";
        jdbcTemplate.update(new PreparedStatementCreator() {
            @Override
            public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
                PreparedStatement statement = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                statement.setString(1, film.getName());
                statement.setString(2, film.getDescription());
                statement.setDate(3, Date.valueOf(film.getReleaseDate()));
                statement.setLong(4, film.getDuration());
                statement.setLong(5, film.getMpa().getId());
                return statement;
            }
        }, keyHolder);
        Long idFilm = keyHolder.getKey().longValue();
        film.setId(idFilm);
        if(!film.getGenres().isEmpty()) {
            fillingTableFilmGenre(idFilm, film.getGenres());
        }
        return film;
    }

    private void fillingTableFilmGenre(Long idFilm, Set<Genre> genre) {
        List<Genre> idGenres = new ArrayList<>(genre);
        String sqlDelete = "DELETE FROM film_genre WHERE film_id = ?";
        jdbcTemplate.update(sqlDelete, idFilm);
        String sql = "INSERT INTO film_genre (film_id, genre_id)" + "VALUES (?, ?)";
        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                ps.setLong(1, idFilm);
                ps.setLong(2, idGenres.get(i).getId());
            }

            @Override
            public int getBatchSize() {
                return genre.size();
            }
        });
    }

    @Override
    public Film updateFilm(Film film) {
        String sql = "UPDATE films SET " +"name = ?, description = ?, release_date = ?, duration = ?, mpa_id = ?" +
                "WHERE id = ?";
        jdbcTemplate.update(sql, film.getName(), film.getDescription(), film.getReleaseDate(), film.getDuration(),
                film.getMpa().getId(), film.getId());
        fillingTableFilmGenre(film.getId(), film.getGenres());
        if(!film.getGenres().isEmpty()) {
            film.setGenres(getGenreToFilm(film.getId()));
        }
        return film;
    }

    @Override
    public void deleteFilm(long idFilm) {
        String sql = "DELETE FROM films WHERE id = ?";
        jdbcTemplate.update(sql, idFilm);
    }

    @Override
    public List<Film> getFilms() {
        String sql = "select * from films AS f JOIN mpa AS m " +
                "ON f.mpa_id=m.id";
        return jdbcTemplate.query(sql, this::mapRowToFilm);
    }

    private Film mapRowToFilm(ResultSet resultSet, int i) throws SQLException {
        Mpa mpa = new Mpa(resultSet.getInt("mpa_id"), resultSet.getString("mpa_name"));
        Film film = new Film(
                resultSet.getLong("id"),
                resultSet.getString("name"),
                resultSet.getDate("release_date").toLocalDate(),
                resultSet.getString("description"),
                resultSet.getInt("duration"),
                mpa);
        if(jdbcTemplate.queryForObject("SELECT COUNT(DISTINCT film_id) FROM film_genre", Integer.class) > 0) {
            film.setGenres(getGenreToFilm(resultSet.getLong("id")));
        }
        film.setLikes(getLikesToFilm(resultSet.getLong("id")));
        return film;
    }

    @Override
    public Film getFilmById(long idFilm) {
        SqlRowSet filmRows = jdbcTemplate.queryForRowSet("select * from films AS f JOIN mpa AS m " +
                "ON f.mpa_id=m.id where f.id = ?", idFilm);
        if(filmRows.next()) {
            Mpa mpa = new Mpa(filmRows.getInt("mpa_id"), filmRows.getString("mpa_name"));
            Film film = new Film(
                    filmRows.getLong("id"),
                    filmRows.getString("name"),
                    filmRows.getDate("release_date").toLocalDate(),
                    filmRows.getString("description"),
                    filmRows.getInt("duration"),
                    mpa);
            if(jdbcTemplate.queryForObject("SELECT COUNT(DISTINCT film_id) FROM film_genre", Integer.class) > 0) {
                film.setGenres(getGenreToFilm(idFilm));
            }
            film.setLikes(getLikesToFilm(idFilm));
            return film;
        } else {
            return null;
        }
    }

    private Set<Long> getLikesToFilm(long idFilm) {
        SqlRowSet likesRows = jdbcTemplate.queryForRowSet("SELECT user_id FROM likes WHERE id = ?", idFilm);
        Set<Long> likes = new HashSet<>();
        if(likesRows.next()) {
            likes.add(likesRows.getLong("user_id"));
        }
        return likes;
    }

    @Override
    public void putLikeToFilm(long idFilm, long idUser) {
        String sql = "INSERT INTO likes (id, user_id) VALUES (?, ?)";
        jdbcTemplate.update(sql, idFilm, idUser);
    }

    private List<Genre> getGenreToFilm(long idFilm) {
        String sql = "SELECT g.id, g.name FROM film_genre AS fg JOIN genre AS g ON fg.genre_id=g.id " +
                "WHERE film_id = " + idFilm;
        return jdbcTemplate.query(sql, this::mapRowToGenreToFilm);
    }

    private Genre mapRowToGenreToFilm(ResultSet resultSet, int rowNum) throws SQLException {
        return Genre.builder()
                .id(resultSet.getInt(1))
                .name(resultSet.getString(2))
                .build();
    }
}
