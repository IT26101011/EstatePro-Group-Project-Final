
package com.realestate.realestateportal.repository;

import com.realestate.realestateportal.model.User;
import com.realestate.realestateportal.model.Client;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class UserRepository {

    private static final String FILE_PATH = "data/users.txt";

    // Save a new user
    public boolean saveUser(User user) {

        try {
            File file = new File(FILE_PATH);

            File parent = file.getParentFile();

            if (parent != null && !parent.exists()) {
                parent.mkdirs();
            }

            try (BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(
                            new FileOutputStream(file, true),
                            StandardCharsets.UTF_8))) {

                writer.write(
                        user.getFullName() + "," +
                                user.getEmail() + "," +
                                user.getPhone() + "," +
                                user.getPassword() + "," +
                                user.getRole()
                );

                writer.newLine();
            }

            return true;

        } catch (IOException e) {
            System.out.println("Error saving user: " + e.getMessage());
            return false;
        }
    }

    // Check whether email already exists
    public boolean emailExists(String email) {
        return findByEmail(email) != null;
    }

    // Find user by email
    public User findByEmail(String email) {

        for (User user : getAllUsers()) {

            if (user.getEmail().equalsIgnoreCase(email)) {
                return user;
            }
        }

        return null;
    }

    // Get All Users
    public List<User> getAllUsers() {

        List<User> users = new ArrayList<>();

        File file = new File(FILE_PATH);

        if (!file.exists()) {
            return users;
        }

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(
                        new FileInputStream(file),
                        StandardCharsets.UTF_8))) {

            String line;

            while ((line = reader.readLine()) != null) {

                String[] parts = line.split(",", -1);

                if (parts.length == 5) {

                    User user;

                    // Create Client objects for CLIENT accounts
                    if ("CLIENT".equalsIgnoreCase(parts[4])) {

                        user = new Client(
                                parts[0],
                                parts[1],
                                parts[2],
                                parts[3]
                        );

                    } else {

                        user = new User(
                                parts[0],
                                parts[1],
                                parts[2],
                                parts[3],
                                parts[4]
                        );
                    }

                    users.add(user);
                }
            }

        } catch (IOException e) {
            System.out.println("Error reading users: " + e.getMessage());
        }

        return users;
    }

    // Update User Profile
    public boolean updateUserProfile(
            String email,
            String fullName,
            String phone,
            String newPasswordHash) {

        List<User> users = getAllUsers();

        boolean found = false;

        for (User user : users) {

            if (user.getEmail().equalsIgnoreCase(email)) {

                user.setFullName(fullName);
                user.setPhone(phone);

                if (newPasswordHash != null && !newPasswordHash.isBlank()) {
                    user.setPassword(newPasswordHash);
                }

                found = true;
                break;
            }
        }

        if (!found) {
            return false;
        }

        return rewriteFile(users);
    }

    // Delete User
    public boolean deleteUser(String email) {

        List<User> users = getAllUsers();

        boolean removed = users.removeIf(
                user -> user.getEmail().equalsIgnoreCase(email)
        );

        if (!removed) {
            return false;
        }

        return rewriteFile(users);
    }

    // Update User Role
    public boolean updateUserRole(String email, String newRole) {

        List<User> users = getAllUsers();

        boolean found = false;

        for (User user : users) {

            if (user.getEmail().equalsIgnoreCase(email)) {

                user.setRole(newRole);
                found = true;
                break;
            }
        }

        if (!found) {
            return false;
        }

        return rewriteFile(users);
    }

    // Rewrite Users File
    private boolean rewriteFile(List<User> users) {

        try {

            File file = new File(FILE_PATH);

            File parent = file.getParentFile();

            if (parent != null && !parent.exists()) {
                parent.mkdirs();
            }

            try (BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(
                            new FileOutputStream(file, false),
                            StandardCharsets.UTF_8))) {

                for (User user : users) {

                    writer.write(
                            user.getFullName() + "," +
                                    user.getEmail() + "," +
                                    user.getPhone() + "," +
                                    user.getPassword() + "," +
                                    user.getRole()
                    );

                    writer.newLine();
                }
            }

            return true;

        } catch (IOException e) {

            System.out.println("Error updating users: " + e.getMessage());
            return false;
        }
    }
}
