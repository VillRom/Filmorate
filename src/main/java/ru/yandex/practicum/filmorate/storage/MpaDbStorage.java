package ru.yandex.practicum.filmorate.storage;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.ArrayList;
import java.util.List;

@Component
public class MpaDbStorage implements MpaStorage{
    private final JdbcTemplate jdbcTemplate;

    public MpaDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Mpa> getAllMpa() {
        SqlRowSet mpaRows = jdbcTemplate.queryForRowSet("SELECT * FROM mpa");
        List<Mpa> listMpa = new ArrayList<>();
        while (mpaRows.next()){
            listMpa.add(new Mpa(mpaRows.getInt(1), mpaRows.getString(2)));
        }
        return listMpa;
    }

    @Override
    public Mpa getMpa(Integer id) {
        SqlRowSet mpaRows = jdbcTemplate.queryForRowSet("SELECT * FROM mpa WHERE id = ?", id);
        if(mpaRows.next()) {
            Mpa mpa = new Mpa(mpaRows.getInt("id"), mpaRows.getString("mpa_name"));
            return mpa;
        }
        return null;
    }
}
