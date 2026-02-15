package ru.yandex.practicum.filmorate.storage.mappers;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MPA;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashSet;
import java.util.Set;


@Component
public class FilmMapper implements RowMapper<Film> {

    @Override
    public Film mapRow(ResultSet rs, int rowNum) throws SQLException {

        Film film = new Film();
        film.setId(rs.getLong("film_id"));
        film.setName(rs.getString("name"));
        film.setDescription(rs.getString("description"));
        Date releaseDate = rs.getDate("release_date");
        film.setReleaseDate(releaseDate.toLocalDate());
        film.setDuration(rs.getLong("duration"));
        MPA mpa = new MPA();
        mpa.setId(rs.getLong("mpa_id"));
        mpa.setName(rs.getString("mpa_name"));
        film.setMpa(mpa);
        film.setGenres(new LinkedHashSet<>());
        film.setDirectors(new LinkedHashSet<>());
        Integer genreFilm = rs.getObject("genre_id", Integer.class);
        if (genreFilm != null) {
            Set<Genre> genres = new LinkedHashSet<>();
            Genre genre = new Genre();
            genre.setId(rs.getLong("genre_id"));
            genre.setName(rs.getString("genre_name"));
            genres.add(genre);
            film.setGenres(genres);
        }
        Integer directorFilm = rs.getObject("director_id", Integer.class);
        if (directorFilm != null) {
            Set<Director> director = new LinkedHashSet<>();
            Director newDirector = new Director();
            newDirector.setId(rs.getLong("director_id"));
            newDirector.setName(rs.getString("director_name"));
            director.add(newDirector);
            film.setDirectors(director);

        }
        return film;
    }
}


