package com.progetto.uid.progettouid.Model;

public record Product(String id, String name, String description, String seller, String price, String category, String nSeller) {
    @Override
    public String toString() {
        return id + ";" + name + ";" + description + ";" + seller + ";" + price  + ";" + category + ";" + nSeller;
    }
}