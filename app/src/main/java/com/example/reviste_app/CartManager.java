package com.example.reviste_app;

import java.util.ArrayList;
import java.util.List;

public class CartManager {
    private static List<CartItem> cartItems = new ArrayList<>();
    private static CartItem lastRemovedItem;

    public static List<CartItem> getCartItems() {
        return cartItems;
    }

    public static void addToCart(CartItem cartItem) {
        for (CartItem item : cartItems) {
            if (item.getProductId().equals(cartItem.getProductId())) {
                item.setQuantity(item.getQuantity() + 1);
                return;
            }
        }

        cartItems.add(cartItem);
    }

    public static CartItem getLastRemovedItem() {
        return lastRemovedItem;
    }

    public static void setLastRemovedItem(CartItem item) {
        lastRemovedItem = item;
    }

    public static void removeFromCart(CartItem cartItem) {
        cartItems.remove(cartItem);
    }
}
