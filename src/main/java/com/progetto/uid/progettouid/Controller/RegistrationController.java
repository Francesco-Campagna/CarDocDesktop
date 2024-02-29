package com.progetto.uid.progettouid.Controller;

import com.progetto.uid.progettouid.DataBase.DBConnection;
import com.progetto.uid.progettouid.DataBase.Validation;
import com.progetto.uid.progettouid.Message;
import com.progetto.uid.progettouid.View.SceneHandler;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class RegistrationController {

    @FXML
    private Pane boxRegistration, registrationUpperPane;
    @FXML
    private ImageView carDocLogoIcon, returnHomeImage;
    @FXML
    private Button createAccountButton;
    @FXML
    private TextField emailField, nameField, surnameField;
    @FXML
    private PasswordField passwordField, repeatPasswordField;
    @FXML
    private Text privacyInformationText;
    @FXML
    private AnchorPane registrationAnchorPane;

    @FXML
    void createAccountAction(ActionEvent event) throws Exception {
        CompletableFuture<Boolean> future = Validation.getInstance().checkRegistration(nameField.getText(), surnameField.getText(), emailField.getText(), passwordField.getText(), repeatPasswordField.getText());
        Boolean valid = future.get(10, TimeUnit.SECONDS);
        if (valid){
            String encryptedPassword = DBConnection.getInstance().encryptedPassword(passwordField.getText());
            DBConnection.getInstance().insertUsers(nameField.getText(), surnameField.getText(), emailField.getText(), encryptedPassword, null, 0);
            SceneHandler.getInstance().setLoginScene();
        }
    }

    @FXML
    void enterPressed(KeyEvent event) throws Exception {
        if (event.getCode() == KeyCode.ENTER){
            ActionEvent ignored = new ActionEvent();
            createAccountAction(ignored);
        }
    }

    @FXML
    void privacyInformationAction(MouseEvent event) {
        try{
            SceneHandler.getInstance().showAlert("Informativa sulla privacy", Message.privacy_information, 1);
        } catch (Exception e){
            SceneHandler.getInstance().showAlert("Error", Message.privacy_information_error, 0);
        }
    }

    @FXML
    void returnHomeAction(MouseEvent event) {
        SceneHandler.getInstance().setHomeScene();
    }

}
