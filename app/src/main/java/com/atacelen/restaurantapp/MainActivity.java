package com.atacelen.restaurantapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.opencsv.CSVReader;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    final ArrayList<String> foodNames = new ArrayList<>();
    final ArrayList<String> foodPrices = new ArrayList<>();
    final ArrayList<String> foodCookingTime = new ArrayList<>();
    final ArrayList<String> foodCategory = new ArrayList<>();
    final ArrayList<String> foodDiscount = new ArrayList<>();
    //final ArrayList<Integer> foodImageResourceId = new ArrayList<>();
    final ArrayList<Bitmap> foodImages = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecyclerView recyclerView = findViewById(R.id.recyclerView);

        try {
            readData();
        } catch (IOException e) {
            e.printStackTrace();
        }

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        MenuRecyclerAdapter menuRecyclerAdapter = new MenuRecyclerAdapter(foodNames, foodPrices, foodCookingTime, foodCategory, foodDiscount, foodImages);
        recyclerView.setAdapter(menuRecyclerAdapter);

        Intent intent = getIntent();
        if(intent != null && intent.getStringExtra("intentID") != null) {
            foodNames.add(intent.getStringExtra("addedFoodName"));
            foodPrices.add(intent.getStringExtra("addedFoodPrice"));
            foodCookingTime.add(intent.getStringExtra("addedFoodCookingTime"));
            foodCategory.add(intent.getStringExtra("addedFoodCategory"));
            foodDiscount.add(intent.getStringExtra("addedFoodDiscount"));

            Singleton singleton = Singleton.getInstance();
            foodImages.add(singleton.getChosenFoodImage());

            menuRecyclerAdapter.notifyDataSetChanged();

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.add_item, menu);
        return super.onCreateOptionsMenu(menu);
    }
    /*
        The onOptionsItemSelected() method is called when an item from the dropdown menu is selected by the user.
        In this implementation, we take the user from the Main Activity to the Add Item Activity via an intent
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.add_food_item) {
            Intent intent = new Intent(MainActivity.this, AddItemActivity.class);
            startActivity(intent);
        } else if(item.getItemId() == R.id.favourites) {
            Intent intent = new Intent(MainActivity.this, FavouritesActivity.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    private void readData() throws IOException {
        InputStreamReader inputStreamReader = new InputStreamReader(getResources().openRawResource(R.raw.foods));
        CSVReader csvReader = new CSVReader(inputStreamReader);

        String[] nextLine;

        csvReader.readNext();
        try {
            while((nextLine = csvReader.readNext()) != null) {
                Foods foods = new Foods(nextLine[0], nextLine[1], nextLine[2], nextLine[3], nextLine[4], nextLine[5]);

                foodNames.add(foods.getFoodName());
                foodPrices.add(foods.getFoodPrice());
                foodCookingTime.add(foods.getFoodCookingTime());
                foodCategory.add(foods.getFoodCategory());
                foodDiscount.add(foods.getFoodDiscount());

                /*
                Resources resources = getApplicationContext().getResources();
                int resourceId = resources.getIdentifier(foods.getFoodPhotos(), "drawable", getApplicationContext().getPackageName());
                foodImageResourceId.add(resourceId);

                 */
                Resources resources = getApplicationContext().getResources();
                foodImages.add(BitmapFactory.decodeResource(resources, resources.getIdentifier(foods.getFoodPhotos(), "drawable", getApplicationContext().getPackageName())));


            }
        } catch(IOException e){
            e.printStackTrace();
        }
    }
}