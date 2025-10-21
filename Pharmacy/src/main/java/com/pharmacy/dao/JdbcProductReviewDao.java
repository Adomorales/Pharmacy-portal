package com.pharmacy.dao;

import com.pharmacy.exception.NotFoundException;
import com.pharmacy.model.ProductReview;
import com.pharmacy.model.enums.ReviewStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Repository
public class JdbcProductReviewDao implements ProductReviewDao {

    private final JdbcTemplate jdbc;

    public JdbcProductReviewDao(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    private static ProductReview mapRow(SqlRowSet rs) {
        ProductReview r = new ProductReview();
        r.setProductReviewId            (rs.getLong("review_id"));
        r.setProductId                  (rs.getLong("product_id"));
        r.setReviewerUserId             (rs.getLong("reviewer_user_id"));
        r.setComment                    (rs.getString("comments"));
        java.sql.Timestamp cts = rs.getTimestamp("created_at");
        r.setReviewedAt(cts != null ? cts.toLocalDateTime() : null);
        String status = rs.getString("status");
        if (status != null) r.setStatus(ReviewStatus.valueOf(status));
        return r;
    }

    @Override
    public ProductReview getById(long productReviewId) {
        String sql = "SELECT review_id, product_id, reviewer_user_id, comments, created_at, status FROM pharmacy.product_review WHERE review_id = ?";
        SqlRowSet rs = jdbc.queryForRowSet(sql, productReviewId);
        if (rs.next()) return mapRow(rs);
        throw new NotFoundException("Product review not found");
    }

    @Override
    public List<ProductReview> listByProduct(long productId) {
        String sql = "SELECT review_id, product_id, reviewer_user_id, comments, created_at, status FROM pharmacy.product_review WHERE product_id = ? ORDER BY created_at DESC";
        SqlRowSet rs = jdbc.queryForRowSet(sql, productId);
        List<ProductReview> list = new ArrayList<>();
        while (rs.next()) list.add(mapRow(rs));
        return list;
    }

    @Override
    public ProductReview create(ProductReview r) {
        String sql = "INSERT INTO pharmacy.product_review (product_id, reviewer_user_id, rating, comments, status) VALUES (?, ?, ?, ?, ?) RETURNING review_id";
        Long id = jdbc.queryForObject(sql, Long.class,
                r.getProductId(),
                r.getReviewerUserId(),
                5,
                r.getComment(),
                (r.getStatus() == null ? ReviewStatus.PENDING.name() : r.getStatus().name()));
        r.setProductReviewId(id);
        return getById(r.getProductReviewId());
    }

    @Override
    public ProductReview updateStatus(long productReviewId, ReviewStatus status) {
        int rows = jdbc.update("UPDATE pharmacy.product_review SET status = ? WHERE review_id = ?", status.name(), productReviewId);
        if (rows == 0) throw new NotFoundException("Product review not found");
        return getById(productReviewId);
    }
}
