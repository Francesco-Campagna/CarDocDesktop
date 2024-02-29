package com.progetto.uid.progettouid.Controller;

import com.progetto.uid.progettouid.Controller.Handler.ProductHandler;
import com.progetto.uid.progettouid.DataBase.Authentication;
import com.progetto.uid.progettouid.DataBase.DBConnection;
import com.progetto.uid.progettouid.MainApplication;
import com.progetto.uid.progettouid.Message;
import com.progetto.uid.progettouid.Model.Product;
import com.progetto.uid.progettouid.Model.User;
import com.progetto.uid.progettouid.View.SceneHandler;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class ProductViewController {
    @FXML
    private Button addToCartButton, addToWishlistButton;
    @FXML
    private TextField amountField;
    @FXML
    private Text amountText, availabilityText, carDocText, descriptionField, freeReturnsText, freeShippingText, paymentText, productField, secureTransationText, seller, sellerText, shippingText, titleSimilarProductText1, titleSimilarProductText2, titleSimilarProductText3, titleSimilarProductText4, titleSimilarProductText5, SimilarProducts;
    @FXML
    private Button lessButton, plusButton;
    @FXML
    private Text priceText, priceText1, priceText2, priceText3, priceText4, priceText5;
    @FXML
    private ImageView productImage, productImage1, productImage2, productImage3, productImage4, productImage5;
    @FXML
    private VBox totalVbox, actionBox, vBoxSimilarProduct1, vBoxSimilarProduct2, vBoxSimilarProduct3, vBoxSimilarProduct4, vBoxSimilarProduct5;
    @FXML
    private HBox productImageBox;

    @FXML
    void addToCartButtonAction(ActionEvent event) throws Exception {
        boolean find = false;
        String email_user;
        if (Authentication.getInstance().settedUser()){
            email_user = Authentication.getInstance().getUser().email();
        }else{
            email_user = "null";
        }

        String id_prodotto = ProductHandler.getInstance().getProduct().id();
        CompletableFuture<Integer> future = DBConnection.getInstance().checkProductQuantity(email_user, id_prodotto, "carrello");
        Integer quantity = future.get(10, TimeUnit.SECONDS);
        if (quantity != 0){
            find = true;
        }
        if (Integer.parseInt(amountField.getText()) != 1){
            quantity = quantity + Integer.parseInt(amountField.getText());
        }else{
            quantity += 1;
        }
        if (find){
            try {
                DBConnection.getInstance().updateProductQuantity(email_user, id_prodotto, quantity, "carrello");
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }else{
            try {
                DBConnection.getInstance().insertCartProductIntoDB(email_user, id_prodotto, quantity);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        Platform.runLater(() -> SceneHandler.getInstance().setHomeScene());
    }

    @FXML
    void addToWishlistButtonAction(ActionEvent event) {
        User user = Authentication.getInstance().getUser();
        if (user != null){
            String email = user.email();
            String id_prod = ProductHandler.getInstance().getProduct().id();
            DBConnection.getInstance().insertWishlistProductIntoDB(email, id_prod);
        }else{
            SceneHandler.getInstance().showAlert("Error", Message.not_logged_in_error,0);
        }
    }

    ImageView[] imageViews;
    Text[] priceTexts;
    VBox[] similarProductVBox;
    Text[] similarProductText;

    @FXML
    void lessButtonAction(ActionEvent event) {
        int amount = Integer.parseInt(amountField.getText());
        if (amount > 1){
            amount--;
            amountField.setText(String.valueOf(amount));
        }
    }

    @FXML
    void plusButtonAction(ActionEvent event) {
        int amount = Integer.parseInt(amountField.getText());
        amount++;
        amountField.setText(String.valueOf(amount));
    }

    void initializeArrays(){
        imageViews = new ImageView[]{productImage1, productImage2, productImage3, productImage4, productImage5};
        priceTexts = new Text[]{priceText1, priceText2, priceText3, priceText4, priceText5};
        similarProductVBox = new VBox[]{vBoxSimilarProduct1, vBoxSimilarProduct2, vBoxSimilarProduct3, vBoxSimilarProduct4, vBoxSimilarProduct5};
        similarProductText = new Text[]{titleSimilarProductText1, titleSimilarProductText2, titleSimilarProductText3, titleSimilarProductText4, titleSimilarProductText5};
    }

    void loadProduct() {
        Product p = ProductHandler.getInstance().getProduct();
        String url = "immagini/"+ p.id() + ".jpg";
        Image image = new Image(Objects.requireNonNull(MainApplication.class.getResourceAsStream(url)));
        productImage.setImage(image);
        productField.setText(p.name());
        descriptionField.setText(p.description());
        priceText.setText(p.price() + "€");
        sellerText.setText(p.seller());
    }


    void loadSimilarProductViewPage(String id, String category){
        try {
            CompletableFuture<Product> future = DBConnection.getInstance().getProduct(id);
            Product p = future.get(10, TimeUnit.SECONDS);

            DBConnection.getInstance().addSimilarProduct(id, category);

            ProductHandler.getInstance().setProduct(p);
            Platform.runLater(() -> SceneHandler.getInstance().setHomeScene());

        } catch (Exception e){
            SceneHandler.getInstance().showAlert("Error", Message.load_product_view_error,0);
        }
    }


    void loadSimilarProducts() {
        ArrayList<Product> products = DBConnection.getInstance().getSimilarProducts();
        for (int i = 0; i < products.size(); i++) {
            String url = "immagini/" + products.get(i).id() + ".jpg";
            Image image = new Image(Objects.requireNonNull(MainApplication.class.getResourceAsStream(url)));
            imageViews[i].setImage(image);
            similarProductText[i].setText(products.get(i).name());
            priceTexts[i].setText(products.get(i).price() + "€");

            String id = products.get(i).id();
            String category = products.get(i).category();
            similarProductVBox[i].setOnMouseClicked(event -> loadSimilarProductViewPage(id, category));
        }
    }


    @FXML
    void initialize(){
        initializeArrays();
        loadProduct();
        loadSimilarProducts();
    }

}
