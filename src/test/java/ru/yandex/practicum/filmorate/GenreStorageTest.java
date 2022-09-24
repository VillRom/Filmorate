package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.GenreDbStorage;

import java.util.List;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class GenreStorageTest {
    private final GenreDbStorage genreDbStorage;
    Genre genre = Genre.builder()
            .id(3)
            .name("Мультфильм")
            .build();

    @Test
    public void getGenreFromIdTest() {
        Genre genreTest = genreDbStorage.getGenreFromId(3);
        Assertions.assertEquals(3, genreTest.getId(), "Идентификатор не совпадает");
        Assertions.assertEquals(genre, genreTest, "Объекты не совпадают");
    }

    @Test
    public void getAllGenreTest() {
        List<Genre> genres = genreDbStorage.getAllGenre();
        Assertions.assertEquals(6, genres.size(), "Размер списка не равен 6");
        Assertions.assertEquals(genre, genres.get(2), "Объекты не совпадают");
    }
}
