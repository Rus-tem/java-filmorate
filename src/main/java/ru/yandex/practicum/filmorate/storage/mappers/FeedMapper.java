package ru.yandex.practicum.filmorate.storage.mappers;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Feed;
import ru.yandex.practicum.filmorate.model.enumFeed.EventType;
import ru.yandex.practicum.filmorate.model.enumFeed.Operation;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class FeedMapper implements RowMapper<Feed> {
    @Override
    public Feed mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        Feed feed = new Feed();
        feed.setTimestamp(resultSet.getLong("timestamp_id"));
        feed.setUserId(resultSet.getLong("userid"));
        String eventTypeString = resultSet.getString("eventType");
        feed.setEventType(EventType.valueOf(eventTypeString));
        String operationString = resultSet.getString("operation");
        feed.setOperation(Operation.valueOf(operationString));
        feed.setEventId(resultSet.getLong("eventId"));
        feed.setEntityId(resultSet.getLong("entityId"));
        return feed;

    }
}
