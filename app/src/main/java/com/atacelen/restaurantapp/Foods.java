package com.atacelen.restaurantapp;

public class Foods {
    private String foodName;
    private String foodPrice;
    private String foodCookingTime;
    private String foodCategory;
    private String foodDiscount;
    private String foodPhotos;

    public Foods(String foodName, String foodPrice, String foodCookingTime, String foodCategory, String foodDiscount, String foodPhotos) {
        this.foodName = foodName;
        this.foodPrice = foodPrice;
        this.foodCookingTime = foodCookingTime;
        this.foodCategory = foodCategory;
        this.foodDiscount = foodDiscount;
        this.foodPhotos = foodPhotos;
    }

    public String getFoodName() {
        return foodName;
    }

    public void setFoodName(String foodName) {
        this.foodName = foodName;
    }

    public String getFoodPrice() {
        return foodPrice;
    }

    public void setFoodPrice(String foodPrice) {
        this.foodPrice = foodPrice;
    }

    public String getFoodCookingTime() {
        return foodCookingTime;
    }

    public void setFoodCookingTime(String foodCookingTime) {
        this.foodCookingTime = foodCookingTime;
    }

    public String getFoodCategory() {
        return foodCategory;
    }

    public void setFoodCategory(String foodCategory) {
        this.foodCategory = foodCategory;
    }

    public String getFoodDiscount() {
        return foodDiscount;
    }

    public void setFoodDiscount(String foodDiscount) {
        this.foodDiscount = foodDiscount;
    }

    public String getFoodPhotos() {
        return foodPhotos;
    }

    public void setFoodPhotos(String foodPhotos) {
        this.foodPhotos = foodPhotos;
    }
}
