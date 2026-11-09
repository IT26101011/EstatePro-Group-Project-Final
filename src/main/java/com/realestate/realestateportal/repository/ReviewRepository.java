package com.realestate.realestateportal.repository;

import com.realestate.realestateportal.model.Review;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

public class ReviewRepository {


    private final String FILE_PATH = "data/reviews.txt";

    public ReviewRepository() {
        try {
            Files.createDirectories(Paths.get("data"));

            File file = new File(FILE_PATH);

            if (!file.exists()) {
                file.createNewFile();
            }

        } catch (IOException e) {
            throw new RuntimeException("Unable to initialize reviews file.", e);
        }
    }

    // Save Review
    public void saveReview(Review review) {

        try (BufferedWriter writer = Files.newBufferedWriter(
                Paths.get(FILE_PATH),
                StandardCharsets.UTF_8,
                StandardOpenOption.CREATE,
                StandardOpenOption.APPEND)) {

            writer.write(convertToLine(review));
            writer.newLine();

        } catch (IOException e) {
            throw new RuntimeException("Unable to save review.", e);
        }
    }

    // Get All Reviews
    public List<Review> getAllReviews() {

        List<Review> reviews = new ArrayList<>();

        try (BufferedReader reader = Files.newBufferedReader(
                Paths.get(FILE_PATH), StandardCharsets.UTF_8)) {

            String line;

            while ((line = reader.readLine()) != null) {

                Review review = convertToReview(line);

                if (review != null) {
                    reviews.add(review);
                }
            }

        } catch (IOException e) {
            throw new RuntimeException("Unable to read reviews.", e);
        }

        return reviews;
    }

    // Find Review By ID
    public Review findById(String reviewId) {

        for (Review review : getAllReviews()) {

            if (review.getReviewId().equals(reviewId)) {
                return review;
            }
        }

        return null;
    }

    // Find Reviews By Agent Email
    public List<Review> findByAgentEmail(String email) {

        List<Review> result = new ArrayList<>();

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

        for (Review review : getAllReviews()) {

            if (review.getClientEmail().equalsIgnoreCase(email)) {
                result.add(review);
            }
        }

        return result;
    }

    // Update Review
    public boolean updateReview(Review updatedReview) {

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
    private void rewriteFile(List<Review> reviews) {

        try (BufferedWriter writer = Files.newBufferedWriter(
                Paths.get(FILE_PATH),
                StandardCharsets.UTF_8)) {

            for (Review review : reviews) {
                writer.write(convertToLine(review));
                writer.newLine();
            }

        } catch (IOException e) {
            throw new RuntimeException("Unable to update reviews file.", e);
        }
    }

    // Convert Review To Text Line
    private String convertToLine(Review review) {

        return String.join("|",
                review.getReviewId(),
                review.getAgentEmail(),
                review.getClientEmail(),
                String.valueOf(review.getRating()),
                review.getComment(),
                review.getDate(),
                review.getStatus()
        );
    }

    // Convert Text Line To Review
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
        } catch (NumberFormatException e) {
            return null;
        }
    }


}
