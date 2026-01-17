-- Insert theatres
INSERT INTO theatres (theatre_id, theatre_name, city, address) VALUES
(1, 'PVR Inorbit Mall', 'Hyderabad', 'Hitech City'),
(2, 'INOX GVK One', 'Hyderabad', 'Banjara Hills');

-- Insert movies
INSERT INTO movies (movie_id, movie_name, language, duration_minutes) VALUES
(1, 'Devara', 'Telugu', 165),
(2, 'Salaar', 'Telugu', 175),
(3, 'Pushpa 2', 'Telugu', 180);

-- Insert screens
INSERT INTO screens (screen_id, theatre_id, screen_name, total_seats) VALUES
(1, 1, 'Screen 1', 250),
(2, 1, 'Screen 2', 220),
(3, 2, 'Screen A', 200),
(4, 2, 'Screen B', 180);

-- Insert shows
INSERT INTO shows (show_id, movie_id, screen_id, show_date) VALUES
(1, 1, 1, '2026-01-10'),
(2, 2, 2, '2026-01-10'),
(3, 3, 3, '2026-01-10'),
(4, 1, 4, '2026-01-10');

-- Insert show timings
INSERT INTO show_timings (timing_id, show_id, show_time) VALUES
(1, 1, '10:00:00'),
(2, 1, '14:00:00'),
(3, 1, '18:00:00'),
(4, 2, '11:00:00'),
(5, 2, '16:00:00'),
(6, 3, '12:00:00'),
(7, 3, '20:00:00'),
(8, 4, '09:30:00'),
(9, 4, '13:30:00'),
(10, 4, '17:30:00');