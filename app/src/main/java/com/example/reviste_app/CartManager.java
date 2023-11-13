package com.example.reviste_app;

import java.util.ArrayList;
import java.util.List;

public class CartManager {
    private static List<CartItem> cartItems = new ArrayList<>();
    private static double cartTotal = 0.0;
    private static CartItem lastRemovedItem;

    public static List<CartItem> getCartItems() {
        return cartItems;
    }

    public static double getCartTotal() {
        return cartTotal;
    }

    public static void addToCart(CartItem cartItem) {
        for (CartItem item : cartItems) {
            if (item.getProductId().equals(cartItem.getProductId())) {
                item.setQuantity(item.getQuantity() + 1);
                cartTotal += cartItem.getPrice();
                return;
            }
        }

        cartItems.add(cartItem);
        cartTotal += cartItem.getPrice();
    }

    public static CartItem getLastRemovedItem() {
        return lastRemovedItem;
    }

    public static void setLastRemovedItem(CartItem item) {
        lastRemovedItem = item;
    }

    public static void removeFromCart(CartItem cartItem) {
        cartTotal -= (cartItem.getPrice() * cartItem.getQuantity());
        cartItems.remove(cartItem);

        int updatedQuantity = cartItem.getQuantity();
        if (updatedQuantity > 1) {
            cartTotal += (cartItem.getPrice() * (updatedQuantity - 1));
        }
    }

    public static void updateCartTotal(double newTotal) {
        cartTotal = newTotal;
    }
}
