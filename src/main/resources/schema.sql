create TABLE IF NOT EXISTS USERS (
user_id integer NOT NULL PRIMARY KEY AUTO_INCREMENT,
name varchar(255) NOT NULL,
email varchar(255) NOT NULL,
login varchar(255) NOT NULL,
birthday date,
CONSTRAINT user_pk PRIMARY KEY (user_id)
);

create TABLE IF NOT EXISTS MPA (
mpa_id integer NOT NULL PRIMARY KEY AUTO_INCREMENT,
mpa_name varchar(255),
CONSTRAINT mpa_pk PRIMARY KEY (mpa_id) -- здесь
);

create TABLE IF NOT EXISTS GENRE (
genre_id integer NOT NULL PRIMARY KEY AUTO_INCREMENT,
genre_name varchar(255),
CONSTRAINT genre_pk PRIMARY KEY (genre_id)
);

create TABLE IF NOT EXISTS FILMS (
film_id integer NOT NULL PRIMARY KEY AUTO_INCREMENT,
name varchar(255) NOT NULL,
description varchar(255) NOT NULL,
release_date date,
duration integer,
mpa_id integer REFERENCES MPA(mpa_id),
CONSTRAINT film_pk PRIMARY KEY (film_id)
);

create TABLE IF NOT EXISTS FILM_GENRES (
film_id integer REFERENCES films(film_id),
genre_id integer REFERENCES genre(genre_id),
PRIMARY KEY (film_id, genre_id)
);

create TABLE IF NOT EXISTS FRIENDS (
user_id integer REFERENCES users,
friends_id integer REFERENCES users,
PRIMARY KEY (user_id, friends_id)
);

create TABLE IF NOT EXISTS LIKES (
user_id integer REFERENCES users(user_id),
film_id integer REFERENCES films(film_id),
PRIMARY KEY (user_id, film_id)
);

create TABLE IF NOT EXISTS REVIEWS (
review_id integer NOT NULL PRIMARY KEY AUTO_INCREMENT ,
content varchar(255) NOT NULL,
is_positive boolean NOT NULL,
user_id integer REFERENCES users(user_id) NOT NULL,
film_id integer REFERENCES films(film_id) NOT NULL
);

create TABLE IF NOT EXISTS REVIEW_LIKES (
review_id integer REFERENCES reviews(review_id),
user_id integer REFERENCES users(user_id),
likes integer
);

CREATE UNIQUE INDEX IF NOT EXISTS reviews ON REVIEW_LIKES(review_id, user_id);




