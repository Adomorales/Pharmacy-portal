package com.pharmacy.dao;

import com.pharmacy.exception.NotFoundException;
import com.pharmacy.model.DataReview;
import com.pharmacy.model.enums.ReviewStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Repository
public class JdbcDataReviewDao implements DataReviewDao {

    private final JdbcTemplate jdbc;

    public JdbcDataReviewDao(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    private static DataReview mapRow(SqlRowSet rs) {
        DataReview r = new DataReview();
        r.setDataReviewId      (rs.getLong("review_id"));
        r.setDataEntryId       (rs.getLong("entry_id"));
        r.setReviewerUserId    (rs.getLong("reviewed_by_user_id"));
        r.setNotes             (rs.getString("notes"));
        java.sql.Timestamp ts = rs.getTimestamp("reviewed_at");
        r.setReviewedAt        (ts != null ? ts.toLocalDateTime() : null);
        r.setStatus    (ReviewStatus.fromDbValue(rs.getString("status")));
        return r;
    }

    @Override
    public DataReview getById(long dataReviewId) {
        String sql = "SELECT review_id, entry_id, reviewed_by_user_id, status, notes, reviewed_at " +
                        "FROM pharmacy.data_review WHERE review_id = ?";
        SqlRowSet rs = jdbc.queryForRowSet(sql, dataReviewId);
        if (rs.next()) return mapRow(rs);
        throw new NotFoundException("Data review not found");
    }

    @Override
    public List<DataReview> listByDataEntry(long dataEntryId) {
        String sql = "SELECT review_id, entry_id, reviewed_by_user_id, status, notes, reviewed_at " +
                        "FROM pharmacy.data_review WHERE entry_id = ? ORDER BY reviewed_at DESC";
        SqlRowSet rs = jdbc.queryForRowSet(sql, dataEntryId);
        List<DataReview> list = new ArrayList<>();
        while (rs.next()) list.add(mapRow(rs));
        return list;
    }

    @Override
    public DataReview create(DataReview r) {
        String sql = "INSERT INTO pharmacy.data_review (entry_id, reviewed_by_user_id, status, notes) " +
                        "VALUES (?, ?, ?, ?) RETURNING review_id";
        Long id = jdbc.queryForObject(sql, Long.class,
                r.getDataEntryId(),
                r.getReviewerUserId(),
                (r.getStatus() == null ? ReviewStatus.PENDING.name() : r.getStatus()),
                r.getNotes());
        r.setDataReviewId(id);
        return getById(r.getDataReviewId());
    }
}
