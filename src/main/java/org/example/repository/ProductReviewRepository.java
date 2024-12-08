package org.example.repository;

import org.example.model.Product;
import org.example.model.ProductReview;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductReviewRepository extends CrudRepository<ProductReview, Long> {
    List<ProductReview> findByProduct(Product product);
    void deleteByProduct(Product product);
}
