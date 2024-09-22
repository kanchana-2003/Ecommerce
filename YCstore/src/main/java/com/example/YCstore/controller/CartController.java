package com.example.YCstore.controller;


import com.example.YCstore.model.Cart;
import com.example.YCstore.model.Product;
import com.example.YCstore.model.User;
import com.example.YCstore.service.CartService;
import com.example.YCstore.service.ProductService;
import com.example.YCstore.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.util.Base64;
import java.util.List;

@Controller
public class CartController {

    @Autowired
    private UserService userService;

    @Autowired
    private ProductService productService;

    @Autowired
    private CartService cartService;

    @PostMapping("/addToCart")
    public String addToCart(@RequestParam("productId") Long productId, Principal principal) {
        cartService.addToCart(productId, principal.getName());
        return "redirect:/productDetail?id="+productId;
    }

    @GetMapping("/cart")
    public String showCart(Model model, Principal principal) {
        List<Cart> cartItems = cartService.getUserCart(principal.getName());
        for (Cart cartItem : cartItems) {
            String base64Image = Base64.getEncoder().encodeToString(cartItem.getProduct().getImage());
            cartItem.getProduct().setImageBase64(base64Image);
        }
        double totalPrice = cartItems.stream()
                        .mapToDouble(cartItem -> cartItem.getProduct().getPrice() * cartItem.getQuantity())
                                .sum();
        model.addAttribute("cartItems", cartItems);
        model.addAttribute("totalPrice", totalPrice);
        return "cart";
    }

    @PostMapping("/updateCartItemQuantity")
    public String updateCartItemQuantity(@RequestParam Long cartId, @RequestParam int quantity) {
        cartService.updateCartItemQuantity(cartId, quantity);
        return "redirect:/cart";
    }

    @GetMapping("/removeCartItem/{id}")
    public String removeCartItem(@PathVariable Long id, Principal principal) {
        cartService.removeCartItem(id, principal.getName());

        return "redirect:/cart";
    }
}
