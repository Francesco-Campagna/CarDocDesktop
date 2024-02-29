package com.progetto.uid.progettouid.Controller;

import com.progetto.uid.progettouid.Controller.Handler.ProductHandler;
import com.progetto.uid.progettouid.DataBase.DBConnection;
import com.progetto.uid.progettouid.MainApplication;
import com.progetto.uid.progettouid.Message;
import com.progetto.uid.progettouid.Model.Product;
import com.progetto.uid.progettouid.View.SceneHandler;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;


public class BoxPageController {

    @FXML
    private HBox buttomBoxPage;
    @FXML
    private Text priceText1, priceText2, priceText3, priceText4, priceText5, priceText6, priceText7, priceText8, priceText9, priceText10, priceText11, priceText12, priceText13, priceText14, priceText15;
    @FXML
    private ImageView productImage1, productImage2, productImage3, productImage4, productImage5, productImage6, productImage7, productImage8, productImage9, productImage10, productImage11, productImage12, productImage13, productImage14, productImage15;
    @FXML
    private Text titleText1, titleText2, titleText3, titleText4, titleText5, titleText6, titleText7, titleText8, titleText9, titleText10, titleText11, titleText12, titleText13, titleText14, titleText15;
    @FXML
    private Text sellerText1, sellerText2, sellerText3, sellerText4, sellerText5, sellerText6, sellerText7, sellerText8, sellerText9, sellerText10, sellerText11, sellerText12, sellerText13, sellerText14, sellerText15;


    ImageView[] productImages;
    Text[] titleTextArray;
    Text[] priceArray;
    Text[] sellerTextArray;
    VBox[] vBoxes;
    @FXML
    private VBox totalVbox, vBoxProduct1, vBoxProduct2, vBoxProduct3, vBoxProduct4, vBoxProduct5, vBoxProduct6, vBoxProduct7, vBoxProduct8, vBoxProduct9, vBoxProduct10, vBoxProduct11, vBoxProduct12, vBoxProduct13, vBoxProduct14, vBoxProduct15, totalBox;
    @FXML
    private HBox hBox1, hBox2, hBox3;
    @FXML
    private ScrollPane scrollPaneBoxPage;

    void arrayInitialize(){
        productImages = new ImageView[]{productImage1, productImage2, productImage3, productImage4, productImage5, productImage6, productImage7, productImage8, productImage9, productImage10, productImage11, productImage12, productImage13, productImage14, productImage15};
        titleTextArray = new Text[]{titleText1, titleText2, titleText3, titleText4, titleText5, titleText6, titleText7, titleText8, titleText9, titleText10, titleText11, titleText12, titleText13, titleText14, titleText15};
        priceArray = new Text[]{priceText1, priceText2, priceText3, priceText4, priceText5, priceText6, priceText7, priceText8, priceText9, priceText10, priceText11, priceText12, priceText13, priceText14, priceText15};
        sellerTextArray = new Text[]{sellerText1,sellerText2,sellerText3, sellerText4,sellerText5,sellerText6,sellerText7,sellerText8,sellerText9,sellerText10,sellerText11,sellerText12,sellerText13,sellerText14,sellerText15};
        vBoxes = new VBox[]{vBoxProduct1, vBoxProduct2, vBoxProduct3, vBoxProduct4, vBoxProduct5, vBoxProduct6, vBoxProduct7, vBoxProduct8, vBoxProduct9, vBoxProduct10, vBoxProduct11, vBoxProduct12, vBoxProduct13, vBoxProduct14, vBoxProduct15, totalBox};
    }

    private void loadProductViewPage(String id, String category) {
        try {
            CompletableFuture<Product> future = DBConnection.getInstance().getProduct(id);
            Product p = future.get(10, TimeUnit.SECONDS);
            ProductHandler.getInstance().setProduct(p);
            DBConnection.getInstance().addSimilarProduct(id, category);
            Platform.runLater(() -> SceneHandler.getInstance().setHomeScene());
        } catch (Exception e){
            SceneHandler.getInstance().showAlert("Error", Message.load_product_view_error,0);
        }
    }

    public void loadHomePageImage(){
        try{
            CompletableFuture<ArrayList<Product>> future = DBConnection.getInstance().addHomePageProducts();
            ArrayList<Product> products = future.get(10, TimeUnit.SECONDS);
            List<Integer> randomNumber = new ArrayList<>();
            for (int i = 0; i < 48; i++){
                randomNumber.add(i);
            }
            Collections.shuffle(randomNumber);
            for (int i = 0; i < 15; i++) {
                String url = "immagini/"+ products.get(randomNumber.get(i)).id()+".jpg";
                Image image = new Image(Objects.requireNonNull(MainApplication.class.getResourceAsStream(url)));
                productImages[i].setImage(image);
                titleTextArray[i].setText(products.get(randomNumber.get(i)).name());
                priceArray[i].setText(products.get(randomNumber.get(i)).price() + "€");
                sellerTextArray[i].setText(products.get(randomNumber.get(i)).nSeller() + " venduti");
                String id = products.get(randomNumber.get(i)).id();
                String category = products.get(randomNumber.get(i)).category();
                vBoxes[i].setOnMouseClicked(event -> loadProductViewPage(id, category));
            }
        } catch (Exception e){
            SceneHandler.getInstance().showAlert("", Message.add_home_page_product_error, 0);
        }
    }

    @FXML
    void initialize(){
            arrayInitialize();
            loadHomePageImage();
    }
}
