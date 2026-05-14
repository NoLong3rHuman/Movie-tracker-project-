package org.example.movietracker;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class AddMovieDialogController {
    @FXML private TextField titleField;
    @FXML private TextField yearField;
    @FXML private TextField posterField;
    @FXML private Button cancelButton;
    @FXML private Button addButton;
    @FXML
    private ComboBox<String> genreComboBox;

    private Stage dialogStage;
    private MainController mainController;

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    @FXML
    private void handleCancel() {
        dialogStage.close();
    }

    @FXML
    private void handleAdd() {
        String title = titleField.getText().trim();
        String year = yearField.getText().trim();
        String poster = posterField.getText().trim();

        if (title.isEmpty() || year.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Invalid Input");
            alert.setHeaderText("Please fill in all required fields");
            alert.showAndWait();
            return;
        }

        Movie movie = new Movie(title, year, poster.isEmpty() ? null : poster);
        mainController.addMovie(movie);
        dialogStage.close();
    }


}