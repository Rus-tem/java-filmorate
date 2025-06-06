package ru.yandex.practicum.filmorate.storage.mappers;

import org.springframework.jdbc.core.ResultSetExtractor;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;

public class FilmResultSetDirectors implements ResultSetExtractor<Film> {
    // Метод обновления режиссеров фильма
    @Override
    public Film extractData(ResultSet rs) throws SQLException {
        Film film = null;
        Set<Director> directors = new TreeSet<>(Comparator.comparingLong(Director::getId));
        while (rs.next()) {
            if (film == null) {
                film = new FilmMapper().mapRow(rs, rs.getRow());
            }

            if (rs.getObject("director_id") != null) {
                Director director = new Director();
                director.setId(rs.getInt("director_id"));
                director.setName(rs.getString("director_name"));
                directors.add(director);

            }
        }

        if (film != null) {
            film.setDirectors(directors);
        }
        return film;
    }
}




