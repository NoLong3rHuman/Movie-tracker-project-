package org.example.movietracker;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class MainController {
    @FXML private Label emailLabel;
    @FXML private TextField searchField;
    @FXML private Button logoutButton;
    @FXML private Button allFilterButton;
    @FXML private Button watchedFilterButton;
    @FXML private Button unwatchedFilterButton;
    @FXML private Button addMovieButton;
    @FXML private ListView<Movie> movieListView;

    private ObservableList<Movie> allMovies;
    private ObservableList<Movie> filteredMovies;
    private String currentFilter = "all";

    @FXML
    public void initialize() {
        emailLabel.setText(MovieTrackerApp.getCurrentUser().getEmail());

        allMovies = FXCollections.observableArrayList();
        filteredMovies = FXCollections.observableArrayList();

        loadSampleMovies();

        movieListView.setCellFactory(param -> new MovieCellController());
        movieListView.setItems(filteredMovies);

        filterMovies();
    }

    @FXML
    private void handleLogout() {
        MovieTrackerApp.showLoginScreen();
    }

    @FXML
    private void handleSearch() {
        filterMovies();
    }

    @FXML
    private void handleAllFilter() {
        currentFilter = "all";
        updateFilterButtons();
        filterMovies();
    }

    @FXML
    private void handleWatchedFilter() {
        currentFilter = "watched";
        updateFilterButtons();
        filterMovies();
    }

    @FXML
    private void handleUnwatchedFilter() {
        currentFilter = "unwatched";
        updateFilterButtons();
        filterMovies();
    }

    @FXML
    private void handleAddMovie() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("add-movie-dialog.fxml"));
            Parent root = loader.load();

            AddMovieDialogController controller = loader.getController();

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Add Movie");
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            dialogStage.setScene(new Scene(root));

            controller.setDialogStage(dialogStage);
            controller.setMainController(this);

            dialogStage.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addMovie(Movie movie) {
        allMovies.add(movie);
        filterMovies();
    }

    public void deleteMovie(Movie movie) {
        allMovies.remove(movie);
        filterMovies();
    }

    private void filterMovies() {
        String searchText = searchField.getText().toLowerCase();
        filteredMovies.clear();

        for (Movie movie : allMovies) {
            boolean matchesSearch = movie.getTitle().toLowerCase().contains(searchText) ||
                    movie.getYear().contains(searchText);
            boolean matchesFilter = currentFilter.equals("all") ||
                    (currentFilter.equals("watched") && movie.isWatched()) ||
                    (currentFilter.equals("unwatched") && !movie.isWatched());

            if (matchesSearch && matchesFilter) {
                filteredMovies.add(movie);
            }
        }
    }

    private void updateFilterButtons() {
        allFilterButton.setStyle(currentFilter.equals("all") ?
                "-fx-background-color: #3b82f6; -fx-text-fill: white;" :
                "-fx-background-color: #e5e7eb;");
        watchedFilterButton.setStyle(currentFilter.equals("watched") ?
                "-fx-background-color: #3b82f6; -fx-text-fill: white;" :
                "-fx-background-color: #e5e7eb;");
        unwatchedFilterButton.setStyle(currentFilter.equals("unwatched") ?
                "-fx-background-color: #3b82f6; -fx-text-fill: white;" :
                "-fx-background-color: #e5e7eb;");
    }

    private void loadSampleMovies() {
        Movie movie1 = new Movie("The Shawshank Redemption", "1994", null);
        movie1.setRating(5);
        movie1.setWatched(true);
        allMovies.add(movie1);

        Movie movie2 = new Movie("The Dark Knight", "2008", null);
        movie2.setRating(4);
        movie2.setWatched(true);
        allMovies.add(movie2);

        Movie movie3 = new Movie("Inception", "2010", null);
        allMovies.add(movie3);
    }
}
