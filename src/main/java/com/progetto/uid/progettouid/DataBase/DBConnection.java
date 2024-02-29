package com.progetto.uid.progettouid.DataBase;

import com.progetto.uid.progettouid.Message;
import com.progetto.uid.progettouid.Model.Product;
import com.progetto.uid.progettouid.Model.User;
import com.progetto.uid.progettouid.View.SceneHandler;
import javafx.application.Platform;
import javafx.scene.control.Label;
import org.springframework.security.crypto.bcrypt.BCrypt;
import java.sql.*;
import java.util.ArrayList;
import java.util.concurrent.*;

public class DBConnection {
    private static DBConnection instance = null;
    private Connection con = null;
    private ArrayList<Product> categoryProducts;
    private ArrayList<Product> similarProducts;
    private ArrayList<Product> searchedProducts = new ArrayList<>();
    private Label resultLabel;
    private Product product;
    private final ExecutorService executorService = Executors.newCachedThreadPool();

    private DBConnection() {}

    public static DBConnection getInstance() {
        if (instance == null) {
            instance = new DBConnection();
        }
        return instance;
    }

    public void createConnection() throws SQLException {
        String url = "jdbc:sqlite:database.db";
        con = DriverManager.getConnection(url);
        if (con != null && !con.isClosed())
            System.out.println("Connected!");
    }

    public void closeConnection() throws SQLException {
        if (con != null)
            con.close();
        con = null;
    }

    public void close() {
        executorService.shutdownNow();
    }

    public void insertUsers(String name, String surname, String email, String password, String address, int balance) {
        executorService.submit(createDaemonThread(() -> {
            try {
                if (con == null || con.isClosed())
                    return;
                PreparedStatement stmt = con.prepareStatement("INSERT INTO utenti VALUES(?, ?, ?, ?, ?, ?);");
                stmt.setString(1, name);
                stmt.setString(2, surname);
                stmt.setString(3, email);
                stmt.setString(4, password);
                stmt.setString(5, address);
                stmt.setInt(6, balance);
                stmt.execute();
                stmt.close();
            } catch (SQLException e) {
                SceneHandler.getInstance().showAlert("Errore thread", Message.thread_error, 0);
            }
        }));
    }

    public Future<?> checkLogin(String email, String password) throws SQLException {
        return executorService.submit(createDaemonThread(() -> {
            try {
                if (con == null || con.isClosed())
                    return;
                String query = "select * from utenti where email=?";
                PreparedStatement stmt = con.prepareStatement(query);
                stmt.setString(1, email);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    boolean check = BCrypt.checkpw(password, rs.getString("password"));
                    if (check)
                        System.out.println("Password OK");
                    else
                        throw new SQLException();
                } else {
                    throw new SQLException();
                }
                stmt.close();
            } catch (SQLException e) {
                SceneHandler.getInstance().showAlert("Errore thread", Message.thread_error, 0);
            }
        }));
    }

    public CompletableFuture<User> setUser(String email) {
        CompletableFuture<User> future = new CompletableFuture<>();
        executorService.submit(createDaemonThread(() -> {
            try {
                User user;
                if (con == null || con.isClosed())
                    future.complete(null);
                String query = "select * from utenti where email=?;";
                PreparedStatement stmt = con.prepareStatement(query);
                stmt.setString(1, email);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    user = new User(rs.getString("email"), rs.getString("nome"), rs.getString("cognome"), rs.getString("indirizzo"), rs.getString("saldo"));
                    future.complete(user);
                }
                stmt.close();
            } catch (SQLException e) {
                future.completeExceptionally(e);
            }
        }));
        return future;
    }


    public String encryptedPassword(String password) {
        String generatedSecuredPasswordHash = BCrypt.hashpw(password, BCrypt.gensalt(12));
        return generatedSecuredPasswordHash;
    }

    public CompletableFuture<ArrayList<Product>> addHomePageProducts() {
        CompletableFuture<ArrayList<Product>> future = new CompletableFuture<>();
        executorService.submit(createDaemonThread(() -> {
            try {
                ArrayList<Product> products = new ArrayList<>();
                if (this.con != null && !this.con.isClosed()) {
                    String query = "select * from prodotti;";
                    PreparedStatement stmt = this.con.prepareStatement(query);
                    ResultSet rs = stmt.executeQuery();

                    while (rs.next()) {
                        Product p = new Product(rs.getString("id"), rs.getString("nome"), rs.getString("descrizione"), rs.getString("venditore"), rs.getString("prezzo"), rs.getString("categoria"), rs.getString("nSeller"));
                        products.add(p);
                    }
                    future.complete(products);
                    stmt.close();
                }
            } catch (SQLException e) {
                SceneHandler.getInstance().showAlert("Errore thread", Message.thread_error, 0);
            }
        }));
        return future;
    }

    public void addCategoryProducts(String category) {
        executorService.submit(createDaemonThread(() -> {
            try {
                categoryProducts = new ArrayList<>();
                if (this.con != null && !this.con.isClosed()) {
                    String query = "select * from prodotti where categoria=?;";
                    PreparedStatement stmt = this.con.prepareStatement(query);
                    stmt.setString(1, category);
                    ResultSet rs = stmt.executeQuery();

                    while (rs.next()) {
                        Product p = new Product(rs.getString("id"), rs.getString("nome"), rs.getString("descrizione"), rs.getString("venditore"), rs.getString("prezzo"), rs.getString("categoria"), rs.getString("nSeller"));
                        categoryProducts.add(p);
                    }
                }
            } catch (SQLException e) {
                SceneHandler.getInstance().showAlert("Errore thread", Message.thread_error, 0);
            }
        }));
    }

    public ArrayList<Product> getCategoryProducts() {
        return categoryProducts;
    }

    public void clearCategoryProductList() {
        if (categoryProducts != null) {
            categoryProducts.clear();
        }
    }


    public CompletableFuture<Boolean> checkExistEmail(String email) {
        CompletableFuture<Boolean> future = new CompletableFuture<>();
        executorService.submit(createDaemonThread(() -> {
            try {
                if (this.con != null && !this.con.isClosed()) {
                    String query = "SELECT * from utenti WHERE email =?;";
                    PreparedStatement stmt = this.con.prepareStatement(query);
                    stmt.setString(1, email);
                    ResultSet rs = stmt.executeQuery();

                    if (rs.next()) {
                        future.complete(true);
                    } else {
                        future.complete(false);
                    }
                }
            } catch (SQLException e) {
                SceneHandler.getInstance().showAlert("Errore thread", Message.thread_error, 0);
            }
        }));
        return future;
    }

    public CompletableFuture<Product> getProduct(String id) {
        CompletableFuture<Product> future = new CompletableFuture<>();
        executorService.submit(createDaemonThread(() -> {
            try {
                if (this.con != null && !this.con.isClosed()) {
                    String query = "select * from prodotti where id=?;";
                    PreparedStatement stmt = this.con.prepareStatement(query);
                    stmt.setString(1, id);
                    ResultSet rs = stmt.executeQuery();

                    if (rs.next()) {
                        product = new Product(rs.getString("id"), rs.getString("nome"), rs.getString("descrizione"), rs.getString("venditore"), rs.getString("prezzo"), rs.getString("categoria"), rs.getString("nSeller"));
                        future.complete(product);
                    }
                }
            } catch (SQLException e) {
                SceneHandler.getInstance().showAlert("Errore thread", Message.thread_error, 0);
            }
        }));
        return future;
    }


    public void addSimilarProduct(String id, String category) {
        executorService.submit(createDaemonThread(() -> {
            try {
                similarProducts = new ArrayList<>();
                if (this.con != null && !this.con.isClosed()) {
                    String query = "select * from prodotti where categoria=?;";
                    PreparedStatement stmt = this.con.prepareStatement(query);
                    stmt.setString(1, category);
                    ResultSet rs = stmt.executeQuery();

                    while (rs.next() && similarProducts.size() < 5) {
                        if (!rs.getString("id").equals(id)) {
                            product = new Product(rs.getString("id"), rs.getString("nome"), rs.getString("descrizione"), rs.getString("venditore"), rs.getString("prezzo"), rs.getString("categoria"), rs.getString("nSeller"));
                            similarProducts.add(product);
                        }
                    }
                }
            } catch (SQLException e) {
                SceneHandler.getInstance().showAlert("Errore thread", Message.thread_error, 0);
            }
        }));
    }

    public ArrayList<Product> getSimilarProducts() {
        return similarProducts;
    }

    public void insertCartProductIntoDB(String email, String id_product, int quantita) throws SQLException {
        executorService.submit(createDaemonThread(() -> {
            try {
                CompletableFuture<ArrayList<String>> future = getCart(email, "carrello");
                ArrayList<String> nProd = future.get(10, TimeUnit.SECONDS);
                if (nProd.size() < 6) {
                    if (con == null || con.isClosed())
                        return;
                    PreparedStatement stmt = con.prepareStatement("INSERT INTO carrello VALUES(?, ?, ?, ?, ?, ?);");
                    stmt.setString(1, email);
                    stmt.setString(2, id_product);
                    stmt.setInt(3, quantita);
                    stmt.setString(4, "carrello");
                    stmt.setInt(5, 0);
                    stmt.setInt(6, 0);
                    stmt.execute();
                    stmt.close();
                } else {
                    SceneHandler.getInstance().showAlert("Attenzione", Message.add_cart_information, 1);
                }
            } catch (SQLException | ExecutionException | InterruptedException | TimeoutException e) {
                SceneHandler.getInstance().showAlert("Errore thread", Message.thread_error, 0);
            }
        }));
    }

    public CompletableFuture<Integer> checkProductQuantity(String email, String id_prodotto, String tipo) throws SQLException {
        CompletableFuture<Integer> future = new CompletableFuture<>();
        executorService.submit(createDaemonThread(() -> {
            try {
                int quantity = 0;
                if (this.con != null && !this.con.isClosed()) {
                    String query = "select * from carrello where id_cliente=? AND id_prodotto=? AND tipo=?;";
                    PreparedStatement stmt = this.con.prepareStatement(query);
                    stmt.setString(1, email);
                    stmt.setString(2, id_prodotto);
                    stmt.setString(3, tipo);

                    ResultSet rs = stmt.executeQuery();

                    while (rs.next()) {
                        quantity = rs.getInt(3);
                    }
                }
                future.complete(quantity);
            } catch (SQLException e) {
                SceneHandler.getInstance().showAlert("Errore thread", Message.thread_error, 0);
            }
        }));
        return future;
    }

    public void updateProductQuantity(String email, String id_prodotto, int quantity, String tipo) throws SQLException {
        executorService.submit(createDaemonThread(() -> {
            try {
                if (this.con != null && !this.con.isClosed()) {
                    String query = "update carrello set quantita = ? where id_cliente=? AND id_prodotto=? AND tipo=?;";
                    PreparedStatement stmt = this.con.prepareStatement(query);
                    stmt.setInt(1, quantity);
                    stmt.setString(2, email);
                    stmt.setString(3, id_prodotto);
                    stmt.setString(4, tipo);
                    stmt.execute();
                    stmt.close();
                }
            } catch (SQLException e) {
                SceneHandler.getInstance().showAlert("Errore thread", Message.thread_error, 0);
            }
        }));
    }

    public Future<?> clearCart() throws SQLException {
        return executorService.submit(createDaemonThread(() -> {
            try {
                if (con == null || con.isClosed())
                    return;
                String query = "DELETE FROM carrello where id_cliente=?;";
                PreparedStatement stmt = con.prepareStatement(query);
                stmt.setString(1, "null");
                stmt.execute();
                stmt.close();
            } catch (SQLException e) {
                SceneHandler.getInstance().showAlert("Errore thread", Message.thread_error, 0);
            }
        }));
    }

    public CompletableFuture<Label> searchProduct(String searchText) {
        CompletableFuture<Label> future = new CompletableFuture<>();
        executorService.submit(createDaemonThread(() -> {
            try {
                resultLabel = new Label();
                if (this.con != null && !this.con.isClosed()) {
                    String query = "SELECT * FROM prodotti WHERE chiavi LIKE ?;";
                    PreparedStatement stmt = con.prepareStatement(query);
                    stmt.setString(1, "%" + searchText + "%");

                    ResultSet resultSet = stmt.executeQuery();
                    StringBuilder resultBuilder = new StringBuilder();
                    while (resultSet.next()) {
                        String columnValue = resultSet.getString("id");
                        resultBuilder.append(columnValue + ";");
                    }

                    resultLabel.setText(resultBuilder.toString());
                    future.complete(resultLabel);
                }
            } catch (SQLException e) {
                SceneHandler.getInstance().showAlert("Errore thread", Message.thread_error, 0);
            }
        }));
        return future;
    }

    public void addSearchedProducts(String[] products) {
        executorService.submit(createDaemonThread(() -> {
            try {
                if (this.con != null && !this.con.isClosed()) {
                    for (String id : products) {
                        String query = "select * from prodotti where id= ?;";
                        PreparedStatement stmt = this.con.prepareStatement(query);
                        stmt.setString(1, id);

                        ResultSet rs = stmt.executeQuery();

                        while (rs.next()) {
                            Product p = new Product(rs.getString("id"), rs.getString("nome"), rs.getString("descrizione"), rs.getString("venditore"), rs.getString("prezzo"), rs.getString("categoria"), rs.getString("nSeller"));
                            searchedProducts.add(p);
                        }
                    }
                }
            } catch (SQLException e) {
                SceneHandler.getInstance().showAlert("Errore thread", Message.thread_error, 0);
            }
        }));
    }

    public ArrayList<Product> getSearchedProducts() {
        return searchedProducts;
    }

    public void clearSearchedList() {
        if (searchedProducts != null) {
            searchedProducts.clear();
        }
    }

    public CompletableFuture<Boolean> checkExistigCar(String email, String car) {
        CompletableFuture<Boolean> future = new CompletableFuture<>();
        executorService.submit(createDaemonThread(() -> {
            try {
                if (con == null || con.isClosed())
                    future.complete(true);
                PreparedStatement stmt = con.prepareStatement("SELECT email, auto FROM utente_auto WHERE email = ? and auto = ?");
                stmt.setString(1, email);
                stmt.setString(2, car);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()){
                    future.complete(true);
                }else{
                    future.complete(false);
                }
                rs.close();
                stmt.close();
            } catch (SQLException e) {
                SceneHandler.getInstance().showAlert("Errore thread", Message.thread_error, 0);
            }
        }));
        return future;
    }

    public CompletableFuture<Boolean> addCar(String email, String car) {
        CompletableFuture<Boolean> future = new CompletableFuture<>();
        executorService.submit(createDaemonThread(() -> {
            try {
                CompletableFuture<Boolean> future1 = checkExistigCar(email, car);
                Boolean exists = future1.get(10, TimeUnit.SECONDS);
                if (!exists) {
                    if (con == null || con.isClosed())
                        return;
                    String query = "INSERT into utente_auto values (?,?)";
                    PreparedStatement stmt = con.prepareStatement(query);
                    stmt.setString(1, email);
                    stmt.setString(2, car);
                    stmt.execute();
                    stmt.close();
                }
                future.complete(exists);
            } catch (SQLException | ExecutionException | InterruptedException | TimeoutException e) {
                SceneHandler.getInstance().showAlert("Errore thread", Message.thread_error, 0);
            }
        }));
        return future;
    }

    public CompletableFuture<ArrayList<String>> getCar(String email) {
        CompletableFuture<ArrayList<String>> future = new CompletableFuture<>();
        executorService.submit(createDaemonThread(() -> {
            try {
                ArrayList<String> car = new ArrayList<>();
                if (con == null || con.isClosed())
                    future.complete(car);
                String query = "SELECT * from utente_auto where email=?";
                PreparedStatement stmt = con.prepareStatement(query);
                stmt.setString(1, email);
                ResultSet rs = stmt.executeQuery();

                while (rs.next()) {
                    car.add(rs.getString(2));
                }
                future.complete(car);
            } catch (SQLException e) {
                SceneHandler.getInstance().showAlert("Errore thread", Message.thread_error, 0);
            }
        }));
        return future;
    }

    public void removeSelectedCar(int id, String email) {
        executorService.submit(createDaemonThread(() -> {
            try {
                if (con == null || con.isClosed())
                    return;
                String query = "SELECT * from utente_auto where email=?";
                PreparedStatement stmt = con.prepareStatement(query);
                stmt.setString(1, email);
                ResultSet rs = stmt.executeQuery();
                int cont = 0;
                String car = null;
                while (rs.next()) {
                    if (cont == id) car = (rs.getString(2));
                    cont++;
                }

                query = "DELETE FROM utente_auto where email=? and auto = ?;";
                stmt = con.prepareStatement(query);
                stmt.setString(1, email);
                stmt.setString(2, car);
                stmt.execute();
                stmt.close();
            } catch (SQLException e) {
                SceneHandler.getInstance().showAlert("Errore thread", Message.thread_error, 0);
            }
        }));
    }


    public CompletableFuture<ArrayList<String>> getCart(String email, String tipo) {
        CompletableFuture<ArrayList<String>> future = new CompletableFuture<>();
        executorService.submit(createDaemonThread(() -> {
            try {
                ArrayList<String> id_prod = new ArrayList<>();
                if (con == null || con.isClosed())
                    future.complete(id_prod);
                String query = "SELECT * from carrello where id_cliente=? and tipo =?";
                PreparedStatement stmt = con.prepareStatement(query);
                stmt.setString(1, email);
                stmt.setString(2, tipo);

                ResultSet rs = stmt.executeQuery();

                while (rs.next()) {
                    id_prod.add(rs.getString(2));
                }
                future.complete(id_prod);
            } catch (SQLException e) {
                SceneHandler.getInstance().showAlert("Errore thread", Message.thread_error, 0);
            }
        }));
        return future;
    }

    public ArrayList getCartProductInfo(ArrayList<String> id_prod) throws ExecutionException, InterruptedException, TimeoutException {
        ArrayList<Product> prod = new ArrayList<>();
        for (String id : id_prod) {
            CompletableFuture<Product> future = getProduct(id);
            Product product = future.get(10, TimeUnit.SECONDS); // Attendere il completamento del CompletableFuture
            prod.add(product);
        }
        return prod;
    }

    public void removeSelectedCartItem(String id, String email) {
        executorService.submit(createDaemonThread(() -> {
            try {
                if (con == null || con.isClosed())
                    return;
                String query = "DELETE FROM carrello where id_cliente=? and id_prodotto = ? and tipo=?;";
                PreparedStatement stmt = con.prepareStatement(query);
                stmt.setString(1, email);
                stmt.setString(2, id);
                stmt.setString(3, "carrello");

                stmt.execute();
                stmt.close();
            } catch (SQLException e) {
                SceneHandler.getInstance().showAlert("Errore thread", Message.thread_error, 0);
            }
        }));

    }

    public double checkTotalPrice(String email, String tipo) throws SQLException, ExecutionException, InterruptedException, TimeoutException {
        CompletableFuture<ArrayList<String>> future = getCart(email, tipo);
        ArrayList<String> id_prod = future.get(10, TimeUnit.SECONDS);
        ArrayList<Product> products = getCartProductInfo(id_prod);
        double totalPriceQuantity = 0.00;

        for (int i = 0; i < id_prod.size(); i++) {
            CompletableFuture<Integer> future1 = checkProductQuantity(email, id_prod.get(i), tipo);
            Integer q = future1.get(10, TimeUnit.SECONDS);
            String price = products.get(i).price();
            price = price.replace(",", ".");
            double tmp = q * Double.parseDouble(price);
            totalPriceQuantity += tmp;
        }
        return totalPriceQuantity;
    }

    public String getTotalCartPrice(String email, String tipo) throws SQLException, ExecutionException, InterruptedException, TimeoutException {
        double tmpTotalCartPrice = checkTotalPrice(email, tipo);
        String str = String.format("%.2f", tmpTotalCartPrice).replace(".", ",");
        return str;
    }

    public Future<?> updateNullProduct(String email) {
        return executorService.submit(createDaemonThread(() -> {
            try {
                CompletableFuture<ArrayList<String>> future = getCart("null", "carrello");
                ArrayList<String> id_prod = future.get(10, TimeUnit.SECONDS);

                for (String id : id_prod) {
                    CompletableFuture<Integer> future1 = checkProductQuantity("null", id, "carrello");
                    Integer quantity = future1.get(10, TimeUnit.SECONDS);
                    insertCartProductIntoDB(email, id, quantity);
                }
                if (this.con != null && !this.con.isClosed()) {
                    String query = "delete from carrello where id_cliente = ?;";
                    PreparedStatement stmt = this.con.prepareStatement(query);
                    stmt.setString(1, "null");
                    stmt.execute();
                    stmt.close();
                }
            } catch (SQLException | ExecutionException | InterruptedException | TimeoutException e) {
                SceneHandler.getInstance().showAlert("Errore thread", Message.thread_error, 0);
            }
        }));
    }

    public CompletableFuture<String> getBalanceAccount(String email) {
        CompletableFuture<String> future = new CompletableFuture<>();
        executorService.submit(createDaemonThread(() -> {
            try {
                String balance = "0.00";
                if (con == null || con.isClosed())
                    future.complete(balance);
                String query = "SELECT saldo from utenti where email=?";
                PreparedStatement stmt = con.prepareStatement(query);
                stmt.setString(1, email);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    balance = rs.getString("saldo");
                }
                if (balance == "0") {
                    balance = "0.00";
                }
                future.complete(balance);
            } catch (SQLException e) {
                SceneHandler.getInstance().showAlert("Errore thread", Message.thread_error, 0);
            }
        }));
        return future;
    }

    public CompletableFuture<ArrayList<Product>> getWishlist(String email) {
        CompletableFuture<ArrayList<Product>> future = new CompletableFuture<>();
        executorService.submit(createDaemonThread(() -> {
            try {
                ArrayList<Product> prod = new ArrayList<>();
                if (con == null || con.isClosed())
                    future.complete(prod);
                String query = "SELECT * from wishlist where id_utente=?";
                PreparedStatement stmt = con.prepareStatement(query);
                stmt.setString(1, email);
                ResultSet rs = stmt.executeQuery();

                while (rs.next()) {
                    CompletableFuture<Product> future1 = getProduct(rs.getString(2));
                    Product p = future1.get(10, TimeUnit.SECONDS);
                    prod.add(p);
                }
                future.complete(prod);
            } catch (SQLException | ExecutionException | InterruptedException | TimeoutException e) {
                SceneHandler.getInstance().showAlert("Errore thread", Message.thread_error, 0);
            }
        }));
        return future;
    }


    public void insertWishlistProductIntoDB(String email, String id_product) {
        executorService.submit(createDaemonThread(() -> {
            try {
                CompletableFuture<ArrayList<Product>> future = getWishlist(email);
                ArrayList<Product> nProd = future.get(10, TimeUnit.SECONDS);
                boolean find = false;
                for (Product id : nProd) {
                    if (id.id().equals(id_product)) {
                        find = true;
                    }
                }
                if (nProd.size() < 6 && !find) {
                    if (con == null || con.isClosed())
                        return;
                    PreparedStatement stmt = con.prepareStatement("INSERT INTO wishlist VALUES(?, ?);");
                    stmt.setString(1, email);
                    stmt.setString(2, id_product);
                    stmt.execute();
                    stmt.close();
                }

                if (nProd.size() >= 6) {
                    Platform.runLater(() -> SceneHandler.getInstance().showAlert("Attenzione", Message.add_wishlist_max_information, 1));

                }
                if (find) {
                    Platform.runLater(() -> SceneHandler.getInstance().showAlert("Attenzione", Message.add_wishlist_find_information, 1));
                }
            } catch (SQLException | ExecutionException | InterruptedException | TimeoutException e) {
                SceneHandler.getInstance().showAlert("Errore thread", Message.thread_error, 0);
            }
        }));
    }

    public void removeSelectedWishlistItem(String id, String email) {
        executorService.submit(createDaemonThread(() -> {
            try {
                if (con == null || con.isClosed())
                    return;
                String query = "DELETE FROM wishlist where id_utente=? and id_prodotto = ?;";
                PreparedStatement stmt = con.prepareStatement(query);
                stmt.setString(1, email);
                stmt.setString(2, id);
                stmt.execute();
                stmt.close();
            } catch (SQLException e) {
                SceneHandler.getInstance().showAlert("Errore thread", Message.thread_error, 0);
            }
        }));
    }

    public void updateAddress(String email, String address) {
        executorService.submit(createDaemonThread(() -> {
            try {
                if (this.con != null && !this.con.isClosed()) {
                    String query = "update utenti set indirizzo = ? where email=?;";
                    PreparedStatement stmt = this.con.prepareStatement(query);
                    stmt.setString(1, address);
                    stmt.setString(2, email);
                    stmt.execute();
                    stmt.close();
                }
            } catch (SQLException e) {
                SceneHandler.getInstance().showAlert("Errore thread", Message.thread_error, 0);
            }
        }));
    }

    public void updatePassword(String email, String password) {
        executorService.submit(createDaemonThread(() -> {
            try {
                if (this.con != null && !this.con.isClosed()) {
                    String query = "update utenti set password = ? where email=?;";
                    PreparedStatement stmt = this.con.prepareStatement(query);
                    stmt.setString(1, password);
                    stmt.setString(2, email);
                    stmt.execute();
                    stmt.close();
                }
            } catch (SQLException e) {
                SceneHandler.getInstance().showAlert("Errore thread", Message.thread_error, 0);
            }
        }));
    }

    public CompletableFuture<Boolean> checkCoupon(String code) {
        CompletableFuture<Boolean> future = new CompletableFuture<>();
        executorService.submit(createDaemonThread(() -> {
            try {
                if (con == null || con.isClosed())
                    future.complete(false);
                PreparedStatement stmt = con.prepareStatement("SELECT codice FROM coupon where codice=?");
                stmt.setString(1, code);
                ResultSet rs = stmt.executeQuery();
                boolean exist = rs.next(); // Controlla se esistono righe nel ResultSet
                rs.close();
                stmt.close();
                future.complete(exist);
            } catch (SQLException e) {
                SceneHandler.getInstance().showAlert("Errore thread", Message.thread_error, 0);
            }
        }));
        return future;
    }

    public CompletableFuture<String> getCouponValue(String code) {
        CompletableFuture<String> future = new CompletableFuture<>();
        executorService.submit(createDaemonThread(() -> {
            try {
                String value = "0";
                if (con == null || con.isClosed())
                    future.complete(value);
                PreparedStatement stmt = con.prepareStatement("SELECT valore FROM coupon where codice=?");
                stmt.setString(1, code);
                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    value = rs.getString("valore");
                }

                stmt = con.prepareStatement("DELETE from coupon where codice =? ");
                stmt.setString(1, code);
                stmt.execute();
                stmt.close();
                future.complete(value);
            } catch (SQLException e) {
                SceneHandler.getInstance().showAlert("Errore thread", Message.thread_error, 0);
            }
        }));
        return future;
    }

    public void updateBalance(String email, String balance) {
        executorService.submit(createDaemonThread(() -> {
            try {
                if (this.con != null && !this.con.isClosed()) {
                    String query = "update utenti set saldo = ? where email=?;";
                    PreparedStatement stmt = this.con.prepareStatement(query);
                    stmt.setString(1, balance);
                    stmt.setString(2, email);
                    stmt.execute();
                    stmt.close();
                }
            } catch (SQLException e) {
                SceneHandler.getInstance().showAlert("Errore thread", Message.thread_error, 0);
            }
        }));
    }

    public void checkOutOrder(String email, String totalPrice) {
        executorService.submit(createDaemonThread(() -> {
            try {
                if (this.con != null && !this.con.isClosed()) {
                    String maxQuery = "SELECT MAX(numero) AS max_value FROM carrello where id_cliente = ?;";
                    PreparedStatement maxStmt = this.con.prepareStatement(maxQuery);
                    maxStmt.setString(1, email);
                    ResultSet maxResult = maxStmt.executeQuery();

                    int maxValue = 0;
                    if (maxResult.next()) {
                        maxValue = maxResult.getInt("max_value");
                    }

                    String query = "update carrello set tipo = ?, numero =?, totale =? where id_cliente=? AND numero = ?;";
                    PreparedStatement stmt = this.con.prepareStatement(query);
                    stmt.setString(1, "ordine");
                    stmt.setInt(2, maxValue + 1);
                    stmt.setString(3, totalPrice);
                    stmt.setString(4, email);
                    stmt.setInt(5, 0);
                    stmt.execute();
                    stmt.close();

                    String numOrders = "SELECT COUNT(distinct numero) AS row_count FROM carrello where id_cliente=? and tipo=?;";
                    PreparedStatement countStmt = this.con.prepareStatement(numOrders);
                    countStmt.setString(1, email);
                    countStmt.setString(2, "ordine");
                    ResultSet countResult = countStmt.executeQuery();
                    int rowCount = 0;
                    if (countResult.next()) {
                        rowCount = countResult.getInt("row_count");
                    }
                    countStmt.close();

                    if (rowCount == 7) {

                        String minQuery = "SELECT MIN(numero) AS min_value FROM carrello where tipo=? and id_cliente=?;";
                        PreparedStatement minStmt = this.con.prepareStatement(minQuery);
                        minStmt.setString(1, "ordine");
                        minStmt.setString(2, email);

                        ResultSet minResult = minStmt.executeQuery();

                        int minValue = 0;
                        if (minResult.next()) {
                            minValue = minResult.getInt("min_value");
                        }
                        minStmt.close();

                        stmt = con.prepareStatement("DELETE from carrello where id_cliente=? and tipo=? and numero=? ");
                        stmt.setString(1, email);
                        stmt.setString(2, "ordine");
                        stmt.setInt(3, minValue);
                        stmt.execute();
                        stmt.close();
                    }
                }
            } catch (SQLException e) {
                SceneHandler.getInstance().showAlert("Errore thread", Message.thread_error, 0);
            }
        }));
    }


    public CompletableFuture<ArrayList<ArrayList<Product>>> getOrder(String email) {
        CompletableFuture<ArrayList<ArrayList<Product>>> future = new CompletableFuture<>();
        executorService.submit(createDaemonThread(() -> {
            try {
                ArrayList<ArrayList<Product>> order = new ArrayList<>();
                if (con == null || con.isClosed())
                    future.complete(order);
                String query = "SELECT distinct numero AS num_order FROM carrello where id_cliente=? and tipo=?;";
                PreparedStatement stmt = con.prepareStatement(query);
                stmt.setString(1, email);
                stmt.setString(2, "ordine");
                ResultSet rs = stmt.executeQuery();

                ArrayList<Integer> order_number = new ArrayList<>();
                int num_order = 0;
                while (rs.next()) {
                    num_order = rs.getInt("num_order");
                    order_number.add(num_order);
                }

                for (Integer i : order_number) {
                    ArrayList<String> id_prods = new ArrayList<>();
                    query = "SELECT * from carrello where id_cliente=? and tipo =? and numero=?";
                    stmt = con.prepareStatement(query);
                    stmt.setString(1, email);
                    stmt.setString(2, "ordine");
                    stmt.setInt(3, i);

                    rs = stmt.executeQuery();

                    while (rs.next()) {
                        id_prods.add(rs.getString("id_prodotto"));
                    }
                    ArrayList<Product> products = getCartProductInfo(id_prods);
                    order.add(products);

                }
                future.complete(order);
            } catch (SQLException | ExecutionException | InterruptedException | TimeoutException e) {
                SceneHandler.getInstance().showAlert("Errore thread", Message.thread_error, 0);
            }
        }));
        return future;
    }

    public CompletableFuture<ArrayList<String>> getOrderPrice(String email) {
        CompletableFuture<ArrayList<String>> future = new CompletableFuture<>();
        executorService.submit(createDaemonThread(() -> {
            try {
                ArrayList<String> total_price = new ArrayList<>();
                if (con == null || con.isClosed())
                    future.complete(total_price);
                PreparedStatement stmt = con.prepareStatement("SELECT totale FROM carrello where id_cliente =? and tipo =? Group by numero");
                stmt.setString(1, email);
                stmt.setString(2, "ordine");

                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    total_price.add(rs.getString("totale"));
                }
                future.complete(total_price);
            } catch (SQLException e) {
                SceneHandler.getInstance().showAlert("Errore thread", Message.thread_error, 0);
            }
        }));
        return future;
    }

    public CompletableFuture<ArrayList<String>> getOrderNumber(String email) {
        CompletableFuture<ArrayList<String>> future = new CompletableFuture<>();
        executorService.submit(createDaemonThread(() -> {
            try {
                ArrayList<String> orders_number = new ArrayList<>();
                if (con == null || con.isClosed())
                    future.complete(orders_number);
                PreparedStatement stmt = con.prepareStatement("SELECT distinct numero FROM carrello where id_cliente =?");
                stmt.setString(1, email);

                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    orders_number.add(rs.getString("numero"));
                }
                future.complete(orders_number);
            } catch (SQLException e) {
                SceneHandler.getInstance().showAlert("Errore thread", Message.thread_error, 0);
            }
        }));
        return future;
    }

    public CompletableFuture<Integer> checkOrderProductQuantity(String email, String id_prod, String orderNumber) {
        CompletableFuture<Integer> future = new CompletableFuture<>();
        executorService.submit(createDaemonThread(() -> {
            try {
                int q = 0;
                if (con == null || con.isClosed())
                    future.complete(q);
                PreparedStatement stmt = con.prepareStatement("SELECT quantita FROM carrello where id_cliente =? and id_prodotto =? and tipo=? and numero =?");
                stmt.setString(1, email);
                stmt.setString(2, id_prod);
                stmt.setString(3, "ordine");
                stmt.setString(4, orderNumber);
                ResultSet rs = stmt.executeQuery();

                if (rs.next()) {
                    q = (rs.getInt("quantita"));
                }
                future.complete(q);
            } catch (SQLException e) {
                SceneHandler.getInstance().showAlert("Errore thread", Message.thread_error, 0);
            }
        }));
        return future;
    }

    private Thread createDaemonThread(Runnable runnable) {
        Thread t = new Thread(runnable);
        t.setDaemon(true);
        return t;
    }
}