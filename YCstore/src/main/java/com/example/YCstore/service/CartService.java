package com.example.YCstore.service;


import com.example.YCstore.model.Cart;
import com.example.YCstore.model.Product;
import com.example.YCstore.model.User;
import com.example.YCstore.repo.CartRepository;
import com.example.YCstore.repo.ProductRepository;
import com.example.YCstore.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

@Service
public class CartService {


    @Autowired
    private CartRepository cartRepository;
    @Autowired
    private ProductService productService;
    @Autowired
    private UserService userService;


    public void addToCart(Long productId, String email) {
        User user = userService.getUserByEmail(email).get();
        Product product = productService.getProductById(productId).get();

        Optional<Cart> existingCartItem = cartRepository.findByUserAndProduct(user, product);
        if (existingCartItem.isEmpty()) {
            Cart cartItem = new Cart();
            cartItem.setUser(user);
            cartItem.setProduct(product);
            cartItem.setQuantity(1);
            cartRepository.save(cartItem);
        }
    }

    public List<Cart> getUserCart(String email) {
        User user = userService.getUserByEmail(email).get();
        return cartRepository.findByUser(user);
    }

    public void removeCartItem(Long itemId, String email) {
        Cart cartItem = cartRepository.findById(itemId).get();
        if (cartItem.getUser().getEmail().equals(email)) {
            cartRepository.delete(cartItem);
        }
    }

    public void updateCartItemQuantity(Long itemId, int quantity) {
        Cart cartItem = cartRepository.findById(itemId).get();
        cartItem.setQuantity(quantity);
        cartRepository.save(cartItem);
    }

    public void clearCart(User user) {
        List<Cart> cartItems = cartRepository.findByUser(user);
        cartRepository.deleteAll(cartItems);
    }

}
