package org.example.movietracker;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;

public class MovieCellController extends ListCell<Movie> {
    private final MainController mainController;
    private HBox content;
    private Label titleLabel;
    private Label yearLabel;
    private Label typeLabel;
    private HBox ratingBox;
    private CheckBox watchedCheckBox;
    private Button deleteButton;

    public MovieCellController(MainController mainController) {
        super();
        this.mainController = mainController;
        createContent();
    }

    private void createContent() {
        titleLabel = new Label();
        titleLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

        yearLabel = new Label();
        yearLabel.setStyle("-fx-text-fill: gray; -fx-font-size: 12px;");

        typeLabel = new Label();

        VBox textBox = new VBox(3);
        textBox.getChildren().addAll(titleLabel, yearLabel, typeLabel);

        ratingBox = new HBox(5);

        watchedCheckBox = new CheckBox("Watched");
        watchedCheckBox.setStyle("-fx-text-fill: green;");

        deleteButton = new Button("Delete");
        deleteButton.setStyle("-fx-background-color: #ef4444; -fx-text-fill: white; -fx-cursor: hand;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        content = new HBox(15);
        content.setPadding(new Insets(10));
        content.setStyle("-fx-background-color: white; -fx-background-radius: 5;");
        content.getChildren().addAll(textBox, spacer, ratingBox, watchedCheckBox, deleteButton);
    }

    @Override
    protected void updateItem(Movie movie, boolean empty) {
        super.updateItem(movie, empty);

        if (empty || movie == null) {
            setGraphic(null);
        } else {
            titleLabel.setText(movie.getTitle());
            yearLabel.setText(movie.getYear());

            typeLabel.setText(movie.getType());
            typeLabel.setStyle("Show".equals(movie.getType())
                    ? "-fx-font-size: 10px; -fx-text-fill: white; -fx-background-color: #8b5cf6; -fx-background-radius: 3; -fx-padding: 1 5 1 5;"
                    : "-fx-font-size: 10px; -fx-text-fill: white; -fx-background-color: #3b82f6; -fx-background-radius: 3; -fx-padding: 1 5 1 5;");

            ratingBox.getChildren().clear();
            for (int i = 1; i <= 5; i++) {
                final int rating = i;
                Button starButton = new Button(i <= movie.getRating() ? "★" : "☆");
                starButton.setStyle(i <= movie.getRating()
                        ? "-fx-background-color: transparent; -fx-text-fill: gold; -fx-font-size: 16px; -fx-cursor: hand;"
                        : "-fx-background-color: transparent; -fx-text-fill: gray; -fx-font-size: 16px; -fx-cursor: hand;");
                starButton.setOnAction(e -> {
                    movie.setRating(rating);
                    updateItem(movie, false);
                });
                ratingBox.getChildren().add(starButton);
            }

            watchedCheckBox.setSelected(movie.isWatched());
            watchedCheckBox.setOnAction(e -> movie.setWatched(watchedCheckBox.isSelected()));

            deleteButton.setOnAction(e -> mainController.deleteMovie(movie));

            setGraphic(content);
        }
    }
}
