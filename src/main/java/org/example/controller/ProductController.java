package org.example.controller;

import jakarta.validation.Valid;
import org.example.model.Product;
import org.example.model.ProductReview;
import org.example.repository.ProductRepository;
import org.example.repository.ProductReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.example.utils.CalculationUtility.calculateAverageRating;

@Controller
public class ProductController {
    private final ProductRepository productRepository;
    private final ProductReviewRepository productReviewRepository;

    @Autowired
    public ProductController(ProductRepository productRepository, ProductReviewRepository productReviewRepository){
        this.productRepository = productRepository;
        this.productReviewRepository = productReviewRepository;
    }

    @GetMapping("/")
    public String showProductsList(Model model) {
        List<Product> products = (List<Product>) productRepository.findAll();
        Map<Long, Double> reviewAvgForProducts = new HashMap<>();

        for (Product product : products) {
            List<ProductReview> reviews = productReviewRepository.findByProduct(product);
            double averageRating = calculateAverageRating(reviews);
            reviewAvgForProducts.put(product.getId(), averageRating);
        }

        model.addAttribute("products", products);
        model.addAttribute("productRatings", reviewAvgForProducts);
        return "index";
    }

    @GetMapping("/add")
    public String showProductAddForm(Product product) {
        return "add-product";
    }

    @PostMapping("/add/product")
    public String addProduct(@Valid Product product, BindingResult result, Model model) {
        if (result.hasErrors())
            return "add-product";

        productRepository.save(product);
        return "redirect:/";
    }

    @GetMapping("/edit/{id}")
    public String showUpdateForm(@PathVariable("id") long id, Model model) {
        Product product = productRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid product Id:" + id));
        model.addAttribute("product", product);

        return "update-product";
    }

    @GetMapping("/product/{id}")
    public String showProductView(@PathVariable("id") long id, Model model) {
        Product product = productRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid product Id:" + id));
        model.addAttribute("product", product);

        List<ProductReview> reviews = productReviewRepository.findByProduct(product);
        double averageRating = calculateAverageRating(reviews);

        model.addAttribute("productRating", averageRating);
        model.addAttribute("reviews", reviews);

        return "view-product";
    }

    @PostMapping("/update/{id}")
    public String updateProduct(@PathVariable("id") long id, @Valid Product product, BindingResult result, Model model) {
        if (result.hasErrors()) {
            product.setId(id);
            return "update-product";
        }

        productRepository.save(product);

        return "redirect:/";
    }

    @Transactional
    @GetMapping("/delete/{id}")
    public String deleteProduct(@PathVariable("id") long id, Model model) {
        Product product = productRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid product Id:" + id));
        productReviewRepository.deleteByProduct(product);
        productRepository.delete(product);

        return "redirect:/";
    }
}
