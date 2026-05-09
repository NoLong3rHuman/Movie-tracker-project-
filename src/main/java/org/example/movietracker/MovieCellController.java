package org.example.movietracker;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.shape.Rectangle;

public class MovieCellController extends ListCell<Movie> {
    private final MainController mainController;
    private HBox content;
    private Label titleLabel;
    private Label yearLabel;
    private Label typeLabel;
    private HBox ratingBox;
    private CheckBox watchedCheckBox;
    private Button deleteButton;
    private ImageView posterView;

    public MovieCellController(MainController mainController) {
        super();
        this.mainController = mainController;
        createContent();
        setContentDisplay(ContentDisplay.GRAPHIC_ONLY);

        setOnMouseClicked(event -> {
            if (event.getClickCount() == 2 && !isEmpty()) {
                mainController.editMovie(getItem());
            }
        });
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

        posterView = new ImageView();
        posterView.setFitWidth(90);
        posterView.setFitHeight(135);
        posterView.setPreserveRatio(true);
        posterView.setSmooth(true);

        Rectangle clip = new Rectangle(90, 135);
        clip.setArcWidth(20);
        clip.setArcHeight(20);

        posterView.setClip(clip);

        content = new HBox(15);
        content.setPadding(new Insets(10));
        content.setStyle("-fx-background-color: transparent; -fx-background-radius: 5;");

        content.getChildren().addAll(
                posterView,
                textBox,
                spacer,
                ratingBox,
                watchedCheckBox,
                deleteButton
        );
    }




    @Override
    protected void updateItem(Movie movie, boolean empty) {
        super.updateItem(movie, empty);

        if (empty || movie == null) {
            setGraphic(null);
            setText(null);
            return;
        }


        if (movie.getPosterUrl() != null && !movie.getPosterUrl().isEmpty()) {
            Image image = new Image(movie.getPosterUrl(), true);
            posterView.setImage(image);
        } else {
            posterView.setImage(null);
        }

        titleLabel.setText(movie.getTitle());
        yearLabel.setText(String.valueOf(movie.getYear()));

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
            starButton.setOnAction(e -> movie.setRating(rating));
            ratingBox.getChildren().add(starButton);
        }

        watchedCheckBox.setOnAction(null); // clear first to avoid firing during setSelected
        watchedCheckBox.setSelected(movie.isWatched());
        watchedCheckBox.setOnAction(e -> movie.setWatched(watchedCheckBox.isSelected()));

        deleteButton.setOnAction(e -> mainController.deleteMovie(movie));

        setGraphic(content); // ← Always set at the end of the non-empty path
        setText(null);       // ← ADD THIS
    }
    }

