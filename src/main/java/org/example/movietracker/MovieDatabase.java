package org.example.movietracker;

import java.security.MessageDigest;
import java.sql.*;
import java.util.*;

public class MovieDatabase {

    // Embedded Derby — creates a local database folder called "movietrackerdb" on first run.
    // To switch back to Azure, replace these three lines with the MySQL URL, username, and password.
      final static String DB_URL = "jdbc:derby:movietrackerdb;create=true";
//    final static String USERNAME = "";
//    final static String PASSWORD = "";

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }

    final static String USERNAME = "pined20";
    final static String PASSWORD = "Skyluvsme24";

    // ---------- INIT TABLES ----------

    public void connectToDatabase() {
        try (Connection conn = getConnection()) {
            Statement stmt = conn.createStatement();

            createTableSafely(stmt,
                    "CREATE TABLE users (" +
                            "id INT NOT NULL PRIMARY KEY GENERATED ALWAYS AS IDENTITY," +
                            "email VARCHAR(255) NOT NULL UNIQUE," +
                            "password_hash VARCHAR(64) NOT NULL" +
                            ")");

            createTableSafely(stmt,
                    "CREATE TABLE movies (" +
                            "id INT NOT NULL PRIMARY KEY GENERATED ALWAYS AS IDENTITY," +
                            "user_id INT NOT NULL," +
                            "title VARCHAR(200) NOT NULL," +
                            "\"year\" VARCHAR(10)," +
                            "posterUrl VARCHAR(500)," +
                            "\"type\" VARCHAR(10)," +
                            "rating DOUBLE," +
                            "watched BOOLEAN" +
                            ")");

            // Add sort_order column if it doesn't exist yet
            try {
                stmt.executeUpdate("ALTER TABLE movies ADD COLUMN sort_order INT DEFAULT 0");
            } catch (SQLException e) {
                if (!"X0Y32".equals(e.getSQLState()) && !"42X01".equals(e.getSQLState())) {
                    e.printStackTrace();
                }
            }
                    "id INT NOT NULL PRIMARY KEY GENERATED ALWAYS AS IDENTITY," +
                    "user_id INT NOT NULL," +
                    "title VARCHAR(200) NOT NULL," +
                    "year VARCHAR(10)," +
                    "posterUrl VARCHAR(500)," +
                    "rating INT," +
                    "watched BOOLEAN" +
                    ")" + "genre VARCHAR(100)");

            stmt.close();
            conn.close();
            System.out.println("Database ready.");

            System.out.println("Database ready.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void createTableSafely(Statement stmt, String sql) {
        try {
            stmt.executeUpdate(sql);
        } catch (SQLException e) {
            if (!"X0Y32".equals(e.getSQLState())) { // X0Y32 = table already exists in Derby
                e.printStackTrace();
            }
        }
    }

    //---------------- TEST CONNECTION
        public boolean testConnection() {
            try (Connection conn = getConnection()) {
                return conn != null && conn.isValid(2);
            } catch (SQLException e) {
                System.err.println("Connection test failed: " + e.getMessage());
                return false;
            }
        }
    // ---------- PASSWORD HASHING ----------
        private String hashPassword(String password) {
            try {
                MessageDigest md = MessageDigest.getInstance("SHA-256");
                byte[] hash = md.digest(password.getBytes("UTF-8"));
                StringBuilder hex = new StringBuilder();
                for (byte b : hash) hex.append(String.format("%02x", b));
                return hex.toString();
            } catch (Exception e) {
                throw new RuntimeException("Password hashing failed", e);
            }
        }

    // ---------- REGISTER USER ----------
    // Returns: 1 = success, 0 = email already taken, -1 = DB error
    public int registerUser(String email, String password) {
        connectToDatabase();
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(
                     "INSERT INTO users (email, password_hash) VALUES (?, ?)")) {
            ps.setString(1, email);
            ps.setString(2, hashPassword(password));
            ps.executeUpdate();
            return 1;
        } catch (SQLException e) {
            if ("23505".equals(e.getSQLState())) { // duplicate entry in Derby
                return 0;
            }
            e.printStackTrace();
            return -1;
        }
    }

    // ---------- AUTHENTICATE USER ----------
    // Returns a User on success, null if credentials are wrong
    public User authenticateUser(String email, String password) {
        connectToDatabase();
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(
                     "SELECT id, email FROM users WHERE email = ? AND password_hash = ?")) {
            ps.setString(1, email);
            ps.setString(2, hashPassword(password));
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new User(rs.getInt("id"), rs.getString("email"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // ---------- INSERT MOVIE ----------
    public void insertMovie(int userId, Movie movie) {
        connectToDatabase();
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(
                     "INSERT INTO movies (user_id, title, \"year\", posterUrl, \"type\", rating, watched) VALUES (?, ?, ?, ?, ?, ?, ?)")) {
            ps.setInt(1, userId);           // user_id  (INTEGER)
            ps.setString(2, movie.getTitle());  // title
            ps.setString(3, movie.getYear());   // year
            ps.setString(4, movie.getPosterUrl()); // posterUrl
            ps.setString(5, movie.getType());   // type
            ps.setDouble(6, movie.getRating()); // rating
            ps.setBoolean(7, movie.isWatched()); // watched
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    //-------------- LOAD MOVIE --------------
    public List<Movie> loadUserMovies(int userid) {
        connectToDatabase();
        List<Movie> movies = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(
                     "SELECT * FROM movies WHERE user_id = ? ORDER BY sort_order DESC")) {
            ps.setInt(1, userid);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Movie m = new Movie(
                        rs.getString("title"),
                        rs.getString("year"),
                        rs.getString("posterUrl"),
                        rs.getString("type"),
                        rs.getInt("rating"),
                        rs.getBoolean("watched")
                );
                m.setId(rs.getInt("id"));
                m.setSortOrder(rs.getInt("sort_order"));
                movies.add(m);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return movies;
    }

            // ---------- UPDATE MOVIE ----------
            public void updateMovie(int movieId, Movie movie) {
                connectToDatabase();
                try (Connection conn = getConnection();
                     PreparedStatement ps = conn.prepareStatement(
                             "UPDATE movies SET title=?, \"year\"=?, posterUrl=?, \"type\"=?, rating=?, watched=? WHERE id=?")) {
                    ps.setString(1, movie.getTitle());
                    ps.setString(2, movie.getYear());
                    ps.setString(3, movie.getPosterUrl());
                    ps.setString(4, movie.getType());
                    ps.setDouble(5, movie.getRating());
                    ps.setBoolean(6, movie.isWatched());
                    ps.setInt(7, movieId);
                    ps.executeUpdate();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }


            // ---------- DELETE MOVIE ----------
            public void deleteMovie(int movieId) {
                connectToDatabase();
                try (Connection conn = getConnection();
                     PreparedStatement ps = conn.prepareStatement(
                             "DELETE FROM movies WHERE id=?")) {
                    ps.setInt(1, movieId);
                    ps.executeUpdate();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

    public void saveMovieOrder(List<Movie> movies) {
        connectToDatabase();
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(
                     "UPDATE movies SET sort_order=? WHERE id=?")) {
            for (int i = 0; i < movies.size(); i++) {
                ps.setInt(1, i);
                ps.setInt(2, movies.get(i).getId());
                ps.addBatch();
            }
            ps.executeBatch();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
        }