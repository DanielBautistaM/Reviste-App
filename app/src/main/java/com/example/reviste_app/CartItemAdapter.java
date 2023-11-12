package com.example.reviste_app;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class CartItemAdapter extends RecyclerView.Adapter<CartItemAdapter.ViewHolder> {
    private List<CartItem> cartItems;
    private OnRemoveItemClickListener removeItemClickListener;

    public CartItemAdapter(List<CartItem> cartItems, OnRemoveItemClickListener removeItemClickListener) {
        this.cartItems = cartItems;
        this.removeItemClickListener = removeItemClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_carrito, parent, false);
        return new ViewHolder(view, removeItemClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CartItem cartItem = cartItems.get(position);
        holder.productNameTextView.setText(cartItem.getProductName());
        holder.priceTextView.setText(String.valueOf(cartItem.getPrice()));

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
        public Button removeButton;

        public ViewHolder(@NonNull View itemView, final OnRemoveItemClickListener listener) {
            super(itemView);
            productImageView = itemView.findViewById(R.id.product_image);
            productNameTextView = itemView.findViewById(R.id.product_name);
            priceTextView = itemView.findViewById(R.id.price);
            removeButton = itemView.findViewById(R.id.remove_button);

            removeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onRemoveItemClick(position);
                        }
                    }
                }
            });
        }
    }

    // Interface for the remove item click listener
    public interface OnRemoveItemClickListener {
        void onRemoveItemClick(int position);
    }
}
