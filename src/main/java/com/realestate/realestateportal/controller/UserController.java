package com.realestate.realestateportal.controller;

import com.realestate.realestateportal.model.User;
import com.realestate.realestateportal.service.UserService;

import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;
import com.realestate.realestateportal.model.Client;

@RestController
public class UserController {

    private final UserService userService = new UserService();

    // User Registration
    @PostMapping("/register") // "C" of CRUD
    public String registerUser(
            @ModelAttribute Client client,
            @RequestParam("confirmPassword") String confirmPassword) {

        if (client.getPassword() == null ||
                !client.getPassword().equals(confirmPassword)) {
            return "Passwords do not match!";
        }

        return userService.registerUser(client);
    }


    // User Login
    @PostMapping("/login")
    public String loginUser(
            @RequestParam("email") String email,
            @RequestParam("password") String password,
            HttpSession session) {

        User user = userService.loginUser(email, password);

        if (user == null) {
            return "Invalid email or password!";
        }

        session.setAttribute("loggedInUser", user);
        session.setAttribute("userRole", user.getRole());

        return "Login successful! Welcome, " + user.getRole() + ".";
    }

    // User Logout
    @GetMapping("/logout")
    public org.springframework.web.servlet.view.RedirectView logout(
            HttpSession session) {

        session.invalidate();

        return new org.springframework.web.servlet.view.RedirectView(
                "/login.html?logout=success"
        );
    }

// CLIENT PROFILE MANAGEMENT

    // View Own Profile
    @GetMapping("/profile")
    public Object getMyProfile(HttpSession session) {//Responsible for read an existing user."R"of CRUD

        User loggedInUser = (User) session.getAttribute("loggedInUser");

        if (loggedInUser == null) {
            Map<String, String> error = new LinkedHashMap<>();
            error.put("error", "Please login to view your profile.");
            return error;
        }

        // Return profile details without password
        Map<String, String> profile = new LinkedHashMap<>();

        profile.put("fullName", loggedInUser.getFullName());
        profile.put("email", loggedInUser.getEmail());
        profile.put("phone", loggedInUser.getPhone());
        profile.put("role", loggedInUser.getRole());

        return profile;
    }

    // Update Own Profile
    @PostMapping("/profile/update")
    public String updateMyProfile(//Responsible for updating an existing profile."U"of CRUD
            @RequestParam("fullName") String fullName,
            @RequestParam("phone") String phone,
            @RequestParam(value = "currentPassword", required = false) String currentPassword,
            @RequestParam(value = "newPassword", required = false) String newPassword,
            HttpSession session) {

        User loggedInUser = (User) session.getAttribute("loggedInUser");

        if (loggedInUser == null) {
            return "Please login to update your profile.";
        }

        String result = userService.updateOwnProfile(
                loggedInUser.getEmail(),
                fullName,
                phone,
                currentPassword,
                newPassword
        );

        if (result.equals("Profile updated successfully!")) {
            loggedInUser.setFullName(fullName.trim());
            loggedInUser.setPhone(phone.trim());
            session.setAttribute("loggedInUser", loggedInUser);
        }

        return result;
    }

    // Delete Own Account
    @PostMapping("/profile/delete")
    public String deleteOwnAccount(
            @RequestParam("currentPassword") String currentPassword,
            HttpSession session) {

        User loggedInUser =
                (User) session.getAttribute("loggedInUser");

        if (loggedInUser == null) {
            return "Please login to delete your account.";
        }

        String result = userService.deleteOwnAccount(
                loggedInUser.getEmail(),
                currentPassword
        );

        if (result.equals("Account deleted successfully!")) {
            session.invalidate();
        }

        return result;
    }

// ADMIN MANAGEMENT

    // View All Users
    @GetMapping("/admin/users")
    public Object getAllUsers(HttpSession session) {

        User admin = (User) session.getAttribute("loggedInUser");

        if (admin == null || !"ADMIN".equals(admin.getRole())) {
            return "Access denied. Admin only.";
        }

        // Return user details without password
        return userService.getAllUsers().stream().map(user -> {

            Map<String, String> data = new LinkedHashMap<>();

            data.put("fullName", user.getFullName());
            data.put("email", user.getEmail());
            data.put("phone", user.getPhone());
            data.put("role", user.getRole());

            return data;

        }).toList();
    }

    // Delete User
    @PostMapping("/admin/delete-user")
    public String deleteUser(
            @RequestParam("email") String email,
            HttpSession session) {

        User admin = (User) session.getAttribute("loggedInUser");

        if (admin == null || !"ADMIN".equals(admin.getRole())) {
            return "Access denied. Admin only.";
        }

        return userService.deleteUserByAdmin(
                admin.getEmail(),
                email
        );
    }

    // Update User Role
    @PostMapping("/admin/update-role")
    public String updateUserRole(
            @RequestParam("email") String email,
            @RequestParam("role") String role,
            HttpSession session) {

        User admin = (User) session.getAttribute("loggedInUser");

        if (admin == null || !"ADMIN".equals(admin.getRole())) {
            return "Access denied. Admin only.";
        }

        return userService.updateUserRoleByAdmin(
                admin.getEmail(),
                email,
                role
        );
    }

}
