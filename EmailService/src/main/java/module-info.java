module com.example.emailservice {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.logging;


    opens com.example.emailservice to javafx.fxml;
    exports com.example.emailservice;
}