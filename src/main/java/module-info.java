module org.example.movietracker {
    requires javafx.controls;
    requires javafx.fxml;


    opens org.example.movietracker to javafx.fxml;
    exports org.example.movietracker;
}