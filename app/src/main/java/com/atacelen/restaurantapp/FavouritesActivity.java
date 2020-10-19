package com.atacelen.restaurantapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Map;

public class FavouritesActivity extends AppCompatActivity {
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;

    ArrayList<String> foodNameFromFB, foodPriceFromFB, foodCookingTimeFromFB, foodCategoryFromFB, foodDiscountFromFB;
    ArrayList<Bitmap> foodImagesFromFB;
    MenuRecyclerAdapter menuRecyclerAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourites);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        RecyclerView recyclerView = findViewById(R.id.recyclerViewFav);

        foodNameFromFB = new ArrayList<>();
        foodPriceFromFB = new ArrayList<>();
        foodCookingTimeFromFB = new ArrayList<>();
        foodCategoryFromFB = new ArrayList<>();
        foodDiscountFromFB = new ArrayList<>();
        foodImagesFromFB = new ArrayList<>();

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        menuRecyclerAdapter = new MenuRecyclerAdapter(foodNameFromFB, foodPriceFromFB, foodCookingTimeFromFB, foodCategoryFromFB, foodDiscountFromFB, foodImagesFromFB);
        recyclerView.setAdapter(menuRecyclerAdapter);

        getDataFromFirestore();

    }

    public void getDataFromFirestore(){
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        CollectionReference collectionReference = firebaseFirestore.collection("Users").document(firebaseUser.getEmail()).collection("Favourites");
        collectionReference.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if(error != null){
                    Toast.makeText(FavouritesActivity.this, error.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                }

                if(value != null){
                    for(DocumentSnapshot documentSnapshot : value.getDocuments()) {
                        Map<String,Object> data = documentSnapshot.getData();

                        String foodName = (String) data.get("foodName");
                        String foodPrice = (String) data.get("foodPrice");
                        String foodCookingTime = (String) data.get("foodCookingTime");
                        String foodCategory = (String) data.get("foodCategory");
                        String foodDiscount = (String) data.get("foodDiscount");
                        String url = (String) data.get("downloadUrl");

                        foodNameFromFB.add(foodName);
                        foodPriceFromFB.add(foodPrice);
                        foodCookingTimeFromFB.add(foodCookingTime);
                        foodCategoryFromFB.add(foodCategory);
                        foodDiscountFromFB.add(foodDiscount);

                        Target target = new Target() {
                            @Override
                            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                                foodImagesFromFB.add(bitmap);
                                menuRecyclerAdapter.notifyDataSetChanged();
                            }

                            @Override
                            public void onBitmapFailed(Exception e, Drawable errorDrawable) {
                                e.printStackTrace();
                            }

                            @Override
                            public void onPrepareLoad(Drawable placeHolderDrawable) {

                            }
                        };
                        Picasso.get().load(url).into(target);


                    }
                }
            }
        });
    }
}