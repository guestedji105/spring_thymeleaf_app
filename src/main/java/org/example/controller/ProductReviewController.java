package org.example.controller;

import org.example.model.Product;
import org.example.model.ProductReview;
import org.example.repository.ProductRepository;
import org.example.repository.ProductReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.example.utils.CalculationUtility.calculateAverageRating;

@Controller
@RequestMapping("/products")
public class ProductReviewController {

    private final ProductRepository productRepository;
    private final ProductReviewRepository productReviewRepository;

    @Autowired
    public ProductReviewController(ProductRepository productRepository, ProductReviewRepository productReviewRepository) {
        this.productRepository = productRepository;
        this.productReviewRepository = productReviewRepository;
    }

    @GetMapping("/{productId}/reviews/add")
    public String showReviewAddForm(@PathVariable("productId") long productId, Model model) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid product Id:" + productId));

        ProductReview review = new ProductReview();
        review.setProduct(product);
        model.addAttribute("review", review);
        model.addAttribute("productId", productId);

        return "review_form";
    }

    @GetMapping("/{productId}/reviews")
    public String getProductReviews(@PathVariable("productId") long productId, Model model) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid product Id:" + productId));
        List<ProductReview> reviews = productReviewRepository.findByProduct(product);
        model.addAttribute("product", product);

        double averageRating = calculateAverageRating(reviews);

        model.addAttribute("productRating", averageRating);

        model.addAttribute("reviews", reviews);
        return "view-product";
    }

    @PostMapping("/{productId}/reviews")
    public String addProductReview(@PathVariable("productId") long productId, @ModelAttribute ProductReview review,
                                   BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("error", "Please correct the review form");
            return "review_form";
        }

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid product Id:" + productId));
        review.setProduct(product);
        productReviewRepository.save(review);
        return "redirect:/products/{productId}/reviews";
    }
}
