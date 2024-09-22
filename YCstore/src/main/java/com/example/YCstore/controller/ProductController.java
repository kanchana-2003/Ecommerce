package com.example.YCstore.controller;


import com.example.YCstore.model.Cart;
import com.example.YCstore.model.Product;
import com.example.YCstore.service.ProductService;
import com.fasterxml.jackson.databind.ser.Serializers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/admin")
public class ProductController {

    @Autowired
    private ProductService productService;

    @GetMapping("/manageProducts")
    public String manageProducts(Model model) {
        List<Product> products = productService.getAllProducts();
        for (Product product : products) {
            String base64Image = Base64.getEncoder().encodeToString(product.getImage());
            product.setImageBase64(base64Image);
        }
        model.addAttribute("products", products);
        return "admin/manageProducts";
    }

    @GetMapping("/addProduct")
    public String showAddProductForm() {
        return "admin/addProduct";
    }

    @PostMapping("/addProduct")
    public String addProduct(@RequestParam String name,
                             @RequestParam Double price,
                             @RequestParam String description,
                             @RequestParam String category,
                             @RequestParam("image")MultipartFile imageFile,
                             RedirectAttributes redirectAttributes) {
        try {
            Product product = new Product();
            product.setName(name);
            product.setPrice(price);
            product.setDescription(description);
            product.setCategory(category);
            product.setImage(imageFile.getBytes());

            productService.saveProduct(product);

            redirectAttributes.addAttribute("message", "Product added successfully!");
        } catch (IOException e) {
            redirectAttributes.addAttribute("error", "Product added successfully!");
        }

        return "redirect:/adminHome";
    }

    @GetMapping("/editProduct/{id}")
    public String showEditProductForm(@PathVariable Long id, Model model) {
        Optional<Product> products = productService.getProductById(id);
        if(products.isPresent()) {
            Product product = products.get();
            model.addAttribute("product", product);
            return "admin/editProduct";
        } else {
            return "admin/manageProducts";
        }
    }

    @PostMapping("/editProduct/{id}")
    public String editProduct(@PathVariable Long id,
                              @RequestParam String name,
                              @RequestParam Double price,
                              @RequestParam String description,
                              @RequestParam String category,
                              @RequestParam(value = "image", required = false) MultipartFile imageFile,
                              RedirectAttributes redirectAttributes) {
        try {
            Optional<Product> products = productService.getProductById(id);
            if (products.isPresent()) {
                Product product = products.get();
                product.setName(name);
                product.setPrice(price);
                product.setDescription(description);
                product.setCategory(category);
                if (!imageFile.isEmpty()) {
                    product.setImage(imageFile.getBytes());
                }

                productService.saveProduct(product);
                redirectAttributes.addAttribute("message", "Product updated successfully!");

            }
        } catch (IOException e) {
            redirectAttributes.addFlashAttribute("error", "Failed to update product.");
        }

        return "redirect:/admin/manageProducts";
    }

    @GetMapping("/deleteProduct/{id}")
    public String deleteProduct(@PathVariable("id") Long id) {
        productService.deleteProductById(id);
        return "redirect:/admin/manageProducts";
    }





}
