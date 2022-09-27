package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Mpa;

import javax.security.auth.login.AccountNotFoundException;
import java.util.List;

public interface MpaStorage {
    public List<Mpa> getAllMpa();
    public Mpa getMpa(Integer id) throws AccountNotFoundException;
}
