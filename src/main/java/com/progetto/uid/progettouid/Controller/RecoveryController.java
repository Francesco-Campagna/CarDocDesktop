package com.progetto.uid.progettouid.Controller;

import com.progetto.uid.progettouid.DataBase.DBConnection;
import com.progetto.uid.progettouid.Message;
import com.progetto.uid.progettouid.View.SceneHandler;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import java.sql.SQLException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class RecoveryController {

    @FXML
    private Pane boxRecoveryPassword;
    @FXML
    private ImageView carDocLogoIcon, returnHomeImage;
    @FXML
    private Text clientService, wrongEmailText;
    @FXML
    private TextField emailField;
    @FXML
    private AnchorPane recoveryAnchorPane, recoveryUpperPane;
    @FXML
    private Button recoveryPasswordButton;

    @FXML
    void clientServiceAction(MouseEvent event) {
        try{
            SceneHandler.getInstance().showAlert("Recupero password", Message.recovery_password_information, 1);
        } catch (Exception e){
            SceneHandler.getInstance().showAlert("Error", Message.recovery_password_error, 0);
        }
    }

    @FXML
    void recoveryPasswordAction(ActionEvent event) throws SQLException, ExecutionException, InterruptedException, TimeoutException {
        String email = emailField.getText();
        if (!email.isEmpty() ||  email.contains("@")) {
            CompletableFuture<Boolean> future = DBConnection.getInstance().checkExistEmail(email);
            Boolean value = future.get(10, TimeUnit.SECONDS);
            if (value) {
                SceneHandler.getInstance().showAlert("Recupero password", Message.recovery_password_email_message, 1);
            } else {
                wrongEmailText.setVisible(true);
            }
        }else{
            wrongEmailText.setVisible(true);
        }
        emailField.clear();
    }

    @FXML
    void returnHomeAction(MouseEvent event) throws Exception {
        SceneHandler.getInstance().setHomeScene();
    }

    @FXML
    void resetWrongText(MouseEvent event) {
        wrongEmailText.setVisible(false);
    }

}
