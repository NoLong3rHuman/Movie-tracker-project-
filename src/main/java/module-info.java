module org.example.movietracker {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires org.apache.derby.engine;
    requires org.apache.derby.commons;
    requires java.desktop;

    uses java.sql.Driver;

    opens org.example.movietracker to javafx.fxml;
    exports org.example.movietracker;
}