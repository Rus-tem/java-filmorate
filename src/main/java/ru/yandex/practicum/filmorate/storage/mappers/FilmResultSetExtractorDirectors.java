package ru.yandex.practicum.filmorate.storage.mappers;

import org.springframework.jdbc.core.ResultSetExtractor;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;

public class FilmResultSetExtractorDirectors implements ResultSetExtractor<Film>{

        @Override
        public Film extractData(ResultSet rs) throws SQLException {
            Film film = null;
          //  Set<Genre> genres = new TreeSet<>(Comparator.comparingLong(Genre::getId));
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

                    //      Genre genre = new Genre();
                   //genre.setId(rs.getInt("genre_id"));
                 //   genre.setName(rs.getString("genre_name"));
                  //  genres.add(genre);
                }
            }
          //  if (film != null) {
         //       film.setGenres(genres);
         //   }
            if (film != null) {
                film.setDirectors(directors);
            }
            return film;
        }
    }




