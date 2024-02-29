package com.progetto.uid.progettouid;

import com.progetto.uid.progettouid.DataBase.DBConnection;
import com.progetto.uid.progettouid.View.SceneHandler;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.util.concurrent.*;

public class MainApplication extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            DBConnection.getInstance().createConnection();
            SceneHandler.getInstance().init(primaryStage);
        } catch(Exception e) {
            e.printStackTrace();
        }
        primaryStage.setOnCloseRequest(event -> {
            Platform.runLater(() -> {
                try {
                    Future<?> future = DBConnection.getInstance().clearCart();
                    future.get();
                    DBConnection.getInstance().closeConnection();
                    DBConnection.getInstance().close();
                } catch (SQLException | InterruptedException | ExecutionException e) {
                    throw new RuntimeException(e);
                }
            });
        });
    }

    public static void main(String[] args) {
        launch(args);
    }
}