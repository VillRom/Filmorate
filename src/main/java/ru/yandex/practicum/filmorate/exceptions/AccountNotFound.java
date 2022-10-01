package ru.yandex.practicum.filmorate.exceptions;

import javax.security.auth.login.AccountNotFoundException;

public class AccountNotFound extends AccountNotFoundException {
    public AccountNotFound(String msg) {
        super(msg);
    }
}
