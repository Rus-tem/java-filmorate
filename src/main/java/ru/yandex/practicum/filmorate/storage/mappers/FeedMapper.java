package ru.yandex.practicum.filmorate.storage.mappers;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Feed;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class FeedMapper implements RowMapper<Feed> {
    @Override
    public Feed mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        Feed feed = new Feed();
        feed.setTimestamp(resultSet.getTimestamp("timestamp_id"));
        feed.setUserId(resultSet.getLong("userid"));
        feed.setEventType(resultSet.getString("eventType"));
        feed.setOperation(resultSet.getString("operation"));
        feed.setEventId(resultSet.getLong("eventId"));
        feed.setEntityId(resultSet.getLong("entityId"));
        return feed;

    }
}
