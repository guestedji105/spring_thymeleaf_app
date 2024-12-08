package org.example.utils;

import org.example.model.ProductReview;

import java.util.List;

public class CalculationUtility {
    public static double calculateAverageRating(List<ProductReview> reviews) {
        if (reviews.isEmpty()) {
            return 0.0;
        }

        double totalRating = 0.0;
        for (ProductReview review : reviews) {
            totalRating += review.getRating();
        }

        return Double.parseDouble(String.format("%.3f", totalRating / reviews.size()));
    }
}
