package org.example.movietracker;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MovieTrackerApp extends Application {
    private static Stage primaryStage;
    private static User currentUser;

    @Override
    public void start(Stage stage) throws Exception {
        primaryStage = stage;
        primaryStage.setTitle("Movie Tracker");
        showLoginScreen();
        primaryStage.show();
    }

    public static void showLoginScreen() {
        try {
            FXMLLoader loader = new FXMLLoader(MovieTrackerApp.class.getResource("login.fxml"));
            Scene scene = new Scene(loader.load(), 800, 600);
            primaryStage.setScene(scene);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void showMainScreen(User user) {
        try {
            currentUser = user;
            FXMLLoader loader = new FXMLLoader(MovieTrackerApp.class.getResource("main.fxml"));
            Scene scene = new Scene(loader.load(), 1000, 700);
            primaryStage.setScene(scene);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static User getCurrentUser() {
        return currentUser;
    }



    public static void main(String[] args) {
        MovieDatabase db = new MovieDatabase();

        Movie test = new Movie("Inception", "2010", "", 5, true);

        db.insertMovie(test);

        launch(args);
    }
}