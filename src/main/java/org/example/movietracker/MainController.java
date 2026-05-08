package org.example.movietracker;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.util.List;

public class MainController {

    // Header
    @FXML private Label emailLabel;

    // Sidebar nav buttons
    @FXML private Button myListNavBtn;
    @FXML private Button addMovieNavBtn;
    @FXML private Button reportsNavBtn;

    // Sections
    @FXML private VBox myListSection;
    @FXML private VBox addMovieSection;
    @FXML private VBox reportsSection;

    // My List section
    @FXML private TextField searchField;
    @FXML private Button allFilterButton;
    @FXML private Button watchedFilterButton;
    @FXML private Button unwatchedFilterButton;
    @FXML private ListView<Movie> movieListView;

    // Add Movie/Show section
    @FXML private TextField titleField;
    @FXML private TextField yearField;
    @FXML private ComboBox<String> typeComboBox;
    @FXML private TextField posterField;

    // Reports section
    @FXML private Label totalCountLabel;
    @FXML private Label watchedCountLabel;
    @FXML private Label avgRatingLabel;
    @FXML private Label highestRatedLabel;

    private ObservableList<Movie> allMovies;
    private ObservableList<Movie> filteredMovies;

    private String currentFilter = "all";
    @FXML private ComboBox<String> genreFilterComboBox;
    private String currentGenreFilter = "All";

    private static final String NAV_ACTIVE =
            "-fx-background-color: white; -fx-text-fill: #1e3a5f; -fx-font-weight: bold; -fx-background-radius: 5; -fx-pref-height: 38px; -fx-cursor: hand;";
    private static final String NAV_INACTIVE =
            "-fx-background-color: transparent; -fx-text-fill: white; -fx-background-radius: 5; -fx-pref-height: 38px; -fx-cursor: hand;";


    @FXML
    public void initialize() {
        emailLabel.setText(MovieTrackerApp.getCurrentUser().getEmail());

        allMovies = FXCollections.observableArrayList();
        filteredMovies = FXCollections.observableArrayList();

        typeComboBox.getItems().addAll("Movie", "Show");
        typeComboBox.setValue("Movie");

        loadSampleMovies();
//        List<Movie> movies = new MovieDatabase().getUserMovies(MovieTrackerApp.getCurrentUser().getId());
//        allMovies.addAll(movies);


        movieListView.setCellFactory(param -> new MovieCellController(this));
        movieListView.setItems(filteredMovies);

        genreFilterComboBox.getItems().addAll(
                "All",
                "Action",
                "Comedy",
                "Drama",
                "Horror",
                "Sci-Fi",
                "Romance",
                "Documentary"
        );

        genreFilterComboBox.setValue("All");

        filterMovies();
    }

    // ---------- NAVIGATION ----------

    @FXML
    private void showMyListSection() {
        myListSection.setVisible(true);
        myListSection.setManaged(true);
        addMovieSection.setVisible(false);
        addMovieSection.setManaged(false);
        reportsSection.setVisible(false);
        reportsSection.setManaged(false);
        updateNavButtons(myListNavBtn);
    }

    @FXML
    private void showAddMovieSection() {
        myListSection.setVisible(false);
        myListSection.setManaged(false);
        addMovieSection.setVisible(true);
        addMovieSection.setManaged(true);
        reportsSection.setVisible(false);
        reportsSection.setManaged(false);
        updateNavButtons(addMovieNavBtn);
    }

    @FXML
    private void showReportsSection() {
        myListSection.setVisible(false);
        myListSection.setManaged(false);
        addMovieSection.setVisible(false);
        addMovieSection.setManaged(false);
        reportsSection.setVisible(true);
        reportsSection.setManaged(true);
        updateNavButtons(reportsNavBtn);
        updateReports();
    }

    private void updateNavButtons(Button active) {
        myListNavBtn.setStyle(myListNavBtn == active ? NAV_ACTIVE : NAV_INACTIVE);
        addMovieNavBtn.setStyle(addMovieNavBtn == active ? NAV_ACTIVE : NAV_INACTIVE);
        reportsNavBtn.setStyle(reportsNavBtn == active ? NAV_ACTIVE : NAV_INACTIVE);
    }

    // ---------- LOGOUT ----------

    @FXML
    private void handleLogout() {
        MovieTrackerApp.showLoginScreen();
    }

    // ---------- MY LIST ----------

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
    private void handleGenreFilter() {
        currentGenreFilter = genreFilterComboBox.getValue();
        filterMovies();
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

            boolean matchesGenre = currentGenreFilter.equals("All") ||
                    (movie.getGenre() != null && movie.getGenre().equals(currentGenreFilter));

            if (matchesSearch && matchesFilter && matchesGenre) {
                filteredMovies.add(movie);
            }
        }
    }

    private void updateFilterButtons() {
        allFilterButton.setStyle(currentFilter.equals("all")
                ? "-fx-background-color: #3b82f6; -fx-text-fill: white; -fx-cursor: hand;"
                : "-fx-background-color: #e5e7eb; -fx-cursor: hand;");
        watchedFilterButton.setStyle(currentFilter.equals("watched")
                ? "-fx-background-color: #3b82f6; -fx-text-fill: white; -fx-cursor: hand;"
                : "-fx-background-color: #e5e7eb; -fx-cursor: hand;");
        unwatchedFilterButton.setStyle(currentFilter.equals("unwatched")
                ? "-fx-background-color: #3b82f6; -fx-text-fill: white; -fx-cursor: hand;"
                : "-fx-background-color: #e5e7eb; -fx-cursor: hand;");
    }

    // ---------- ADD MOVIE/SHOW ----------

    @FXML
    private void handleAddMovieSubmit() {
        String title = titleField.getText().trim();
        String year = yearField.getText().trim();
        String type = typeComboBox.getValue();
        String poster = posterField.getText().trim();

        if (title.isEmpty() || year.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Validation");
            alert.setHeaderText("Title and Year are required.");
            alert.showAndWait();
            return;
        }

        Movie movie = new Movie(title, year, poster.isEmpty() ? null : poster, type, 0, false);
        addMovie(movie);

        titleField.clear();
        yearField.clear();
        typeComboBox.setValue("Movie");
        posterField.clear();

        showMyListSection();
    }

    // ---------- REPORTS ----------

    private void updateReports() {
        int total = allMovies.size();
        int watched = 0;
        int totalRating = 0;
        int ratedCount = 0;
        Movie highestRated = null;

        for (Movie movie : allMovies) {
            if (movie.isWatched()) watched++;
            if (movie.getRating() > 0) {
                totalRating += movie.getRating();
                ratedCount++;
                if (highestRated == null || movie.getRating() > highestRated.getRating()) {
                    highestRated = movie;
                }
            }
        }

        totalCountLabel.setText(String.valueOf(total));
        watchedCountLabel.setText(String.valueOf(watched));

        if (ratedCount > 0) {
            double avg = (double) totalRating / ratedCount;
            avgRatingLabel.setText(String.format("%.1f", avg));
        } else {
            avgRatingLabel.setText("-");
        }

        if (highestRated != null) {
            StringBuilder stars = new StringBuilder();
            for (int i = 0; i < highestRated.getRating(); i++) stars.append("★");
            highestRatedLabel.setText(
                    highestRated.getTitle() + " (" + highestRated.getYear() + ")  " + stars);
        } else {
            highestRatedLabel.setText("No rated entries yet.");
        }
    }

    // ---------- SAMPLE DATA (temporary until DB persistence is wired up) ----------

    private void loadSampleMovies() {
        Movie m1 = new Movie("The Shawshank Redemption", "1994", null);
        m1.setRating(5);
        m1.setWatched(true);
        allMovies.add(m1);

        Movie m2 = new Movie("The Dark Knight", "2008", null);
        m2.setRating(4);
        m2.setWatched(true);
        allMovies.add(m2);

        Movie m3 = new Movie("Inception", "2010", null);
        allMovies.add(m3);

        Movie m4 = new Movie("Breaking Bad", "2008", null, "Show", 5, true);
        allMovies.add(m4);

        Movie m5 = new Movie("The Lord of the Rings", "2007", null, "Show", 5, true, "Sci-Fi");
        allMovies.add(m5);
    }
}
