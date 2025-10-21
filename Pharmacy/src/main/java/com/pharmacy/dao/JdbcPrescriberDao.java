package com.pharmacy.dao;

import com.pharmacy.exception.NotFoundException;
import com.pharmacy.model.ContactInfo;
import com.pharmacy.model.Prescriber;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

@Repository
public class JdbcPrescriberDao implements PrescriberDao {

    private final JdbcTemplate jdbc;
    public JdbcPrescriberDao(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    private static Prescriber mapRow(SqlRowSet rs) {
        ContactInfo c = new ContactInfo(
                rs.getString("first_name"),
                rs.getString("last_name"),
                rs.getString("address"),
                rs.getString("city"),
                rs.getString("state"),
                rs.getString("zip_code"),
                rs.getString("phone"),
                rs.getString("email"));
        Prescriber p = new Prescriber();
         p.setPrescriberId      (rs.getLong("prescriber_id"));
         p.setNpi               (rs.getString("npi"));
         p.setContact           (c);
         return p;
    }

    @Override
    public Prescriber getById(long prescriberId) {
        SqlRowSet rs = jdbc.queryForRowSet("SELECT * FROM pharmacy.prescriber WHERE prescriber_id = ?",
                        prescriberId);
        if (rs.next()) {
            return mapRow(rs);
        }
        throw new NotFoundException("Prescriber not found");
    }

    @Override
    public List<Prescriber> listAll() {
        SqlRowSet rs = jdbc.queryForRowSet("SELECT * FROM pharmacy.prescriber ORDER BY last_name, first_name");
        List<Prescriber> list = new ArrayList<>();
        while (rs.next()) {
            list.add(mapRow(rs));
        }
        return list;
    }

    @Override
    public Prescriber getByNpi(String npi) {
        SqlRowSet rs = jdbc.queryForRowSet("SELECT * FROM pharmacy.prescriber WHERE npi = ?",
                        npi);
        if (rs.next()) {
            return mapRow(rs);
        }
        throw new NotFoundException("Prescriber not found");
    }

    @Override
    public List<Prescriber> searchByName(String namePart) {
        SqlRowSet rs = jdbc.queryForRowSet("SELECT * FROM pharmacy.prescriber " +
                        "WHERE first_name ILIKE ? OR last_name ILIKE ? ORDER BY last_name, first_name",
                        "%" + namePart + "%", "%" + namePart + "%");
        List<Prescriber> list = new ArrayList<>();
        while (rs.next()) {
            list.add(mapRow(rs));
        }
        return list;
    }

    @Override
    public Prescriber create(Prescriber p) {
        KeyHolder kh = new GeneratedKeyHolder();
        jdbc.update(conn -> {
            PreparedStatement ps = conn.prepareStatement("INSERT INTO pharmacy.prescriber" +
                        "(npi,first_name,last_name,address,city,state,zip_code,phone,email) VALUES (?,?,?,?,?,?,?,?,?)",
                        Statement.RETURN_GENERATED_KEYS);
            ContactInfo c = p.getContact();
            ps.setString(1, p.getNpi());
            ps.setString(2, c.getFirstName());
            ps.setString(3, c.getLastName());
            ps.setString(4, c.getAddress());
            ps.setString(5, c.getCity());
            ps.setString(6, c.getState());
            ps.setString(7, c.getZipCode());
            ps.setString(8, c.getPhone());
            ps.setString(9, c.getEmail());
            return ps;
        }, kh);
        p.setPrescriberId(kh.getKey().longValue());
        return p;
    }

    @Override
    public Prescriber update(Prescriber p) {
        ContactInfo c = p.getContact();
        int rows = jdbc.update("UPDATE pharmacy.prescriber " +
                        "SET npi = ?, first_name = ?, last_name = ?, address = ?, city = ?, state = ?, zip_code = ?, phone = ?, email = ? WHERE prescriber_id = ?",
                p.getNpi(),
                c.getFirstName(),
                c.getLastName(),
                c.getAddress(),
                c.getCity(),
                c.getState(),
                c.getZipCode(),
                c.getPhone(),
                c.getEmail(),
                p.getPrescriberId());
        if (rows == 0) {
            throw new NotFoundException("Prescriber not found");
        }
        return p;
    }

}
