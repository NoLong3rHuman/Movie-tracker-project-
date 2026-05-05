package org.example.movietracker;

import java.security.MessageDigest;
import java.sql.*;

public class MovieDatabase {

    // Embedded Derby — creates a local database folder called "movietrackerdb" on first run.
    // To switch back to Azure, replace these three lines with the MySQL URL, username, and password.
    final static String DB_URL = "jdbc:derby:movietrackerdb;create=true";
    final static String USERNAME = "";
    final static String PASSWORD = "";

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }

    final static String USERNAME = "pined20";
    final static String PASSWORD = "Skyluvsme24";

    // ---------- INIT TABLES ----------
    // Derby does not support CREATE TABLE IF NOT EXISTS, so we catch the "table already exists" error.
    public void connectToDatabase() {
        try {
            Connection conn = getConnection();
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
                    "title VARCHAR(200) NOT NULL," +
                    "year VARCHAR(10)," +
                    "posterUrl VARCHAR(500)," +
                    "rating INT," +
                    "watched BOOLEAN" +
                    ")");

            stmt.close();
            conn.close();
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

    // ---------- PASSWORD HASHING ----------
    private String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes("UTF-8"));
            StringBuilder hex = new StringBuilder();
            for (byte b : hash) {
                hex.append(String.format("%02x", b));
            }
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
    public void insertMovie(Movie movie) {
        connectToDatabase();
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(
                     "INSERT INTO movies (title, year, posterUrl, rating, watched) VALUES (?, ?, ?, ?, ?)")) {
            ps.setString(1, movie.getTitle());
            ps.setString(2, movie.getYear());
            ps.setString(3, movie.getPosterUrl());
            ps.setInt(4, movie.getRating());
            ps.setBoolean(5, movie.isWatched());
            ps.executeUpdate();
            System.out.println("Movie inserted!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
