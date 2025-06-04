# java-filmorate

Template repository for Filmorate project.
<picture>
 <source media="(prefers-color-scheme: dark)"[ srcset="https://github.com/Rus-tem/java-filmorate/blob/main/diagFilmorate.JPG">
 <source media="(prefers-color-scheme: light)" srcset="https://github.com/Rus-tem/java-filmorate/blob/main/diagFilmorate.JPG">
 <img alt="YOUR-ALT-TEXT" src="https://github.com/Rus-tem/java-filmorate/blob/main/diagFilmorate.JPG">
</picture>
<H3> Диаграмма описывает 7 таблиц: </H3>
<h2>Films</h2>
 <H6>
<br> Номер (id) фильма; </br> 
<br> Название (name) фильма; </br> 
<br>  Описание (description) фильма; </br> 
<br>  Дата создания (releaseDate) фильма; </br> 
<br>  Длительность (duration) фильма; </br> 
<br>  Возрастной рейтинг (mpa) фильма; </br> 
</H6>
 <h2>User</h2>
 <H6>
<br>  Номер (user_id) пользователя в таблице; </br> 
<br>  Логин (login) пользователя; </br> 
<br>  Имя (name) пользователя; </br> 
<br>  Электронный ящик (email) пользователя; </br> 
<br>  День рождения (birthday) пользователя; </br> 
</H6>
 <h2>Friends</h2>
 <H6>
<br>  id друга (friend_id) ; </br> 
<br>  id пользователя (user_id) ; </br> 
</H6>
 <h2>Likes</h2>
 <H6>
<br>  id фильма (film_id) ; </br> 
<br>  id пользователя (user_id); </br> 
</H6>
 <h2>Genre</h2>
 <H6>
<br>  id (genre_id) жанра; </br> 
<br>  название жанра (genre_name); </br> 
</H6>
 <h2>Film_Genres</h2>
 <H6>
<br>  id (genre_id) жанра; </br> 
<br>  id (film_id) фильма; </br> 
</H6>
 <h2>Mpa</h2>
 <H6>
<br>  id (mpa_id) рейтинга; </br> 
<br>  название (mpa_name) рейтинга; </br> 
</H6>