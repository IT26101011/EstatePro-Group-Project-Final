package com.realestate.realestateportal.service;

import com.realestate.realestateportal.model.User;
import com.realestate.realestateportal.repository.UserRepository;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.List;

public class UserService {

    private final UserRepository userRepository = new UserRepository();

    // Register a new user
    public String registerUser(User user) {

        if (user.getFullName() == null || user.getFullName().isBlank() ||
                user.getEmail() == null || user.getEmail().isBlank() ||
                user.getPhone() == null || user.getPhone().isBlank() ||
                user.getPassword() == null || user.getPassword().isBlank() ||
                user.getRole() == null || user.getRole().isBlank()) {

            return "Please fill all fields.";
        }

        if (!user.getEmail().matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
            return "Invalid email address.";
        }

        if (user.getPassword().length() < 8) {
            return "Password must contain at least 8 characters.";
        }

        if (!user.getRole().equals("CLIENT") &&
                !user.getRole().equals("AGENT")) {

            return "Invalid account type.";
        }

        if (userRepository.emailExists(user.getEmail())) {
            return "Email already registered.";
        }

        try {

            String hashedPassword = hashPassword(user.getPassword());

            user.setPassword(hashedPassword);

            boolean saved = userRepository.saveUser(user);

            if (saved) {
                return "Registration successful!";
            }

            return "Registration failed. Please try again.";

        } catch (Exception e) {

            return "An error occurred during registration.";
        }
    }

    // Hash password
    private String hashPassword(String password) throws Exception {

        byte[] salt = new byte[16];

        SecureRandom random = new SecureRandom();
        random.nextBytes(salt);

        int iterations = 210000;

        PBEKeySpec spec = new PBEKeySpec(
                password.toCharArray(),
                salt,
                iterations,
                256
        );

        SecretKeyFactory factory =
                SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");

        byte[] hash = factory.generateSecret(spec).getEncoded();

        spec.clearPassword();

        return "PBKDF2$" + iterations + "$" +
                Base64.getEncoder().encodeToString(salt) + "$" +
                Base64.getEncoder().encodeToString(hash);
    }

    // Verify password
    private boolean verifyPassword(String password, String storedHash) {

        try {

            String[] parts = storedHash.split("\\$");

            if (parts.length != 4 || !parts[0].equals("PBKDF2")) {
                return false;
            }

            int iterations = Integer.parseInt(parts[1]);

            byte[] salt = Base64.getDecoder().decode(parts[2]);
            byte[] originalHash = Base64.getDecoder().decode(parts[3]);

            PBEKeySpec spec = new PBEKeySpec(
                    password.toCharArray(),
                    salt,
                    iterations,
                    originalHash.length * 8
            );

            SecretKeyFactory factory =
                    SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");

            byte[] newHash = factory.generateSecret(spec).getEncoded();

            spec.clearPassword();

            return MessageDigest.isEqual(originalHash, newHash);

        } catch (Exception e) {

            return false;
        }
    }

    // Login user
    public User loginUser(String email, String password) {

        if (email == null || email.isBlank() ||
                password == null || password.isBlank()) {

            return null;
        }

        User user = userRepository.findByEmail(email);

        if (user == null) {
            return null;
        }

        if (verifyPassword(password, user.getPassword())) {
            return user;
        }

        return null;
    }

    // Update Own Profile
    public String updateOwnProfile(
            String email,
            String fullName,
            String phone,
            String currentPassword,
            String newPassword) {

        // Validate required fields
        if (fullName == null || fullName.isBlank()) {
            return "Full name is required.";
        }

        if (phone == null || phone.isBlank()) {
            return "Phone number is required.";
        }

        // Get the existing user
        User user = userRepository.findByEmail(email);

        if (user == null) {
            return "User not found.";
        }

        // Password update is optional
        String newPasswordHash = null;

        if (newPassword != null && !newPassword.isBlank()) {

            if (currentPassword == null || currentPassword.isBlank()) {
                return "Current password is required.";
            }

            if (!verifyPassword(currentPassword, user.getPassword())) {
                return "Current password is incorrect.";
            }

            if (newPassword.length() < 8) {
                return "New password must be at least 8 characters.";
            }

            try {
                newPasswordHash = hashPassword(newPassword);
            } catch (Exception e) {
                return "Password update failed due to a system error.";
            }
        }

        boolean updated = userRepository.updateUserProfile(
                email,
                fullName.trim(),
                phone.trim(),
                newPasswordHash
        );

        if (!updated) {
            return "Profile update failed.";
        }

        return "Profile updated successfully!";
    }

    // =====================================
    // ADMIN MANAGEMENT
    // =====================================

    // Get all users
    public List<User> getAllUsers() {

        return userRepository.getAllUsers();
    }

    // Delete user by Admin
    public String deleteUserByAdmin(String adminEmail, String targetEmail) {

        User admin = userRepository.findByEmail(adminEmail);

        if (admin == null || !admin.getRole().equals("ADMIN")) {
            return "Access denied. Admin only.";
        }

        if (adminEmail.equalsIgnoreCase(targetEmail)) {
            return "Admin cannot delete their own account.";
        }

        User targetUser = userRepository.findByEmail(targetEmail);

        if (targetUser == null) {
            return "User not found.";
        }

        if (targetUser.getRole().equals("ADMIN")) {

            long adminCount = userRepository.getAllUsers()
                    .stream()
                    .filter(user -> user.getRole().equals("ADMIN"))
                    .count();

            if (adminCount <= 1) {
                return "Cannot delete the last Admin account.";
            }
        }

        boolean deleted = userRepository.deleteUser(targetEmail);

        if (deleted) {
            return "User deleted successfully!";
        }

        return "User deletion failed.";
    }

    // Update user role by Admin
    public String updateUserRoleByAdmin(
            String adminEmail,
            String targetEmail,
            String newRole) {

        User admin = userRepository.findByEmail(adminEmail);

        if (admin == null || !admin.getRole().equals("ADMIN")) {
            return "Access denied. Admin only.";
        }

        if (newRole == null ||
                (!newRole.equals("CLIENT") &&
                        !newRole.equals("AGENT") &&
                        !newRole.equals("ADMIN"))) {

            return "Invalid user role.";
        }

        User targetUser = userRepository.findByEmail(targetEmail);

        if (targetUser == null) {
            return "User not found.";
        }

        if (adminEmail.equalsIgnoreCase(targetEmail)) {
            return "Admin cannot change their own role.";
        }

        if (targetUser.getRole().equals("ADMIN") &&
                !newRole.equals("ADMIN")) {

            long adminCount = userRepository.getAllUsers()
                    .stream()
                    .filter(user -> user.getRole().equals("ADMIN"))
                    .count();

            if (adminCount <= 1) {
                return "Cannot change the role of the last Admin.";
            }
        }

        boolean updated = userRepository.updateUserRole(
                targetEmail,
                newRole
        );

        if (updated) {
            return "User role updated successfully!";
        }

        return "User role update failed.";
    }
}