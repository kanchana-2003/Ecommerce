package com.example.YCstore.repo;

import com.example.YCstore.model.Cart;
import com.example.YCstore.model.Product;
import com.example.YCstore.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Long> {

    List<Cart> findByUser(User user);
    Optional<Cart> findByUserAndProduct(User user, Product product);
}
