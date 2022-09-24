package ru.yandex.practicum.filmorate.storage;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.ArrayList;
import java.util.List;

@Component
public class GenreDbStorage implements GenreStorage {
    private final JdbcTemplate jdbcTemplate;

    public GenreDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Genre> getAllGenre() {
        SqlRowSet genreRows = jdbcTemplate.queryForRowSet("SELECT * FROM genre");
        List<Genre> listGenre = new ArrayList<>();
        while (genreRows.next()){
            listGenre.add(Genre.builder()
                    .id(genreRows.getInt(1))
                    .name(genreRows.getString(2))
                    .build());
        }
        return listGenre;
    }

    @Override
    public Genre getGenreFromId(Integer id) {
        SqlRowSet genreRows = jdbcTemplate.queryForRowSet("SELECT * FROM genre WHERE id = ?", id);
        if(genreRows.next()) {
            Genre genre = Genre.builder()
                    .id(genreRows.getInt(1))
                    .name(genreRows.getString(2))
                    .build();
            return genre;
        }
        return null;
    }
}
