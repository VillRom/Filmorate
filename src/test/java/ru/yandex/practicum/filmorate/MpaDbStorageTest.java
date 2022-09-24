package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.MpaDbStorage;

import java.util.List;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class MpaDbStorageTest {
    private final MpaDbStorage mpaDbStorage;
    Mpa mpa = new Mpa(2, "PG");

    @Test
    public void getMpaTest() {
        Mpa mpaTest = mpaDbStorage.getMpa(2);
        Assertions.assertEquals(2, mpaTest.getId(), "Идентификатор не совпадает");
        Assertions.assertEquals(mpa, mpaTest, "Объекты не совпадают");
    }

    @Test
    public void getAllMpaTest() {
        List<Mpa> rates = mpaDbStorage.getAllMpa();
        Assertions.assertEquals(5, rates.size(), "Размер списка не равен 6");
        Assertions.assertEquals(mpa, rates.get(1), "Объекты не совпадают");
    }
}
