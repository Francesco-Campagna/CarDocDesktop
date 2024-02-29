package com.progetto.uid.progettouid.Controller;

import com.progetto.uid.progettouid.Controller.Handler.ProductHandler;
import com.progetto.uid.progettouid.DataBase.DBConnection;
import com.progetto.uid.progettouid.MainApplication;
import com.progetto.uid.progettouid.Message;
import com.progetto.uid.progettouid.Model.Product;
import com.progetto.uid.progettouid.View.SceneHandler;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class CategoryBoxController {

    @FXML
    private VBox vBoxProduct1, vBoxProduct2, vBoxProduct3, vBoxProduct4, vBoxProduct5, vBoxProduct6, vBoxProduct7, vBoxProduct8, vBoxProduct9, vBoxProduct10, vBoxProduct11, vBoxProduct12, vBoxProduct13, vBoxProduct14, vBoxProduct15;
    @FXML
    private Text sellerText1, sellerText2, sellerText3, sellerText4, sellerText5, sellerText6, sellerText7, sellerText8, sellerText9, sellerText10, sellerText11, sellerText12, sellerText13, sellerText14, sellerText15;
    @FXML
    private Text titleText1, titleText2, titleText3, titleText4, titleText5, titleText6, titleText7, titleText8, titleText9, titleText10, titleText11, titleText12, titleText13, titleText14, titleText15;
    @FXML
    private Text priceText1, priceText2, priceText3, priceText4, priceText5, priceText6, priceText7, priceText8, priceText9, priceText10, priceText11, priceText12, priceText13, priceText14, priceText15;
    @FXML
    private ImageView productImage1, productImage2, productImage3, productImage4, productImage5, productImage6, productImage7, productImage8, productImage9, productImage10, productImage11, productImage12, productImage13, productImage14, productImage15;
    @FXML
    private HBox hBox1, hBox2;
    ImageView[] productImagesArray;
    Text[] titleTextArray;
    Text[] priceArray;
    Text[] sellerTextArray;
    VBox[] vBoxArray;

    void arrayInitialize(){
        vBoxArray = new VBox[]{vBoxProduct1, vBoxProduct2, vBoxProduct3, vBoxProduct4, vBoxProduct5, vBoxProduct6, vBoxProduct7, vBoxProduct8, vBoxProduct9, vBoxProduct10, vBoxProduct11,  vBoxProduct12, vBoxProduct13, vBoxProduct14, vBoxProduct15};
        productImagesArray = new ImageView[]{productImage1, productImage2, productImage3, productImage4, productImage5, productImage6, productImage7, productImage8, productImage9, productImage10, productImage11, productImage12, productImage13, productImage14, productImage15};
        titleTextArray = new Text[]{titleText1, titleText2, titleText3, titleText4, titleText5, titleText6, titleText7, titleText8, titleText9, titleText10, titleText11, titleText12, titleText13, titleText14, titleText15};
        priceArray = new Text[]{priceText1, priceText2, priceText3, priceText4, priceText5, priceText6, priceText7, priceText8, priceText9, priceText10, priceText11, priceText12, priceText13, priceText14, priceText15};
        sellerTextArray = new Text[]{sellerText1, sellerText2, sellerText3, sellerText4, sellerText5, sellerText6, sellerText7, sellerText8, sellerText9, sellerText10, sellerText11, sellerText12, sellerText13, sellerText14, sellerText15};
    }

    void removeExcessBox(int size){
        for (int i = size; i < 15; i++){
            vBoxArray[i].setVisible(false);
        }
    }

    private void loadProductViewPage(String id, String category) {
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

    void addProduct(ArrayList<Product> products){
        for (int i = 0; i < products.size(); i++){
            String url = "immagini/"+ products.get(i).id()+".jpg";
            Image image = new Image(Objects.requireNonNull(MainApplication.class.getResourceAsStream(url)));
            productImagesArray[i].setImage(image);
            titleTextArray[i].setText(products.get(i).name());
            priceArray[i].setText(products.get(i).price()+"€");
            sellerTextArray[i].setText(products.get(i).nSeller() + " venduti");

            String id = products.get(i).id();
            String category = products.get(i).category();
            vBoxArray[i].setOnMouseClicked(event -> loadProductViewPage(id, category));
        }
    }


    @FXML
    void initialize(){
        arrayInitialize();
        ArrayList<Product> products = new ArrayList<>();
        if (DBConnection.getInstance().getSearchedProducts().isEmpty()){
            products = DBConnection.getInstance().getCategoryProducts();
        }else{
            products = DBConnection.getInstance().getSearchedProducts();
        }
        if(products != null){
            removeExcessBox(products.size());
            addProduct(products);
            DBConnection.getInstance().clearSearchedList();
            DBConnection.getInstance().clearCategoryProductList();
        }else{
            removeExcessBox(0);
        }
    }


}



