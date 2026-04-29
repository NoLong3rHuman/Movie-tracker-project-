package org.example.movietracker;

import java.sql.*;

public class MovieDatabase {

    final static String DB_NAME = "movietrackerdb";

    final static String DB_URL =
            "jdbc:mysql://skyl4.mysql.database.azure.com:3306/" + DB_NAME +
                    "?useSSL=true&requireSSL=true&verifyServerCertificate=false&serverTimezone=UTC";

    final static String USERNAME = "pined20";
    final static String PASSWORD = "YOUR_PASSWORD"; // put your real password here

    // ---------- CONNECT + CREATE TABLE ----------
    public void connectToDatabase() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

            Connection conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
            Statement statement = conn.createStatement();

            String sql = "CREATE TABLE IF NOT EXISTS movies (" +
                    "id INT NOT NULL PRIMARY KEY AUTO_INCREMENT," +
                    "title VARCHAR(200) NOT NULL," +
                    "year VARCHAR(10)," +
                    "posterUrl VARCHAR(500)," +
                    "rating INT," +
                    "watched BOOLEAN" +
                    ")";

            statement.executeUpdate(sql);

            statement.close();
            conn.close();

            System.out.println("Database ready.");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ---------- INSERT MOVIE ----------
    public void insertMovie(Movie movie) {
        connectToDatabase();

        try {
            Connection conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);

            String sql = "INSERT INTO movies (title, year, posterUrl, rating, watched) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement ps = conn.prepareStatement(sql);

            ps.setString(1, movie.getTitle());
            ps.setString(2, movie.getYear());
            ps.setString(3, movie.getPosterUrl());
            ps.setInt(4, movie.getRating());
            ps.setBoolean(5, movie.isWatched());

            ps.executeUpdate();

            ps.close();
            conn.close();

            System.out.println("Movie inserted!");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}