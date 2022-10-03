package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Director;

import java.util.List;

public interface DirectorStorage {
    public Director getDirector(long directorId);
    public List<Director> getAllDirectors();
    public Director createDirector(Director director);
    public Director updateDirectorById(Director director);
    public void deleteDirector(long id);
}
