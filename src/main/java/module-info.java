module org.example.movietracker {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;


    opens org.example.movietracker to javafx.fxml;
    exports org.example.movietracker;
}