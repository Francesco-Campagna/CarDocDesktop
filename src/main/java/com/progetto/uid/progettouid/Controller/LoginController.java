package com.progetto.uid.progettouid.Controller;

import com.progetto.uid.progettouid.DataBase.Authentication;
import com.progetto.uid.progettouid.DataBase.DBConnection;
import com.progetto.uid.progettouid.Message;
import com.progetto.uid.progettouid.Model.User;
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
import java.sql.SQLException;
import java.util.concurrent.*;

public class LoginController {
    @FXML
    private Pane boxLogin, loginUpperPane;
    @FXML
    private ImageView carDocLogoIcon, returnHomeImage;
    @FXML
    private TextField emailField;
    @FXML
    private Text forgotPassword, registerNow, wrongLoginText;
    @FXML
    private AnchorPane loginAnchorPane;
    @FXML
    private Button loginButton;
    @FXML
    private PasswordField passwordField;


    @FXML
    void forgotPasswordAction(MouseEvent event) {
        try {
            SceneHandler.getInstance().setRecoveryScene();
        } catch (Exception e){
            SceneHandler.getInstance().showAlert("Error", Message.recovery_password_error, 0);
        }
    }

    @FXML
    void login(ActionEvent event) {
        try {
            Future<?> future = DBConnection.getInstance().checkLogin(emailField.getText(), passwordField.getText());
            future.get();
            CompletableFuture<User> future1 = DBConnection.getInstance().setUser(emailField.getText());
            User user = future1.get(10, TimeUnit.SECONDS);
            Authentication.getInstance().login(user);
            SceneHandler.getInstance().setHomeScene();
        } catch (SQLException | InterruptedException | TimeoutException | ExecutionException e){
            wrongLoginText.setVisible(true);
        } catch (Exception e) {
            e.printStackTrace();
            SceneHandler.getInstance().showAlert("Error", Message.returnHome_error, 0);
        }
        emailField.clear();
        passwordField.clear();
    }

    @FXML
    void enterPressed(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER){
            ActionEvent ignored = new ActionEvent();
            login(ignored);
        }
    }

    @FXML
    void registerNowAction(MouseEvent event) throws Exception {
        SceneHandler.getInstance().setRegistrationScene();
    }

    @FXML
    void returnHomeAction(MouseEvent event) throws Exception {
        SceneHandler.getInstance().setHomeScene();
    }

    @FXML
    void resetWrongText(MouseEvent event) {
        wrongLoginText.setVisible(false);
    }

}
