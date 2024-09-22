package com.example.YCstore.controller;

import com.example.YCstore.model.Product;
import com.example.YCstore.model.Role;
import com.example.YCstore.model.User;
import com.example.YCstore.service.ProductService;
import com.example.YCstore.service.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Base64;
import java.util.List;
import java.util.Optional;

@Controller
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private ProductService productService;

    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@ModelAttribute("user") User user, Model model) {
        if (userService.isEmailExists(user.getEmail())) {
            model.addAttribute("error", "An account with this email already exists.");
            return "register";
        }

        userService.saveUser(user);
        return "redirect:/login";
    }


    @GetMapping("/login")
    public String showLoginForm() {
        return "login";
    }

    @GetMapping("/adminHome")
    public String adminHome() {
        return "admin/adminHome";
    }

    @GetMapping("/userHome")
    public String userHome(Model model) {
        List<Product> products = productService.getAllProducts();
        model.addAttribute("products", products);
        for (Product product : products) {
            String base64Image = Base64.getEncoder().encodeToString(product.getImage());
            product.setImageBase64(base64Image);
        }
        return "user/userHome";
    }

    @GetMapping("/admin/manageUsers")
    public String manageUsers(Model model) {
        List<User> users = userService.getAllUsers();
        model.addAttribute("users", users);
        return "admin/manageUsers";
    }

    @GetMapping("/admin/addUser")
    public String showAddUserForm(Model model) {
        User user = new User();
        model.addAttribute("user", user);
        return "admin/addUser";
    }

    @PostMapping("/admin/addUser")
    public String addUser(@ModelAttribute("user") User user, Model model) {
        if (userService.isEmailExists(user.getEmail())) {
            model.addAttribute("error", "An account with this email already exists.");
            return "admin/addUser";
        }

        userService.saveUser(user);
        return "redirect:/admin/manageUsers";
    }

    @GetMapping("/admin/editUser/{id}")
    public String editUser(@PathVariable Long id, Model model) {
        User user = userService.getUserById(id).get();
        model.addAttribute("user", user);
        model.addAttribute("roles", Role.values()); // To get the roles for dropdown
        return "admin/editUser";
    }

    @PostMapping("/admin/updateUser/{id}")
    public String updateUser(@PathVariable Long id,
                             @RequestParam String username,
                             @RequestParam String email,
                             @RequestParam String role) {
        User user = userService.getUserById(id).get();
        user.setUsername(username);
        user.setEmail(email);
        user.setRole(Role.valueOf(role));

        userService.saveUser(user);
        return "redirect:/admin/manageUsers";
    }

    @PostMapping("/admin/deleteUser/{id}")
    public String deleteUser(@PathVariable Long id) {
        userService.deleteUserById(id);
        return "redirect:/admin/manageUsers";
    }



    @RequestMapping(value = "/logout", method = RequestMethod.GET)
    public String logout(HttpServletRequest request) throws ServletException {
        request.logout();
        return "redirect:/login";
    }


}
