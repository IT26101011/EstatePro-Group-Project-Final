
package com.realestate.realestateportal.model;

public class Client extends User {

    public Client() {
        super();
    }

    public Client(String fullName, String email,
                  String phone, String password) {

        super(fullName, email, phone, password, "CLIENT");
    }

    @Override
    public String getAccountDescription() {
        return "Client Account";
    }
}