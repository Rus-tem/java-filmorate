package ru.yandex.practicum.filmorate.storage;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.MPA;

import java.util.Collection;
import java.util.Optional;


@Repository
@Primary
public class MpaDbStorage extends BaseStorage {


    public MpaDbStorage(JdbcTemplate jdbc, @Qualifier("mpaMapper") RowMapper<MPA> mapper) {
        super(jdbc, mapper);
    }

    private static final String FIND_ALL_MPA = "SELECT * FROM mpa";
    private static final String FIND_MPA = "SELECT * FROM mpa WHERE mpa_id = ?";

    // Получение списка всех MPA
    public Collection<MPA> getAllMpa() {
        return findMany(FIND_ALL_MPA);
    }

    // Получение MPA по ID
    public Optional<MPA> getMpa(long id) {
        return findOne(FIND_MPA, id);
    }

}
