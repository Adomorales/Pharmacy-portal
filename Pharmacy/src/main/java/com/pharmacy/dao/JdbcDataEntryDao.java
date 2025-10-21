package com.pharmacy.dao;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pharmacy.exception.DaoException;
import com.pharmacy.exception.NotFoundException;
import com.pharmacy.model.DataEntry;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class JdbcDataEntryDao implements DataEntryDao {

    private final JdbcTemplate jdbc;

    public JdbcDataEntryDao(JdbcTemplate jdbc) { this.jdbc = jdbc; }

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private static DataEntry mapRow(SqlRowSet rs) {
        String json = rs.getString("payload");
        try {
            DataEntry d = MAPPER.readValue(json, DataEntry.class);
            d.setEntryId(rs.getLong("entry_id"));

            if (d.getPrescriptionId() == 0) {
                String pk = rs.getString("entity_pk");
                if (pk != null && !pk.isEmpty()) {
                    try { d.setPrescriptionId(Long.parseLong(pk)); } catch (NumberFormatException ignored) {}
                }
            }
            return d;
        } catch (JsonProcessingException e) {
            throw new DaoException("Failed to parse data_entry payload for entry_id=" + rs.getLong("entry_id"), e);
        }
    }

    @Override
    public DataEntry getById(long entryId) {
        String sql = "SELECT entry_id, entity_pk, payload::text AS payload " +
                        "FROM pharmacy.data_entry WHERE entry_id = ?";
        SqlRowSet rs = jdbc.queryForRowSet(sql, entryId);
        if (rs.next()) {
            return mapRow(rs);
        }
        throw new NotFoundException("Data entry not found");
    }

    @Override
    public List<DataEntry> listQueuePage() {
        String sql = "SELECT entry_id, entity_pk, payload::text AS payload " +
                        "FROM pharmacy.data_entry_20min_queue LIMIT 50";
        SqlRowSet rs = jdbc.queryForRowSet(sql);
        List<DataEntry> list = new ArrayList<>();
        while (rs.next()) {
            list.add(mapRow(rs));
        }
        return list;
    }

    @Override
    public DataEntry create(DataEntry d) {
        String payload;
        try {
            payload = MAPPER.writeValueAsString(d);
        } catch (JsonProcessingException e) {
            throw new DaoException("Failed to serialize DataEntry payload", e);
        }
        String sql = """
            INSERT INTO pharmacy.data_entry (entered_by_user_id, entity_type, entity_pk, payload)
            VALUES (NULL, 'PRESCRIPTION', ?, ?::jsonb)
            RETURNING entry_id
        """;
        Long id = jdbc.queryForObject(sql, Long.class,
                String.valueOf(d.getPrescriptionId()),
                payload);
        d.setEntryId(id);
        return d;
    }

    @Override
    public DataEntry update(DataEntry d) {
        String payload;
        try {
            payload = MAPPER.writeValueAsString(d);
        } catch (JsonProcessingException e) {
            throw new DaoException("Failed to serialize DataEntry payload", e);
        }
        String sql = """
            UPDATE pharmacy.data_entry
            SET entity_pk = ?, payload = ?::jsonb
            WHERE entry_id = ?
        """;
        int rows = jdbc.update(sql,
                String.valueOf(d.getPrescriptionId()),
                payload,
                d.getEntryId());
        if (rows == 0) throw new NotFoundException("Data entry not found: id=" + d.getEntryId());
        return d;
    }

}
