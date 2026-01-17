-- Theatres table
CREATE TABLE theatres (
    theatre_id INT PRIMARY KEY,
    theatre_name VARCHAR(100) NOT NULL,
    city VARCHAR(50),
    address VARCHAR(200)
);

-- Movies table
CREATE TABLE movies (
    movie_id INT PRIMARY KEY,
    movie_name VARCHAR(100) NOT NULL,
    language VARCHAR(20),
    duration_minutes INT
);

-- Screens table
CREATE TABLE screens (
    screen_id INT PRIMARY KEY,
    theatre_id INT NOT NULL,
    screen_name VARCHAR(50),
    total_seats INT,
    CONSTRAINT fk_screens_theatre
        FOREIGN KEY (theatre_id)
        REFERENCES theatres(theatre_id)
);

-- Shows table
CREATE TABLE shows (
    show_id INT PRIMARY KEY,
    movie_id INT NOT NULL,
    screen_id INT NOT NULL,
    show_date DATE NOT NULL,
    CONSTRAINT fk_shows_movie
        FOREIGN KEY (movie_id)
        REFERENCES movies(movie_id),
    CONSTRAINT fk_shows_screen
        FOREIGN KEY (screen_id)
        REFERENCES screens(screen_id)
);

-- Show timings table
CREATE TABLE show_timings (
    timing_id INT PRIMARY KEY,
    show_id INT NOT NULL,
    show_time TIME NOT NULL,
    CONSTRAINT fk_show_timings_show
        FOREIGN KEY (show_id)
        REFERENCES shows(show_id)
);
