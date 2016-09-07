package com.ccec.dexterapp;

/**
 * Created by aanchalharit on 30/08/16.
 */
public class person
{
    private String name;
    private String address;

    public person() {
      /*Blank default constructor essential for Firebase*/
    }
    //Getters and setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
