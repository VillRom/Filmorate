package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.DirectorStorage;

import java.util.List;

@Service
@Slf4j
public class DirectorService {

    private final DirectorStorage directorStorage;

    @Autowired
    public DirectorService(DirectorStorage directorStorage) {
        this.directorStorage = directorStorage;
    }

    public List<Director> getAllDirectors() {
        return directorStorage.getAllDirectors();
    }

    public Director getDirectorById(long id) {
        if(directorStorage.getDirector(id) == null) {
            throw new NotFoundException("Режиссер с id = " + id + " не найден");
        }
        return directorStorage.getDirector(id);
    }

    public Director addDirector(Director director) {
        log.info("Добавлен новый режиссер {}", director.getName());
        return directorStorage.createDirector(director);
    }

    public Director updateDirector(Director director) {
        if(directorStorage.getDirector(director.getId()) == null) {
            throw new NotFoundException("Режиссер с id = " + director.getId() + " не найден");
        }
        log.info("Изменена запись о режиссере с id = {}", director.getId());
        return directorStorage.updateDirectorById(director);
    }

    public void deleteDirectorById(long id) {
        if(directorStorage.getDirector(id) == null) {
            throw new NotFoundException("Режиссер с id = " + id + " не найден");
        }
        directorStorage.deleteDirector(id);
        log.info("Из базы данных удален режиссер с id = {}", id);
    }
}
