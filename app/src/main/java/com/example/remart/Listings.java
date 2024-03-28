package com.example.remart;

public class Listings {
    private String ListingTitle;
    private String Description;
    private double Price;

    public Listings(String ListingTitle, double Price, String Description) {
        this.ListingTitle = ListingTitle;
        this.Price = Price;
        this.Description = Description;
    }


    public String getListingTitle() {
        return ListingTitle;
    }

    public double getPrice() {
        return Price;
    }

    public String getDescription() { return Description; }


    public void setListingTitle(String title) { ListingTitle = title; }

    public void setPrice(double cost) { Price = cost; }

    public void setDescription(String description) {
        Description = description;
    }

}
