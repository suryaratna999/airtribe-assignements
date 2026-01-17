# BookMyShow Database Assignment

**Project Description:**  
BookMyShow is a ticketing platform that allows users to book tickets for movie shows. In this project, we simulate a simplified version of the platform where users can select a theatre, view the next 7 dates, and see all shows running in that theatre along with their timings.

---

## **P1 – Entities, Attributes, and Table Structures**

### **Entities and Attributes**

| Entity        | Attributes                                                                 |
|---------------|---------------------------------------------------------------------------|
| Theatre       | `theatre_id` (PK), `theatre_name`, `city`, `address`                      |
| Movie         | `movie_id` (PK), `movie_name`, `language`, `duration_minutes`             |
| Screen        | `screen_id` (PK), `theatre_id` (FK → Theatre), `screen_name`, `total_seats` |
| Show          | `show_id` (PK), `movie_id` (FK → Movie), `screen_id` (FK → Screen), `show_date` |
| Show Timing   | `timing_id` (PK), `show_id` (FK → Show), `show_time`                      |

**Normalization:**

- **1NF:** All tables have atomic values, no repeating groups. ✅
- **2NF:** All non-key attributes fully depend on the primary key. ✅
- **3NF:** No transitive dependencies; attributes depend only on the primary key. ✅
- **BCNF:** Every determinant is a candidate key. ✅

---

### **SQL Script to Create Tables**

```sql
-- Create Theatre Table
CREATE TABLE theatres (
    theatre_id INT PRIMARY KEY,
    theatre_name VARCHAR(100) NOT NULL,
    city VARCHAR(50),
    address VARCHAR(200)
);

-- Create Movies Table
CREATE TABLE movies (
    movie_id INT PRIMARY KEY,
    movie_name VARCHAR(100) NOT NULL,
    language VARCHAR(20),
    duration_minutes INT
);

-- Create Screens Table
CREATE TABLE screens (
    screen_id INT PRIMARY KEY,
    theatre_id INT NOT NULL,
    screen_name VARCHAR(50),
    total_seats INT,
    FOREIGN KEY (theatre_id) REFERENCES theatres(theatre_id)
);

-- Create Shows Table
CREATE TABLE shows (
    show_id INT PRIMARY KEY,
    movie_id INT NOT NULL,
    screen_id INT NOT NULL,
    show_date DATE NOT NULL,
    FOREIGN KEY (movie_id) REFERENCES movies(movie_id),
    FOREIGN KEY (screen_id) REFERENCES screens(screen_id)
);

-- Create Show Timings Table
CREATE TABLE show_timings (
    timing_id INT PRIMARY KEY,
    show_id INT NOT NULL,
    show_time TIME NOT NULL,
    FOREIGN KEY (show_id) REFERENCES shows(show_id)
);
