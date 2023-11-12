package com.example.reviste_app;


import java.util.ArrayList;
import java.util.List;

public class CartManager {
    private static List<CartItem> cartItems = new ArrayList<>();

    public static List<CartItem> getCartItems() {
        return cartItems;
    }

    public static void addToCart(CartItem cartItem) {
        // Verificar si el producto ya está en el carrito
        for (CartItem item : cartItems) {
            if (item.getProductId().equals(cartItem.getProductId())) {
                // El producto ya está en el carrito, actualizar la cantidad
                item.setQuantity(item.getQuantity() + 1);
                return;
            }
        }

        // Si el producto no está en el carrito, agregarlo
        cartItems.add(cartItem);
    }
}