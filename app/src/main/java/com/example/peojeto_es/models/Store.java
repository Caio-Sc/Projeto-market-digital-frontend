package com.example.peojeto_es.models;

public class Store {
    private int id;
    private String name;
    private String address;
    private String info;

    public Store(int id, String name, String address, String info) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.info = info;
    }

    // Getters
    public int getId() {return id;}
    public String getName() { return name; }
    public String getAddress() { return address; }
    public String getInfo() { return info; }
}