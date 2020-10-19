package com.atacelen.restaurantapp;

import android.graphics.Bitmap;

public class Singleton {
    private Bitmap chosenFoodImage;
    private static Singleton singleton;

    private Singleton() {

    }

    public Bitmap getChosenFoodImage() {
        return chosenFoodImage;
    }

    public void setChosenFoodImage(Bitmap chosenFoodImage) {
        this.chosenFoodImage = chosenFoodImage;
    }

    public static Singleton getInstance() {
        if(singleton == null) {
            singleton = new Singleton();
        }
        return singleton;
    }
}
