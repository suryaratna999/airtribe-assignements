SELECT
    m.movie_name AS Movie,
    s.show_date AS Date,
    st.show_time AS Show_Timing,
    sc.screen_name AS Screen,
    t.theatre_name AS Theatre
FROM show_timings st
JOIN shows s ON st.show_id = s.show_id
JOIN movies m ON s.movie_id = m.movie_id
JOIN screens sc ON s.screen_id = sc.screen_id
JOIN theatres t ON sc.theatre_id = t.theatre_id
WHERE t.theatre_name = 'PVR Inorbit Mall'
  AND s.show_date = '2026-01-10'
ORDER BY st.show_time;
