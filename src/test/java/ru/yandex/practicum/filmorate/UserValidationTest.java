package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import ru.yandex.practicum.filmorate.model.User;
import validation.Validation;

import java.time.LocalDate;

public class UserValidationTest {
    private final Validation validation = new Validation();
    User user;

    @BeforeEach
    public void createTestUser() {
        user = new User("email@yandex.ru", "Login", "Name", LocalDate.of(2005,05,
                15));
    }

    @Test
    public void ValidateEmptyEmailUser() {
        user.setEmail("");
        final RuntimeException exception = Assertions.assertThrows(RuntimeException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                validation.validationUser(user);
            }
        });
        Assertions.assertEquals("Почта пустая или не указан символ - @",
                exception.getMessage(), "сообщение об исключении не совпало");
    }

    @Test
    public void ValidateNonSymbolEmailUser() {
        user.setEmail("emailyandex.ru");
        final RuntimeException exception = Assertions.assertThrows(RuntimeException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                validation.validationUser(user);
            }
        });
        Assertions.assertEquals("Почта пустая или не указан символ - @",
                exception.getMessage(), "сообщение об исключении не совпало");
    }

    @Test
    public void ValidateEmptyLoginUser() {
        user.setLogin("");
        final RuntimeException exception = Assertions.assertThrows(RuntimeException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                validation.validationUser(user);
            }
        });
        Assertions.assertEquals("Логин пустой или содержит пробелы",
                exception.getMessage(), "сообщение об исключении не совпало");
    }

    @Test
    public void ValidateLoginWithBlankUser() {
        user.setLogin("Lo gin");
        final RuntimeException exception = Assertions.assertThrows(RuntimeException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                validation.validationUser(user);
            }
        });
        Assertions.assertEquals("Логин пустой или содержит пробелы",
                exception.getMessage(), "сообщение об исключении не совпало");
    }

    @Test
    public void ValidateBirthdayInTheFutureUser() {
        user.setBirthday(LocalDate.of(2023,01,01));
        final RuntimeException exception = Assertions.assertThrows(RuntimeException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                validation.validationUser(user);
            }
        });
        Assertions.assertEquals("Дата рождения не может быть в будущем",
                exception.getMessage(), "сообщение об исключении не совпало");
    }
}
