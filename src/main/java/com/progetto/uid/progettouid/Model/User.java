package com.progetto.uid.progettouid.Model;

public record User(String email, String name, String surname, String address, String balance) {
    @Override
    public String toString() {
        return email + ";" + name + ";" + surname + ";" + address + ";" + balance;
    }
}

