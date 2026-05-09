package org.example.movietracker;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.shape.Rectangle;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;

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
        setOnDragDetected(event -> {
            if (getItem() == null) return;
            Dragboard db = startDragAndDrop(TransferMode.MOVE);
            ClipboardContent content = new ClipboardContent();
            content.putString(String.valueOf(getIndex()));
            db.setContent(content);
            event.consume();
        });

        setOnDragOver(event -> {
            if (event.getGestureSource() != this && event.getDragboard().hasString()) {
                event.acceptTransferModes(TransferMode.MOVE);
            }
            event.consume();
        });

        setOnDragEntered(event -> {
            if (event.getGestureSource() != this && event.getDragboard().hasString()) {
                setStyle("-fx-border-color: #3b82f6; -fx-border-width: 2;");
            }
        });

        setOnDragExited(event -> setStyle(""));

        setOnDragDropped(event -> {
            if (getItem() == null) return;
            Dragboard db = event.getDragboard();
            if (db.hasString()) {
                int fromIndex = Integer.parseInt(db.getString());
                int toIndex = getIndex();
                mainController.reorderMovie(fromIndex, toIndex);
                event.setDropCompleted(true);
            }
            event.consume();
        });
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

        boolean overScale = movie.getRating() > 10.0;
        Label ratingLabel = new Label(movie.getRating() > 0 ? (overScale ? movie.getRating() + " 🔥" : movie.getRating() + "/10") : "-/10");
        ratingLabel.setStyle(overScale
                ? "-fx-text-fill: #ff4500; -fx-font-size: 14px; -fx-font-weight: bold;"
                : "-fx-text-fill: gold; -fx-font-size: 14px; -fx-font-weight: bold;");

        Spinner<Double> ratingSpinner = new Spinner<>(
                new SpinnerValueFactory.DoubleSpinnerValueFactory(0.0, Double.MAX_VALUE, movie.getRating(), 0.5)
        );
        ratingSpinner.setEditable(true);
        ratingSpinner.setPrefWidth(75);

        ratingSpinner.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                movie.setRating(newVal);
                if (newVal > 10.0) {
                    ratingLabel.setText(newVal + " 🔥");
                    ratingLabel.setStyle("-fx-text-fill: #ff4500; -fx-font-size: 14px; -fx-font-weight: bold;");
                } else {
                    ratingLabel.setText(newVal + "/10");
                    ratingLabel.setStyle("-fx-text-fill: gold; -fx-font-size: 14px; -fx-font-weight: bold;");
                }
                mainController.updateMovie(movie);
            }
        });

        ratingBox.getChildren().addAll(ratingLabel, ratingSpinner);

        watchedCheckBox.setOnAction(null);
        watchedCheckBox.setSelected(movie.isWatched());
        watchedCheckBox.setOnAction(e -> {
            movie.setWatched(watchedCheckBox.isSelected());
            mainController.updateMovie(movie);
        });

        deleteButton.setOnAction(e -> mainController.deleteMovie(movie));

        setGraphic(content);
        setText(null);
    }
    }

