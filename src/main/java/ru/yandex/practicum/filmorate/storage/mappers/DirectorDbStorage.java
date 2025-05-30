package ru.yandex.practicum.filmorate.storage.mappers;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.BaseStorage;

import java.util.Collection;
import java.util.Optional;
@Repository
@Primary
public class DirectorDbStorage extends BaseStorage {

    public DirectorDbStorage(JdbcTemplate jdbc, @Qualifier("directorMapper") RowMapper<Director> mapper) {
        super(jdbc, mapper);
    }

    private static final String FIND_ALL_DIRECTORS = "SELECT * FROM director";
    private static final String FIND_DIRECTOR = "SELECT * FROM director WHERE director_id = ?";
    private static final String CREATE_DIRECTOR = "INSERT INTO director(director_name) VALUES (?) ;";
    private static final String UPDATE_DIRECTOR = "UPDATE director SET director_name = ? WHERE director_id = ?;";
    private static final String DELETE_DIRECTOR = "DELETE FROM director WHERE director_id = ?";
    // Получение списка всех режиссеров(directors)
    public Collection<Director> getAllDirectors() {
        return findMany(FIND_ALL_DIRECTORS);
    }

    // Получение режиссера(director) по ID
    public Optional<Director> getDirector(long id) {
        return findOne(FIND_DIRECTOR, id);
    }

    //Создание режиссера(director)
    public Director createDirector(Director director){
        long id = insert(CREATE_DIRECTOR, director.getName());
        director.setId(id);
        return director;
    }

    //Изменение режиссера(director)
    public Director uptadeDirector (Director director) {
        update(UPDATE_DIRECTOR, director.getName(), director.getId());
        return director;
    }

    //Удаление режиссера(director) по ID
    public void deleteDirector (long id) {
        jdbc.update(DELETE_DIRECTOR, id);
    }


}
