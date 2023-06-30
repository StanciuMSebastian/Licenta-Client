module com.example.licenta {
    requires javafx.controls;
    requires javafx.fxml;
    requires zap.clientapi;
    requires itextpdf;
    requires de.jensd.fx.glyphs.materialdesignicons;
    requires org.json;
    requires de.jensd.fx.glyphs.fontawesome;
    requires com.jfoenix;
    requires org.controlsfx.controls;


    opens com.example.licenta to javafx.fxml;
    exports com.example.licenta;
    exports com.example.licenta.controllers;
    opens com.example.licenta.controllers to javafx.fxml;
}