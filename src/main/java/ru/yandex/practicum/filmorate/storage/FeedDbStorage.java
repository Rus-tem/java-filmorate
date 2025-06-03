package ru.yandex.practicum.filmorate.storage;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Feed;

import java.util.Collection;

@Repository
@Primary
public class FeedDbStorage extends BaseStorage {


    public FeedDbStorage(JdbcTemplate jdbc, @Qualifier("feedMapper") RowMapper<Feed> mapper) {
        super(jdbc, mapper);
    }

    private static final String GET_FEED = """
            select * from feed  WHERE userid = ?;""";

    private static final String CREATE_FEED = """
            INSERT INTO feed(timestamp_id, userid, eventType, operation, entityId) VALUES(?, ?, ?, ?, ?);""";
    private static final String DELETE_FEED = "DELETE FROM feed WHERE userid = ? ";

    // Получение списка событий пользователя
    public Collection<Feed> getFeed(Long userId) {
        return findMany(GET_FEED, userId);
    }

    public void deleteFeed(long id) {
        jdbc.update(DELETE_FEED, id);
    }

    public Feed createFeed(Feed feed) {
        long id = insert(
                CREATE_FEED,
                feed.getTimestamp(),
                feed.getUserId(),
                feed.getEventType(),
                feed.getOperation(),
                feed.getEntityId()
        );
        feed.setEventId(id);
        return feed;
    }


}
