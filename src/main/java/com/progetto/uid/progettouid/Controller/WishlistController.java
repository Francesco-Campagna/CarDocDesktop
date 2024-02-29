package com.progetto.uid.progettouid.Controller;

import com.progetto.uid.progettouid.DataBase.Authentication;
import com.progetto.uid.progettouid.DataBase.DBConnection;
import com.progetto.uid.progettouid.MainApplication;
import com.progetto.uid.progettouid.Message;
import com.progetto.uid.progettouid.Model.Product;
import com.progetto.uid.progettouid.View.SceneHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
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


public class WishlistController {
    @FXML
    private Text balanceText, userText, addedToCartText;
    @FXML
    private ImageView wishlistImage1, wishlistImage2, wishlistImage3, wishlistImage4, wishlistImage5, wishlistImage6;
    @FXML
    private Text wishlistPriceProd1, wishlistPriceProd2, wishlistPriceProd3, wishlistPriceProd4, wishlistPriceProd5, wishlistPriceProd6;
    @FXML
    private Button wishlistAddCartButton1, wishlistAddCartButton2, wishlistAddCartButton3, wishlistAddCartButton4, wishlistAddCartButton5, wishlistAddCartButton6;
    @FXML
    private Button wishlistRemoveButton1, wishlistRemoveButton2, wishlistRemoveButton3, wishlistRemoveButton4, wishlistRemoveButton5, wishlistRemoveButton6;
    @FXML
    private Text wishlistTitleProd1, wishlistTitleProd2, wishlistTitleProd3, wishlistTitleProd4, wishlistTitleProd5, wishlistTitleProd6;
    @FXML
    private VBox vBoxWishlist;
    @FXML
    private HBox hBox1, hBox2, hBox3, hBox4, hBox5, hBox6;

    ImageView[] imageViews;
    Text[] titleTextProd;
    Text[] priceText;
    Button[] removeButtons, addCartButtons;
    HBox[] hBoxes;

    void initializeObject(){
        imageViews = new ImageView[]{wishlistImage1, wishlistImage2, wishlistImage3, wishlistImage4, wishlistImage5, wishlistImage6};
        titleTextProd = new Text[]{wishlistTitleProd1, wishlistTitleProd2, wishlistTitleProd3, wishlistTitleProd4, wishlistTitleProd5, wishlistTitleProd6};
        removeButtons = new Button[]{wishlistRemoveButton1, wishlistRemoveButton2, wishlistRemoveButton3, wishlistRemoveButton4, wishlistRemoveButton5, wishlistRemoveButton6};
        addCartButtons = new Button[]{wishlistAddCartButton1, wishlistAddCartButton2, wishlistAddCartButton3, wishlistAddCartButton4, wishlistAddCartButton5, wishlistAddCartButton6};
        priceText = new Text[]{wishlistPriceProd1, wishlistPriceProd2, wishlistPriceProd3, wishlistPriceProd4, wishlistPriceProd5, wishlistPriceProd6};
        hBoxes = new HBox[]{hBox1, hBox2, hBox3, hBox4, hBox5, hBox6};
    }

    void setAccount() throws SQLException, ExecutionException, InterruptedException, TimeoutException {
        String name = Authentication.getInstance().getUser().name();
        String surname = Authentication.getInstance().getUser().surname();
        String email = Authentication.getInstance().getUser().email();
        userText.setText(name + " " + surname);
        CompletableFuture<String> future = DBConnection.getInstance().getBalanceAccount(email);
        String balance = future.get(10, TimeUnit.SECONDS);
        balanceText.setText(balance+"€");
        loadWishlist(email);
    }

    void removeItem(String id, String email) throws Exception {
        for (int i = 0; i < 6; i++){
            hBoxes[i].setVisible(false);
        }
        DBConnection.getInstance().removeSelectedWishlistItem(id, email);
        initialize();
    }

    void addCartItem(String id_prod, String email) throws Exception {
        boolean find = false;
        CompletableFuture<Integer> future = DBConnection.getInstance().checkProductQuantity(email, id_prod, "carrello");
        Integer quantity = future.get(10, TimeUnit.SECONDS);
        if (quantity != 0){
            find = true;
        }
        if (find){
            try {
                DBConnection.getInstance().updateProductQuantity(email, id_prod, quantity+1, "carrello");
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }else{
            try {
                DBConnection.getInstance().insertCartProductIntoDB(email, id_prod, 1);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        };
        createInfoThread();
    }

    void loadWishlist(String email) throws ExecutionException, InterruptedException, TimeoutException {
        CompletableFuture<ArrayList<Product>> future = DBConnection.getInstance().getWishlist(email);
        ArrayList<Product> prod = future.get(10, TimeUnit.SECONDS);
        for (int i = 0; i < prod.size(); i++) {
            String url = "immagini/" + prod.get(i).id() + ".jpg";
            Image image = new Image(Objects.requireNonNull(MainApplication.class.getResourceAsStream(url)));
            imageViews[i].setImage(image);
            titleTextProd[i].setText(prod.get(i).name());
            priceText[i].setText(prod.get(i).price() + "€");

            String id = prod.get(i).id();
            removeButtons[i].setOnMouseClicked(mouseEvent -> {
                try {
                    removeItem(id, email);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });

            addCartButtons[i].setOnMouseClicked(mouseEvent -> {
                try {
                    addCartItem(id, email);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });
            hBoxes[i].setVisible(true);
        }

    }

    void createInfoThread(){
        Thread thread = new Thread(() -> {
            try {
                addedToCartText.setVisible(true);
                Thread.sleep(1500);
                addedToCartText.setVisible(false);
            } catch (InterruptedException e) {
                SceneHandler.getInstance().showAlert("Errore thread", Message.thread_error, 0);
            }
        });
        thread.start();
    }

    @FXML
    void initialize() throws SQLException, ExecutionException, InterruptedException, TimeoutException {
        initializeObject();
        setAccount();
    }


}


