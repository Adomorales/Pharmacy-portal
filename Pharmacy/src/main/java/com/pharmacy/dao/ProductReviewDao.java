package com.pharmacy.dao;

import com.pharmacy.model.ProductReview;
import com.pharmacy.model.enums.ReviewStatus;

import java.util.List;

public interface ProductReviewDao {

    ProductReview getById(long productReviewId);

    List<ProductReview> listByProduct(long productId);

    ProductReview create(ProductReview r);

    ProductReview updateStatus(long productReviewId, ReviewStatus status);

}
