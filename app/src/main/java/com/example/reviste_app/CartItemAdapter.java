package com.example.reviste_app;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.squareup.picasso.Picasso;
import java.util.List;

public class CartItemAdapter extends RecyclerView.Adapter<CartItemAdapter.ViewHolder> {
    private List<CartItem> cartItems;

    public CartItemAdapter(List<CartItem> cartItems) {
        this.cartItems = cartItems;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_carrito, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CartItem cartItem = cartItems.get(position);
        holder.productNameTextView.setText(cartItem.getProductName());
        holder.priceTextView.setText(String.valueOf(cartItem.getPrice()));

        // Cargar la imagen principal en la ImageView con Picasso
        Picasso.get().load(cartItem.getProductImage()).into(holder.productImageView);
    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView productImageView;
        public TextView productNameTextView;
        public TextView priceTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            productImageView = itemView.findViewById(R.id.product_image);
            productNameTextView = itemView.findViewById(R.id.product_name);
            priceTextView = itemView.findViewById(R.id.price);
        }
    }
}