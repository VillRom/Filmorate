package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Director;

import java.util.List;

public interface DirectorStorage {

    // Возвращаем режиссера по идентификатору
    Director getDirector(long directorId);

    // Возвращаем список всех режиссеров
    List<Director> getAllDirectors();

    // Добавляем режиссера
    Director createDirector(Director director);

    // Обновляем информацию о режиссере
    Director updateDirectorById(Director director);

    // Удаляем режиссера по идентификатору
    void deleteDirector(long id);
}
