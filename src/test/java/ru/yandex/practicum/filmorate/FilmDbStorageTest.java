package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.FilmDbStorage;

import java.time.LocalDate;
import java.util.List;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class FilmDbStorageTest {
    private final JdbcTemplate jdbcTemplate;
    private final FilmDbStorage filmDbStorage;
    protected Film film;

    @BeforeEach
    void initDb() {
        if(jdbcTemplate.queryForObject("SELECT COUNT(id) FROM films", Integer.class) == 0) {
            String sql = "INSERT INTO films (name, description, release_date, duration, mpa_id) VALUES (?, ?, ?, ?, ?)";
            jdbcTemplate.update(sql, "film1", "film1description", LocalDate.now(), 95, 3);
            String sql1 = "INSERT INTO users (email, login, name, birthday_date)" + "VALUES (?, ?, ?, ?)";
            jdbcTemplate.update(sql1, "user@yandex", "user.Login", "userName",
                    LocalDate.now());
        }
        Mpa mpa = new Mpa(1, "Комедия");
        film = new Film(2, "film2", LocalDate.now(),"film2description", 120, mpa);
    }

    @Test
    public void addFilmTest() {
        filmDbStorage.addFilm(film);
        Film film1 = filmDbStorage.getFilmById(2);
        Assertions.assertEquals(2, film1.getId(), "Идентификатор не совпадает");
        Assertions.assertEquals("film2", film1.getName(), "Название фильма не совпадает");
        Assertions.assertEquals(120, film1.getDuration(), "Продолжительность фильма не совпадает");
    }

    @Test
    public void updateFilmTest() {
        film.setDuration(15);
        filmDbStorage.updateFilm(film);
        Film film1 = filmDbStorage.getFilmById(2);
        Assertions.assertEquals(2, film1.getId(), "Идентификатор не совпадает");
        Assertions.assertEquals("film2", film1.getName(), "Название фильма не совпадает");
        Assertions.assertEquals(15, film1.getDuration(), "Продолжительность фильма не совпадает");
    }

    @Test
    public void getFilmsTest() {
        List<Film> filmList = filmDbStorage.getFilms();
        Assertions.assertEquals(2, filmList.size(), "Длина списка не совпадает");
    }

    @Test
    public void getFilmFromIdTest() {
        Film film2 = filmDbStorage.getFilmById(1);
        Assertions.assertEquals(1, film2.getId(), "id фильма не совпадает с ожидаемым");
        Assertions.assertEquals("film1", film2.getName(), "Название фильма не совпадает");
        Assertions.assertEquals(95, film2.getDuration(), "Продолжительность фильма не совпадает");
    }

    @Test
    public void putLikeToFilmTest() {
        filmDbStorage.putLikeToFilm(1,1);
        SqlRowSet likesRows = jdbcTemplate.queryForRowSet("select * from likes where id = ? ", 1);
        if(likesRows.next()) {
            Assertions.assertEquals(1, likesRows.getLong(1), "id фильма не совпадает");
            Assertions.assertEquals(1, likesRows.getLong(2), "id пользователя не совпадает");
        }
    }
}
