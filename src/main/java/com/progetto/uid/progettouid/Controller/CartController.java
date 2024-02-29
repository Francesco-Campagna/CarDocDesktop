package com.progetto.uid.progettouid.Controller;

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
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class CartController {
    @FXML
    private VBox cartBoxPane, checkOutBox;
    @FXML
    private ScrollPane cartScrollPane;
    @FXML
    private ImageView arrowContinuePurchase, image;
    @FXML
    private Text totalOrderText, yourOrderText, continuePurchaseText, cartItemPriceText, titleItemCart, totalPrice1, totalPrice2, totalPrice3;
    @FXML
    private TextField amountField;
    @FXML
    private Button purchaseButton, lessButton, plusButton, removeItemButton;
    @FXML
    private ImageView cartImage1, cartImage2, cartImage3, cartImage4, cartImage5, cartImage6;
    @FXML
    private Text titleCartProd1, titleCartProd2, titleCartProd3, titleCartProd4, titleCartProd5, titleCartProd6, cartItemPriceText1, cartItemPriceText2, cartItemPriceText3, cartItemPriceText4,cartItemPriceText5, cartItemPriceText6;
    @FXML
    private Button removeItemButton1 ,removeItemButton2 ,removeItemButton3 ,removeItemButton4 ,removeItemButton5, removeItemButton6;
    @FXML
    private Button lessButton1, lessButton2, lessButton3, lessButton4, lessButton5, lessButton6;
    @FXML
    private Button plusButton1, plusButton2, plusButton3, plusButton4, plusButton5, plusButton6;
    @FXML
    private TextField amountField1, amountField2, amountField3, amountField4, amountField5, amountField6;
    @FXML
    private HBox hBox, hBox1, hBox2, hBox3, hBox4, hBox5, hBox6;
    ImageView[] imageViews;
    Text[] titleTextProd;
    Text[] priceText;
    Button[] removeButtons;
    Button[] lessButtons;
    Button[] plusButtons;
    TextField[] amountTextFields;
    HBox[] hBoxes;
    @FXML
    void purchaseAction(ActionEvent event) throws Exception {
        User user = Authentication.getInstance().getUser();
        if (user != null){
            String email = user.email();
            String total_price = DBConnection.getInstance().getTotalCartPrice(email, "carrello");

            CompletableFuture<String> future = DBConnection.getInstance().getBalanceAccount(email);
            String balance = future.get(10, TimeUnit.SECONDS);

            total_price = total_price.replace(",",".");
            balance = balance.replace(",", ".");
            double doubleTotalPrice = Double.parseDouble(total_price);

            double doubleBalance = Double.parseDouble(balance);

            if (doubleTotalPrice != 0.00) {
                if (doubleBalance >= doubleTotalPrice) {
                    DBConnection.getInstance().checkOutOrder(email, String.valueOf(doubleTotalPrice));
                    SceneHandler.getInstance().showAlert("Ordine completato", Message.order_success, 1);
                    initializeHbox();
                    updateTotalPrice(email);
                    DBConnection.getInstance().updateBalance(email, String.valueOf(doubleBalance - doubleTotalPrice));
                } else {
                    SceneHandler.getInstance().showAlert("Saldo insufficiente", Message.insufficient_balance_error, 0);
                }
            }else{
                SceneHandler.getInstance().showAlert("Carrello vuoto", Message.empty_cart, 0);
            }
        }else{
            SceneHandler.getInstance().showAlert("Effettuare l'accesso", Message.not_logged_cart_error, 0);
        }
    }

    @FXML
    void returnHomeAction(MouseEvent event) {
        try {
            SceneHandler.getInstance().setHomeScene();
        } catch (Exception e){
            SceneHandler.getInstance().showAlert("Error" ,Message.returnHome_error, 0);
        }
    }

    private void initializeHbox(){
        for (int i = 0; i < 6; i++) {
            hBoxes[i].setVisible(false);
        }
    }

    void initializeObject(){
        imageViews = new ImageView[]{cartImage1, cartImage2, cartImage3, cartImage4, cartImage5, cartImage6};
        titleTextProd = new Text[]{titleCartProd1, titleCartProd2, titleCartProd3, titleCartProd4, titleCartProd5, titleCartProd6};
        priceText = new Text[]{cartItemPriceText1, cartItemPriceText2, cartItemPriceText3, cartItemPriceText4,cartItemPriceText5, cartItemPriceText6};
        removeButtons = new Button[]{removeItemButton1, removeItemButton2, removeItemButton3, removeItemButton4, removeItemButton5, removeItemButton6};
        lessButtons = new Button[]{lessButton1, lessButton2, lessButton3, lessButton4, lessButton5, lessButton6};
        plusButtons = new Button[]{plusButton1, plusButton2, plusButton3, plusButton4, plusButton5, plusButton6};
        amountTextFields = new TextField[]{amountField1, amountField2, amountField3, amountField4, amountField5, amountField6};
        hBoxes = new HBox[]{hBox1, hBox2, hBox3, hBox4, hBox5, hBox6};
    }


    void lessButtonAction(int id, String email, String id_prod) throws Exception {
        int amount = Integer.parseInt(amountTextFields[id].getText());
        if (amount > 1) {
            amount--;
            amountTextFields[id].setText(String.valueOf(amount));
            DBConnection.getInstance().updateProductQuantity(email, id_prod, amount, "carrello");
        }
        initializeCart();
    }
    void plusButtonAction(int id, String email, String id_prod) throws Exception {
        int amount = Integer.parseInt(amountTextFields[id].getText());
        amount++;
        amountTextFields[id].setText(String.valueOf(amount));
        DBConnection.getInstance().updateProductQuantity(email, id_prod, amount, "carrello");
        initializeCart();
    }

    void removeItem(String id, String email) {
        for (int i = 0; i < 6; i++){
            hBoxes[i].setVisible(false);
        }
        DBConnection.getInstance().removeSelectedCartItem(id, email);
        Platform.runLater(() -> {
            try {
                initialize();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    void initializeCart() throws Exception {
        String email;
        if (Authentication.getInstance().getUser() != null) {
            email = Authentication.getInstance().getUser().email();
        }
        else{
            email = "null";
            }

        CompletableFuture<ArrayList<String>> future = DBConnection.getInstance().getCart(email, "carrello");
        ArrayList<String> id_prod = future.get(10, TimeUnit.SECONDS);
        ArrayList<Product> products = DBConnection.getInstance().getCartProductInfo(id_prod);

        for (int i = 0; i < products.size(); i++){
            String url = "immagini/" + products.get(i).id() + ".jpg";
            Image image = new Image(Objects.requireNonNull(MainApplication.class.getResourceAsStream(url)));
            imageViews[i].setImage(image);
            titleTextProd[i].setText(products.get(i).name());
            priceText[i].setText(products.get(i).price() + "€");

            String id = products.get(i).id();
            removeButtons[i].setOnMouseClicked(mouseEvent -> {
                try {
                    removeItem(id, email);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });
            int tmp = i;
            lessButtons[i].setOnMouseClicked(mouseEvent -> {
                try {
                    lessButtonAction(tmp, email, id);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });
            plusButtons[i].setOnMouseClicked(mouseEvent -> {
                try {
                    plusButtonAction(tmp, email, id);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });
            CompletableFuture<Integer> future1 =  DBConnection.getInstance().checkProductQuantity(email,id, "carrello");
            Integer quantity = future1.get(10, TimeUnit.SECONDS);
            amountTextFields[i].setText(String.valueOf(quantity));
            hBoxes[i].setVisible(true);
        }

        updateTotalPrice(email);
    }


    void updateTotalPrice(String email) throws SQLException, ExecutionException, InterruptedException, TimeoutException {
        String totalCartPrice = DBConnection.getInstance().getTotalCartPrice(email, "carrello");
        totalCartPrice = totalCartPrice+"€";
        totalPrice1.setText(totalCartPrice);
        totalPrice2.setText(totalCartPrice);
        totalPrice3.setText(totalCartPrice);

    }

    @FXML
    void initialize() throws Exception {
        initializeObject();
        initializeCart();
    }
}
