package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;

public interface MpaStorage {

    // Возвращает список всех рейтингов
    List<Mpa> getAllMpa();

    // Возвращает рейтинг по его индетификатору
    Mpa getMpa(Integer id);
}
