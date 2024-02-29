package com.progetto.uid.progettouid.Controller;

import com.progetto.uid.progettouid.Controller.Handler.ProductHandler;
import com.progetto.uid.progettouid.DataBase.Authentication;
import com.progetto.uid.progettouid.DataBase.DBConnection;
import com.progetto.uid.progettouid.MainApplication;
import com.progetto.uid.progettouid.Message;
import com.progetto.uid.progettouid.Model.User;
import com.progetto.uid.progettouid.View.SceneHandler;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Objects;
import java.util.concurrent.*;

public class HomePageController {
    @FXML
    private AnchorPane totalPane;
    private Button searchButton;
    @FXML
    private HBox accessHbox;
    @FXML
    private Pane bottomPane, upperPane;
    @FXML
    private ImageView garageIcon, loginIcon, logoutImage, cartIcon, carDocIcon, wishlistIcon;
    @FXML
    private Text lightingText, loginText, priceText, carDocInformation, changeTheme, generalCondition, privacyInformation;
    @FXML
    private Separator logoutSeparator;
    @FXML
    private Text oilAndLiquidsText, textUpgrade, tyresText, filtersText,  exhaustText, cushionsText, cleanlinessCarText, brakesText;
    @FXML
    private TextField searchBar;
    @FXML
    void carDocInformationAction(MouseEvent event) {
        try{
            SceneHandler.getInstance().showAlert("Informazioni su di noi", Message.app_information, 1);
        } catch (Exception e){
            SceneHandler.getInstance().showAlert("Error", Message.app_information_error, 0);
        }
    }
    @FXML
    void changeThemeAction(MouseEvent event) {
        SceneHandler.getInstance().changeTheme();
    }

    @FXML
    void generalConditionAction(MouseEvent event) {
        try{
            SceneHandler.getInstance().showAlert("Condizioni generali di uso e vendita", Message.general_condition, 1);
        } catch (Exception e){
            SceneHandler.getInstance().showAlert("Error", Message.general_condition_error, 0);
        }
    }

    @FXML
    void upgradeInformation(MouseEvent event) {
        try{
            SceneHandler.getInstance().showAlert("Upgrade a premium", Message.upgrade_information, 1);
        } catch (Exception e){
            SceneHandler.getInstance().showAlert("Error", Message.upgrade_information_error, 0);
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
    void LoginAction(MouseEvent event) {
        try {
            if (!Authentication.getInstance().settedUser()) {
                ProductHandler.getInstance().setNullProduct();
                SceneHandler.getInstance().setLoginScene();
            }
            else{
                bottomPane.getChildren().setAll((Node) FXMLLoader.load(Objects.requireNonNull(MainApplication.class.getResource("fxmls/account.fxml"))));
                initializeCartText();
            }
        } catch (Exception e){
            SceneHandler.getInstance().showAlert("Error", Message.login_error, 0);
        }
    }

    @FXML
    void cartAction(MouseEvent event) {
        try {
            bottomPane.getChildren().setAll((Node) FXMLLoader.load(Objects.requireNonNull(MainApplication.class.getResource("fxmls/cart.fxml"))));
            priceText.setVisible(false);
        } catch (Exception e){
            SceneHandler.getInstance().showAlert("Error", Message.cart_error, 0);
        }
    }


    @FXML
    void garageAction(MouseEvent event) {
        try {
            if (Authentication.getInstance().settedUser()) {
                bottomPane.getChildren().setAll((Node) FXMLLoader.load(Objects.requireNonNull(MainApplication.class.getResource("fxmls/garage.fxml"))));
                initializeCartText();
            }
            else {
                SceneHandler.getInstance().showAlert("Error", Message.not_logged_in_error, 0);
            }
        } catch (Exception e){
            SceneHandler.getInstance().showAlert("Error", Message.cart_error, 0);
        }
    }


    @FXML
    void getHomeAction(MouseEvent event) {
        try {
            ProductHandler.getInstance().setProduct(null);
            SceneHandler.getInstance().setHomeScene();
        } catch (Exception e){
            SceneHandler.getInstance().showAlert("Error", Message.returnHome_error, 0);
        }
    }


    @FXML
    void logoutAction(MouseEvent event) throws SQLException, ExecutionException, InterruptedException, TimeoutException {
        ProductHandler.getInstance().setNullProduct();
        Authentication.getInstance().logout();
        initialize();
    }

    @FXML
    void wishlistAction(MouseEvent event) {
        try {
            if (Authentication.getInstance().settedUser()){
                bottomPane.getChildren().setAll((Node) FXMLLoader.load(Objects.requireNonNull(MainApplication.class.getResource("fxmls/wishlist.fxml"))));
                priceText.setVisible(false);
            }
            else {
                SceneHandler.getInstance().showAlert("Errore", Message.not_logged_in_error, 0);
            }
        } catch (Exception e){
            SceneHandler.getInstance().showAlert("Errore", Message.wishlist_error, 0);
        }
    }

    @FXML
    void brakesAction(MouseEvent event) {
        try{
            DBConnection.getInstance().addCategoryProducts("freni");
            bottomPane.getChildren().setAll((Node) FXMLLoader.load(Objects.requireNonNull(MainApplication.class.getResource("fxmls/category_box.fxml"))));
            initializeCartText();
        } catch (Exception e){
            SceneHandler.getInstance().showAlert("Errore", Message.load_page_error, 0);
        }
    }

    @FXML
    void cleanlinessCarAction(MouseEvent event) {
        try{
            DBConnection.getInstance().addCategoryProducts("pulizia");
            bottomPane.getChildren().setAll((Node) FXMLLoader.load(Objects.requireNonNull(MainApplication.class.getResource("fxmls/category_box.fxml"))));
            initializeCartText();
        } catch (Exception e){
            SceneHandler.getInstance().showAlert("Errore", Message.load_page_error, 0);
        }
    }

    @FXML
    void cushionsAction(MouseEvent event) {
        try{
            DBConnection.getInstance().addCategoryProducts("ammortizzatori");
            bottomPane.getChildren().setAll((Node) FXMLLoader.load(Objects.requireNonNull(MainApplication.class.getResource("fxmls/category_box.fxml"))));
            initializeCartText();
        } catch (Exception e){
            SceneHandler.getInstance().showAlert("Errore", Message.load_page_error, 0);
        }

    }

    @FXML
    void exhaustAction(MouseEvent event) {
        try{
            DBConnection.getInstance().addCategoryProducts("scarico");
            bottomPane.getChildren().setAll((Node) FXMLLoader.load(Objects.requireNonNull(MainApplication.class.getResource("fxmls/category_box.fxml"))));
            initializeCartText();
        } catch (Exception e){
            SceneHandler.getInstance().showAlert("Errore", Message.load_page_error, 0);
        }
    }

    @FXML
    void filtersAction(MouseEvent event) {
        try{
            DBConnection.getInstance().addCategoryProducts("filtri");
            bottomPane.getChildren().setAll((Node) FXMLLoader.load(Objects.requireNonNull(MainApplication.class.getResource("fxmls/category_box.fxml"))));
            initializeCartText();
        } catch (Exception e){
            SceneHandler.getInstance().showAlert("Errore", Message.load_page_error, 0);
        }
    }
    @FXML
    void lightingAction(MouseEvent event) {
        try{
            DBConnection.getInstance().addCategoryProducts("illuminazione");
            bottomPane.getChildren().setAll((Node) FXMLLoader.load(Objects.requireNonNull(MainApplication.class.getResource("fxmls/category_box.fxml"))));
            initializeCartText();
        } catch (Exception e){
            SceneHandler.getInstance().showAlert("Errore", Message.load_page_error, 0);
        }
    }
    @FXML
    void oilAndLiquidsAction(MouseEvent event) {
        try{
            DBConnection.getInstance().addCategoryProducts("oli");
            bottomPane.getChildren().setAll((Node) FXMLLoader.load(Objects.requireNonNull(MainApplication.class.getResource("fxmls/category_box.fxml"))));
            initializeCartText();
        } catch (Exception e){
            SceneHandler.getInstance().showAlert("Errore", Message.load_page_error, 0);
        }
    }

    @FXML
    void tyresAction(MouseEvent event) {
        try{
            DBConnection.getInstance().addCategoryProducts("pneumatici");
            bottomPane.getChildren().setAll((Node) FXMLLoader.load(Objects.requireNonNull(MainApplication.class.getResource("fxmls/category_box.fxml"))));
            initializeCartText();
        } catch (Exception e){
            SceneHandler.getInstance().showAlert("Errore", Message.load_page_error, 0);
        }
    }


    void login() throws SQLException, ExecutionException, InterruptedException, TimeoutException {
        logoutImage.setVisible(true);
        loginText.setText("Account");
        String email = Authentication.getInstance().getUser().email();
        Future<?> future = DBConnection.getInstance().updateNullProduct(email);
        future.get(10, TimeUnit.SECONDS);
        updateCartPrice();
    }

    void logout() throws SQLException, ExecutionException, InterruptedException, TimeoutException {
        logoutImage.setVisible(false);
        loginText.setText("Accesso");
        updateCartPrice();
    }

    void updateCartPrice() throws SQLException, ExecutionException, InterruptedException, TimeoutException {
        User user = Authentication.getInstance().getUser();
        if (user != null){
            String totalCartPrice = DBConnection.getInstance().getTotalCartPrice(user.email(), "carrello");
            priceText.setText(totalCartPrice+"€");
        }else{
            String totalCartPriceNull = DBConnection.getInstance().getTotalCartPrice("null", "carrello");
            priceText.setText(totalCartPriceNull+"€");
        }
    }

    @FXML
    void searchButtonAction(ActionEvent event) throws ExecutionException, InterruptedException, TimeoutException {
        String text = searchBar.getText().toLowerCase();
        String textWithOutSpace = text.trim();
        if (!text.isEmpty() && !textWithOutSpace.isEmpty()){
            CompletableFuture<Label> future = DBConnection.getInstance().searchProduct(text);
            Label searchedProduct = future.get(10, TimeUnit.SECONDS);
            String searchedText = searchedProduct.getText();
            String[] prod = searchedText.split(";");
            DBConnection.getInstance().addSearchedProducts(prod);
            try{
                bottomPane.getChildren().setAll((Node) FXMLLoader.load(Objects.requireNonNull(MainApplication.class.getResource("fxmls/category_box.fxml"))));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        searchBar.setText("");
    }

    void initializeCartText() throws SQLException, ExecutionException, InterruptedException, TimeoutException {
        priceText.setVisible(true);
        updateCartPrice();
    }

    @FXML
    void enterPressed(KeyEvent event) throws Exception {
        if (event.getCode() == KeyCode.ENTER){
            ActionEvent ignored = new ActionEvent();
            searchButtonAction(ignored);
        }
    }

    @FXML
    void initialize() throws SQLException, ExecutionException, InterruptedException, TimeoutException {
        try{
            if (ProductHandler.getInstance().getProduct() != null){
                bottomPane.getChildren().setAll((Node) FXMLLoader.load(Objects.requireNonNull(MainApplication.class.getResource("fxmls/product_view.fxml"))));
            }else{
                bottomPane.getChildren().setAll((Node) FXMLLoader.load(Objects.requireNonNull(MainApplication.class.getResource("fxmls/box_page.fxml"))));
            }
        } catch (Exception e){
            SceneHandler.getInstance().showAlert("Error", Message.box_page_error, 0);
        }

        if (Authentication.getInstance().settedUser()){
            login();
        }
        else{
            logout();
        }
        initializeCartText();
    }

}

