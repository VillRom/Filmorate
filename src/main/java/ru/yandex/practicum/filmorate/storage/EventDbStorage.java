package ru.yandex.practicum.filmorate.storage;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.FeedEvent;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.util.List;

@Component
public class EventDbStorage implements EventFeedStorage {
    private final JdbcTemplate jdbcTemplate;

    public EventDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<FeedEvent> getEventById(long userId) {
        String sql = "SELECT * FROM feed_event WHERE user_id = ?";
        return jdbcTemplate.query(sql, this::mapRowToEvent, userId);
    }

    @Override
    public void addEvent(long userId, String eventType, String operation, long entityId) {
        String sql = "INSERT INTO feed_event (timestamp, user_id, event_type, operation, entity_id) " +
                "VALUES (?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql, Instant.now().toEpochMilli(), userId, eventType, operation, entityId);
    }

    private FeedEvent mapRowToEvent(ResultSet resultSet, int rowNum) throws SQLException {
        return FeedEvent.builder()
                .eventId(resultSet.getLong(1))
                .timestamp(resultSet.getLong(2))
                .userId(resultSet.getLong(3))
                .eventType(resultSet.getString(4))
                .operation(resultSet.getString(5))
                .entityId(resultSet.getLong(6))
                .build();
    }
}
