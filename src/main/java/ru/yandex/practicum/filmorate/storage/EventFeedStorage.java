package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.FeedEvent;

import java.util.List;

public interface EventFeedStorage {

    // Возвращает список событий пользователя по индетификатору userId
    List<FeedEvent> getEventById (long userId);

    // Добавляем событие, совершенное пользователем userId
    void addEvent(long userId, String eventType, String operation, long entityId);
}
