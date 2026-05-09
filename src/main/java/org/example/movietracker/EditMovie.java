package org.example.movietracker;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import javax.print.DocFlavor;

public class EditMovie extends Dialog<Movie> {

    public EditMovie(Movie movie) {
        setTitle("Edit Movie / Show");
        setHeaderText("Editing: " + movie.getTitle());

        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        TextField titleField = new TextField(movie.getTitle());
        TextField yearField = new TextField(movie.getYear());
        TextField posterField = new TextField(movie.getPosterUrl() != null ? movie.getPosterUrl() : "");
        ComboBox<String> typeCombo = new ComboBox<>();
        typeCombo.getItems().addAll("Movie", "Show");
        typeCombo.setValue(movie.getType());
        CheckBox watchedCheck = new CheckBox("Watched");
        watchedCheck.setSelected(movie.isWatched());

        HBox ratingBox = new HBox(5);
        double[] selectedRating = {movie.getRating()};
        Button[] stars = new Button[5];
        for (int i = 1; i<= 5; i++) {
            final int rating = i;
            stars[i - 1] = new Button(i <= movie.getRating() ? "★" : "☆");
            stars[i - 1].setStyle("-fx-background-color: transparent; -fx-font-size: 18x; -fx-cursor: hand;");
            stars[i - 1].setOnAction(e -> {
                selectedRating[0] = rating;
                for (int j = 0; j < 5; j++) {
                    stars[j].setText(j < rating ? "★" : "☆");
                    stars[j].setStyle("-fx-background-color: transparent; -fx-font-size: 18px; -fx-cursor: hand; -fx-text-fill: "
                            + (j < rating ? "gold" : "gray") + ";");
                }
            });

            stars[i - 1].setStyle("-fx-background-color: transparent; -fx-font-size: 18px; -fx-cursor: hand; -fx-text-fill: "
                    + (i <= movie.getRating() ? "gold" : "gray") + ";");
            ratingBox.getChildren().add(stars[i - 1]);
        }
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        grid.add(new Label("Title:"), 0, 0);
        grid.add(titleField, 1, 0);
        grid.add(new Label("Year:"), 0, 1);
        grid.add(yearField, 1, 1);
        grid.add(new Label("Type:"), 0, 2);
        grid.add(typeCombo, 1, 2);
        grid.add(new Label("Poster URL:"), 0, 3);
        grid.add(posterField, 1, 3);
        grid.add(new Label("Rating:"), 0, 4);
        grid.add(ratingBox, 1, 4);
        grid.add(new Label("Status:"), 0, 5);
        grid.add(watchedCheck, 1, 5);

        getDialogPane().setContent(grid);


        setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                movie.setTitle(titleField.getText().trim());
                movie.setYear(yearField.getText().trim());
                movie.setType(typeCombo.getValue());
                movie.setPosterUrl(posterField.getText().trim().isEmpty() ? null : posterField.getText().trim());
                movie.setRating((int) selectedRating[0]);
                movie.setWatched(watchedCheck.isSelected());
                return movie;
            }
            return null;
        });
    }
}
