package com.progetto.uid.progettouid.Controller;

import com.progetto.uid.progettouid.DataBase.Authentication;
import com.progetto.uid.progettouid.DataBase.DBConnection;
import com.progetto.uid.progettouid.MainApplication;
import com.progetto.uid.progettouid.Message;
import com.progetto.uid.progettouid.View.SceneHandler;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.*;

public class GarageController {
    @FXML
    private Button addCarButton, removeCarButton1, removeCarButton2;
    @FXML
    private ComboBox<String> brandComboBox, modelComboBox;
    @FXML
    private Pane boxAddCar, paneGarage;
    @FXML
    private Text clickHereText, carText1, carText2, emptyGarageText;
    @FXML
    private ImageView carImage1, carImage2;
    @FXML
    private ScrollPane scrollPaneGarage;
    @FXML
    private VBox vBoxCar;
    private ImageView[] carImagesArray;
    private Text[] carTextArray;
    private Button[] buttons;
    String carSelected = null;

    @FXML
    void clickHereAction(MouseEvent event) {
        SceneHandler.getInstance().showAlert("Aggiunta veicolo", Message.add_car_information, 1);
    }

    @FXML
    void addCarButtonAction(ActionEvent event) throws SQLException, ExecutionException, InterruptedException, TimeoutException {
        String email = Authentication.getInstance().getUser().email();
        if (carSelected != null){
            CompletableFuture<Boolean> future = DBConnection.getInstance().addCar(email, carSelected);
            Boolean exists = future.get(10, TimeUnit.SECONDS);
            if (exists){
                SceneHandler.getInstance().showAlert("Errore", Message.exists_car_selected_error, 0);
            }
            initialize();
        }else{
            SceneHandler.getInstance().showAlert("Errore", Message.no_car_selected_error, 0);
        }

    }

    void removeCar(int id, String email) throws SQLException, ExecutionException, InterruptedException, TimeoutException {
        for (int i = 0; i < 2; i++){
            buttons[i].setVisible(false);
            carImagesArray[i].setImage(null);
            carTextArray[i].setText("");
        }
        DBConnection.getInstance().removeSelectedCar(id, email);
        initialize();
    }


    void arrayInitialize(){
        carImagesArray = new ImageView[]{carImage1, carImage2};
        carTextArray = new Text[]{carText1, carText2};
        buttons = new Button[]{removeCarButton1, removeCarButton2};
    }

    void initiliazeCarAndButtons() throws ExecutionException, InterruptedException, TimeoutException {
        CompletableFuture<ArrayList<String>> future = DBConnection.getInstance().getCar(Authentication.getInstance().getUser().email());
        ArrayList<String> car = future.get(10, TimeUnit.SECONDS);
        if (car.size() == 0){
            emptyGarageText.setText("Il tuo garage è vuoto:");
        }else{
            emptyGarageText.setText("Il tuo garage:");
        }

        for (int i = 0; i < car.size(); i++){
            String url = "immagini/" + car.get(i) + ".jpg";
            Image image = new Image(Objects.requireNonNull(MainApplication.class.getResourceAsStream(url)));
            carImagesArray[i].setImage(image);
            carTextArray[i].setText(car.get(i));

            int id = i;
            String email = Authentication.getInstance().getUser().email();
            buttons[i].setOnMouseClicked(mouseEvent -> {
                try {
                    removeCar(id, email);
                } catch (SQLException | ExecutionException | InterruptedException | TimeoutException e) {
                    throw new RuntimeException(e);
                }
            });
            buttons[i].setVisible(true);
        }
    }


    @FXML
    void initialize() throws ExecutionException, InterruptedException, TimeoutException {
        arrayInitialize();
        ObservableList<String> options = FXCollections.observableArrayList(
                "Fiat",
                "Volkswagen"
        );
        brandComboBox.setItems(options);

        brandComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && newValue.equals("Fiat")) {
                ObservableList<String> modelOption = FXCollections.observableArrayList(
                        "Panda"
                );
                modelComboBox.setItems(modelOption);
            } else if (newValue != null && newValue.equals("Volkswagen")) {
                ObservableList<String> modelOption = FXCollections.observableArrayList(
                        "Golf"
                );
                modelComboBox.setItems(modelOption);
            }
        });
        modelComboBox.setOnAction(actionEvent -> {
            carSelected = modelComboBox.getSelectionModel().getSelectedItem();
        });
        initiliazeCarAndButtons();
    }
}
