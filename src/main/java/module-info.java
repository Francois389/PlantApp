module com.fsp.plantapp {
    requires javafx.controls;
    requires javafx.fxml;
    requires kotlin.stdlib;

    requires org.controlsfx.controls;

    opens com.fsp.plantapp to javafx.fxml;
    exports com.fsp.plantapp;
}