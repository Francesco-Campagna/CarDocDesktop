module com.progetto.uid.progettouid {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.kordamp.ikonli.javafx;
    requires java.sql;
    requires spring.security.crypto;


    opens com.progetto.uid.progettouid.Controller to javafx.fxml;
    exports com.progetto.uid.progettouid;

}