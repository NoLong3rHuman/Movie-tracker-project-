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
        String title = titleField.getText();
        String year = yearField.getText();
        String poster = posterField.getText();

        if (title.isEmpty() || year.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Invalid Input");
            alert.setHeaderText("Please fill in all required fields");
            alert.showAndWait();
            return;
        }

        Movie movie = new Movie(title, year, poster.isEmpty() ? null : poster);
        MovieDatabase md =  new MovieDatabase();
        md.insertMovie(movie);
        mainController.addMovie(movie);
        dialogStage.close();
    }
}