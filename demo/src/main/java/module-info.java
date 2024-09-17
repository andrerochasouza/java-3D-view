module br.com.andre.demo {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;

    opens br.com.andre.demo to javafx.fxml;
    exports br.com.andre.demo;
}