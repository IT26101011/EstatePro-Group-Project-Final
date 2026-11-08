package com.realestate.realestateportal;

import com.realestate.realestateportal.model.User;
import com.realestate.realestateportal.repository.UserRepository;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.SecureRandom;
import java.util.Base64;

@SpringBootApplication
public class RealEstateApplication {


    public static void main(String[] args) {
        SpringApplication.run(RealEstateApplication.class, args);
    }

    @Bean
    CommandLineRunner createAdmin() {

        return args -> {

            UserRepository repository = new UserRepository();

            String adminEmail = "realestateportal1613@gmail.com";

            if (repository.emailExists(adminEmail)) {
                System.out.println("Admin account already exists.");
                return;
            }

            // Enter your own password here
            String adminPassword = "Real@1234";

            User admin = new User();

            admin.setFullName("System Administrator");
            admin.setEmail(adminEmail);
            admin.setPhone("0770000000");
            admin.setPassword(hashPassword(adminPassword));
            admin.setRole("ADMIN");

            if (repository.saveUser(admin)) {
                System.out.println("Admin account created successfully!");
            } else {
                System.out.println("Admin account creation failed!");
            }
        };
    }

    private String hashPassword(String password) throws Exception {

        byte[] salt = new byte[16];

        new SecureRandom().nextBytes(salt);

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

}
