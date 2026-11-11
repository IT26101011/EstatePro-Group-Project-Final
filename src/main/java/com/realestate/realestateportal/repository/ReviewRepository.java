package com.realestate.realestateportal.repository;

import com.realestate.realestateportal.model.Review;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

public class ReviewRepository {

    // Encapsulation:

    private static final String FILE_PATH = "data/reviews.txt";
    private static final String SEPARATOR = "|";

    // Constructor (Initializes the repository by creating)
    // the data directory and reviews file if they do not exist.
    public ReviewRepository() {
        try {
            Path directory = Paths.get("data");
            Files.createDirectories(directory);

            Path file = Paths.get(FILE_PATH);

            if (Files.notExists(file)) {
                Files.createFile(file);
            }

        } catch (IOException e) {
            throw new RuntimeException(
                    "Unable to initialize reviews file.", e);
        }
    }

    // Create - Save Review
    public boolean saveReview(Review review) {

        if (review == null) {
            throw new IllegalArgumentException(
                    "Review cannot be null.");
        }

        if (findById(review.getReviewId()) != null) {
            return false;
        }

        try (BufferedWriter writer = Files.newBufferedWriter(
                Paths.get(FILE_PATH),
                StandardCharsets.UTF_8,
                StandardOpenOption.CREATE,
                StandardOpenOption.APPEND)) {

            // Obj interaction:Uses the Review object
            // and getter methods to retrieve review data.
            writer.write(convertToLine(review));
            writer.newLine();

            return true;

        } catch (IOException e) {
            throw new RuntimeException(
                    "Unable to save review.", e);
        }
    }

    // Read - Get All Reviews
    public List<Review> getAllReviews() {

        // Obj creation (Creates an ArrayList object
        // to store the retrieved Review objects.)
        List<Review> reviews = new ArrayList<>();

        try (BufferedReader reader = Files.newBufferedReader(
                Paths.get(FILE_PATH),
                StandardCharsets.UTF_8)) {

            String line;

            while ((line = reader.readLine()) != null) {

                if (line.trim().isEmpty()) {
                    continue;
                }

                //obj cre: Converts text to review obj
                Review review = convertToReview(line);

                if (review != null) {
                    reviews.add(review);
                }
            }

        } catch (IOException e) {
            throw new RuntimeException(
                    "Unable to read reviews.", e);
        }

        return reviews;
    }

    // Find Review By ID
    public Review findById(String reviewId) {

        if (reviewId == null) {
            return null;
        }

        // OBJ interaction Accesses Review objects by getter methods.
        for (Review review : getAllReviews()) {

            if (review.getReviewId().equals(reviewId)) {
                return review;
            }
        }

        return null;
    }

    // Find Reviews By Agent Email
    public List<Review> findByAgentEmail(String email) {

        // Obj creation ; Creates a list to store
        // Review objects belonging to a particular agent.
        List<Review> result = new ArrayList<>();

        if (email == null) {
            return result;
        }

        for (Review review : getAllReviews()) {

            if (review.getAgentEmail().equalsIgnoreCase(email)) {
                result.add(review);
            }
        }

        return result;
    }

    // Find Reviews By Client Email
    public List<Review> findByClientEmail(String email) {

        List<Review> result = new ArrayList<>();

        if (email == null) {
            return result;
        }

        for (Review review : getAllReviews()) {

            if (review.getClientEmail().equalsIgnoreCase(email)) {
                result.add(review);
            }
        }

        return result;
    }

    // Update Review
    public boolean updateReview(Review updatedReview) {

        if (updatedReview == null) {
            throw new IllegalArgumentException(
                    "Updated review cannot be null.");
        }

        List<Review> reviews = getAllReviews();

        boolean found = false;

        for (int i = 0; i < reviews.size(); i++) {

            if (reviews.get(i).getReviewId()
                    .equals(updatedReview.getReviewId())) {

                reviews.set(i, updatedReview);
                found = true;
                break;
            }
        }

        if (found) {
            rewriteFile(reviews);
        }

        return found;
    }

    // Delete Review
    public boolean deleteReview(String reviewId) {

        if (reviewId == null) {
            return false;
        }

        List<Review> reviews = getAllReviews();

        boolean removed = reviews.removeIf(
                review -> review.getReviewId().equals(reviewId)
        );

        if (removed) {
            rewriteFile(reviews);
        }

        return removed;
    }

    // Rewrite File
    // ABs (Hides the internal file-writing process)
    // inside a private helper method.
    private void rewriteFile(List<Review> reviews) {

        try (BufferedWriter writer = Files.newBufferedWriter(
                Paths.get(FILE_PATH),
                StandardCharsets.UTF_8,
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING)) {

            for (Review review : reviews) {
                writer.write(convertToLine(review));
                writer.newLine();
            }

        } catch (IOException e) {
            throw new RuntimeException(
                    "Unable to update reviews file.", e);
        }
    }

    // Convert Review Object To Text Line
    // Abs: Hides the conversion process
    // from the other methods in this class.
    private String convertToLine(Review review) {

        String[] values = {
                review.getReviewId(),
                review.getAgentEmail(),
                review.getClientEmail(),
                String.valueOf(review.getRating()),
                review.getComment(),
                review.getDate(),
                review.getStatus()
        };

        for (String value : values) {

            if (value == null ||
                    value.contains(SEPARATOR) ||
                    value.contains("\n") ||
                    value.contains("\r")) {

                throw new IllegalArgumentException(
                        "Review data contains invalid characters.");
            }
        }

        return String.join(SEPARATOR, values);
    }

    // Convert Text Line To Review Object
    // Obj creation; Creates a Review object using Parameterize construcror

    private Review convertToReview(String line) {

        String[] data = line.split("\\|", -1);

        if (data.length != 7) {
            return null;
        }

        try {
            return new Review(
                    data[0],
                    data[1],
                    data[2],
                    Integer.parseInt(data[3]),
                    data[4],
                    data[5],
                    data[6]
            );

        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}