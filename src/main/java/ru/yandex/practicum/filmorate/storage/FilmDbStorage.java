package ru.yandex.practicum.filmorate.storage;

import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Director;
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
        jdbcTemplate.update(con -> {
            PreparedStatement statement = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            statement.setString(1, film.getName());
            statement.setString(2, film.getDescription());
            statement.setDate(3, Date.valueOf(film.getReleaseDate()));
            statement.setLong(4, film.getDuration());
            statement.setLong(5, film.getMpa().getId());
            return statement;
        }, keyHolder);
        Long idFilm = keyHolder.getKey().longValue();
        film.setId(idFilm);
        if(!film.getGenres().isEmpty()) {
            fillingTableFilmGenre(idFilm, film.getGenres());
        }
        if(!film.getDirectors().isEmpty()) {
            fillingTableFilmDirectors(idFilm, film.getDirectors());
        }

        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        String sql = "UPDATE films SET " +"name = ?, description = ?, release_date = ?, duration = ?, mpa_id = ?" +
                "WHERE id = ?";
        jdbcTemplate.update(sql, film.getName(), film.getDescription(), film.getReleaseDate(), film.getDuration(),
                film.getMpa().getId(), film.getId());
        fillingTableFilmGenre(film.getId(), film.getGenres());
        fillingTableFilmDirectors(film.getId(), film.getDirectors());
        if(!film.getGenres().isEmpty()) {
            film.setGenres(getGenreToFilm(film.getId()));
        }
        if(!film.getDirectors().isEmpty()) {
            film.setDirector(getDirectorToFilm(film.getId()));
        }
        film.setLikes(getLikesToFilm(film.getId()));
        return film;
    }

    @Override
    public void deleteFilm(long idFilm) {
        String sql = "DELETE FROM films WHERE id = ?";
        jdbcTemplate.update(sql, idFilm);
    }

    @Override
    public List<Film> getAllFilms() {
        String sql = "select * from films AS f JOIN mpa AS m " +
                "ON f.mpa_id=m.id";
        return jdbcTemplate.query(sql, this::mapRowToFilm);
    }

    @Override
    public List<Film> getSortedFilmsOrderCount(int count) {
        String sql = "select f.*, m.* from films AS f JOIN mpa AS m ON f.mpa_id=m.id LEFT JOIN likes AS l ON f.id=l.id " +
                "GROUP BY f.id ORDER BY COUNT(l.id) DESC LIMIT ?";
        return jdbcTemplate.query(sql, this::mapRowToFilm, count);
    }

    @Override
    public List<Film> getSortByYearFilmsOrderCount(int count, int year) {
        String sql = "select f.*, m.* from films AS f JOIN mpa AS m ON f.mpa_id=m.id LEFT JOIN likes AS l ON f.id=l.id " +
                "WHERE EXTRACT (YEAR FROM CAST(f.release_date AS date)) = ? " +
                "GROUP BY f.id ORDER BY COUNT(l.id) DESC LIMIT ?";
        return jdbcTemplate.query(sql, this::mapRowToFilm, year, count);
    }

    @Override
    public List<Film> getSortByGenreFilmsOrderCount(int count, int idGenre) {
        String sql = "select f.*, m.* from films AS f JOIN mpa AS m ON f.mpa_id=m.id " +
                "JOIN film_genre AS fg ON f.id=fg.film_id " +
                "LEFT JOIN likes AS l ON f.id=l.id " +
                "WHERE fg.genre_id = ? " +
                "GROUP BY f.id ORDER BY COUNT(l.id) DESC LIMIT ?";
        return jdbcTemplate.query(sql, this::mapRowToFilm, idGenre, count);
    }

    @Override
    public List<Film> getSortByGenreAndYearFilmsOrderCount(int count, int year, int idGenre) {
        String sql = "select f.*, m.* from films AS f JOIN mpa AS m ON f.mpa_id=m.id " +
                "JOIN film_genre AS fg ON f.id=fg.film_id " +
                "LEFT JOIN likes AS l ON f.id=l.id " +
                "WHERE EXTRACT (YEAR FROM CAST(f.release_date AS date)) = ? AND fg.genre_id = ? " +
                "GROUP BY f.id ORDER BY COUNT(l.id) DESC LIMIT ?";
        return jdbcTemplate.query(sql, this::mapRowToFilm, year, idGenre, count);
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
            if(jdbcTemplate.queryForObject("SELECT COUNT(DISTINCT film_id) FROM film_directors",
                    Integer.class) > 0) {
                film.setDirector(getDirectorToFilm(idFilm));
            }
            film.setLikes(getLikesToFilm(idFilm));
            return film;
        } else {
            return null;
        }
    }

    @Override
    public void putLikeToFilm(long idFilm, long idUser) {
        String sql = "INSERT INTO likes (id, user_id) VALUES (?, ?)";
        jdbcTemplate.update(sql, idFilm, idUser);
    }

    @Override
    public void deleteLikeToFilm(long idFilm, long idUser) {
        String sql = "DELETE FROM likes WHERE id = ? AND user_id = ?";
        jdbcTemplate.update(sql, idFilm, idUser);
    }

    @Override
    public List<Film> getSortedFilmsByDirectorOrderYear(long idDirector) {
        String sql = "SELECT f.*, m.* FROM films AS f JOIN film_directors AS fd ON f.id=fd.film_id " +
                "JOIN mpa AS m ON f.mpa_id=m.id " +
                "WHERE fd.director_id = " + idDirector + " Order BY EXTRACT (YEAR FROM CAST(f.release_date AS date))";
        return jdbcTemplate.query(sql, this::mapRowToFilm);
    }

    @Override
    public List<Film> getSortedFilmsByDirectorOrderLikes(long idDirector) {
        String sql = "SELECT f.*, m.*, COUNT(l.id) AS order_likes FROM films AS f JOIN film_directors AS fd ON f.id=fd.film_id " +
                "JOIN mpa AS m ON f.mpa_id=m.id " + "LEFT JOIN likes AS l ON f.id=l.id " +
                "WHERE fd.director_id = " + idDirector + " GROUP BY f.id Order BY order_likes DESC ";
        return jdbcTemplate.query(sql, this::mapRowToFilm);
    }

    @Override
    public List<Film> search(String title, String director) {
        String sql = "SELECT films.*, mpa.* FROM films " +
                "JOIN mpa ON mpa.id = films.mpa_id " +
                "LEFT JOIN film_directors ON films.id = film_directors.film_id " +
                "LEFT JOIN directors ON directors.id = film_directors.director_id " +
                "WHERE LOWER(films.name) LIKE '%' || ? || '%' " +
                "OR LOWER(directors.name) LIKE '%' || ? || '%'";

        return jdbcTemplate.query(sql, this::mapRowToFilm, title, director);
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
        if(jdbcTemplate.queryForObject("SELECT COUNT(DISTINCT film_id) FROM film_directors",
                Integer.class) > 0) {
            film.setDirector(getDirectorToFilm(resultSet.getLong("id")));
        }
        film.setLikes(getLikesToFilm(resultSet.getLong("id")));
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

    private void fillingTableFilmDirectors(Long idFilm, Set<Director> directors) {
        List<Director> idDirectors = new ArrayList<>(directors);
        String sqlDelete = "DELETE FROM film_directors WHERE film_id = ?";
        jdbcTemplate.update(sqlDelete, idFilm);
        String sql = "INSERT INTO film_directors (film_id, director_id)" + " VALUES (?, ?)";
        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                ps.setLong(1, idFilm);
                ps.setLong(2, idDirectors.get(i).getId());
            }

            @Override
            public int getBatchSize() {
                return idDirectors.size();
            }
        });
    }

    private Set<Long> getLikesToFilm(long idFilm) {
        SqlRowSet likesRows = jdbcTemplate.queryForRowSet("SELECT user_id FROM likes WHERE id = ?", idFilm);
        Set<Long> likes = new HashSet<>();
        while (likesRows.next()) {
            likes.add(likesRows.getLong("user_id"));
        }
        return likes;
    }

    private List<Genre> getGenreToFilm(long idFilm) {
        String sql = "SELECT g.id, g.name FROM film_genre AS fg JOIN genre AS g ON fg.genre_id=g.id " +
                "WHERE film_id = " + idFilm;
        return jdbcTemplate.query(sql, this::mapRowToGenreToFilm);
    }

    private List<Director> getDirectorToFilm(long idFilm) {
        String sql = "SELECT d.id, d.name FROM film_directors AS fd JOIN directors AS d ON fd.director_id=d.id " +
                "WHERE film_id = " + idFilm;
        return jdbcTemplate.query(sql, this::mapRowToDirectorToFilm);
    }

    private Genre mapRowToGenreToFilm(ResultSet resultSet, int rowNum) throws SQLException {
        return Genre.builder()
                .id(resultSet.getInt(1))
                .name(resultSet.getString(2))
                .build();
    }

    private Director mapRowToDirectorToFilm(ResultSet resultSet, int i) throws SQLException {
        return  Director.builder()
                .id(resultSet.getLong(1))
                .name(resultSet.getString(2))
                .build();
    }
}
