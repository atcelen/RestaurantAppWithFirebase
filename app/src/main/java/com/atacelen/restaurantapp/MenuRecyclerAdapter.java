package com.atacelen.restaurantapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class MenuRecyclerAdapter extends RecyclerView.Adapter<MenuRecyclerAdapter.MenuItemHolder> {
    ArrayList<String> foodNames;
    ArrayList<String> foodPrices;
    ArrayList<String> foodCookingTime;
    ArrayList<String> foodCategory;
    ArrayList<String> foodDiscount;
    final ArrayList<Integer> foodImageResourceId = new ArrayList<>();
    ArrayList<Bitmap> foodImages;

    public MenuRecyclerAdapter(ArrayList<String> foodNames, ArrayList<String> foodPrices, ArrayList<String> foodCookingTime, ArrayList<String> foodCategory, ArrayList<String> foodDiscount, ArrayList<Bitmap> foodImages) {
        this.foodNames = foodNames;
        this.foodPrices = foodPrices;
        this.foodCookingTime = foodCookingTime;
        this.foodCategory = foodCategory;
        this.foodDiscount = foodDiscount;
        this.foodImages = foodImages;
    }
    @NonNull

    @Override
    public MenuItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate( R.layout.recycler_row, parent, false);
        return new MenuItemHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MenuItemHolder holder, final int position) {

        // We set the texts and the image of our MenuItemHolder object
        holder.foodNameText.setText(foodNames.get(position));
        holder.priceText.setText("Price: "+foodPrices.get(position).toString() + " CHF");
        holder.cookingTimeText.setText("Cooking Time: " + foodCookingTime.get(position) + " min");
        holder.categoryText.setText("Category: " + foodCategory.get(position).toString());
        holder.discountText.setText("Discount: " + foodDiscount.get(position).toString()+ " %");
        holder.imageView.setImageBitmap(foodImages.get(position));
        holder.linearLayout.setBackgroundResource(R.drawable.box_ui);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), DetailedItemActivity.class );
                intent.putExtra("detailedFoodName", foodNames.get(position));
                intent.putExtra("detailedFoodPrice", foodPrices.get(position));
                intent.putExtra("detailedFoodCookingTime", foodCookingTime.get(position));
                intent.putExtra("detailedFoodCategory", foodCategory.get(position));
                intent.putExtra("detailedFoodDiscount", foodDiscount.get(position));
                Singleton singleton = Singleton.getInstance();
                singleton.setChosenFoodImage(foodImages.get(position));
                view.getContext().startActivity(intent);
            }
        });


    }

    @Override
    public int getItemCount() {
        return foodNames.size();
    }

    /*
        "A ViewHolder describes an item view and metadata about its place within the RecyclerView."

        https://developer.android.com/reference/androidx/recyclerview/widget/RecyclerView.ViewHolder
     */
    class MenuItemHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView foodNameText, priceText, cookingTimeText, categoryText, discountText;
        LinearLayout linearLayout;

        public MenuItemHolder(@NonNull View itemView) {
            super(itemView);

            // links the attributes with the recycler_row items
            imageView = itemView.findViewById(R.id.recycler_row_imageView);
            foodNameText = itemView.findViewById(R.id.recycler_row_foodNameText);
            priceText = itemView.findViewById(R.id.recycler_row_priceText);
            cookingTimeText = itemView.findViewById(R.id.recycler_row_cookingTimeText);
            categoryText = itemView.findViewById(R.id.recycler_row_categoryText);
            discountText = itemView.findViewById(R.id.recycler_row_discountText);

            linearLayout = itemView.findViewById(R.id.linearLayout);
        }
    }
}
