package com.pharmacy.dao;

import com.pharmacy.exception.NotFoundException;
import com.pharmacy.model.Facility;
import com.pharmacy.model.enums.FacilityType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class JdbcFacilityDao implements FacilityDao {

    private final JdbcTemplate jdbc;

    public JdbcFacilityDao(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    private static Facility mapRow(SqlRowSet rs) {
        Facility f = new Facility();
        f.setFacilityId(rs.getLong("facility_id"));
        f.setName(rs.getString("name"));
        f.setType(FacilityType.valueOf(rs.getString("type")));
        f.setPhone(rs.getString("phone"));
        f.setAddress(rs.getString("address"));
        f.setCity(rs.getString("city"));
        f.setState(rs.getString("state"));
        f.setZipCode(rs.getString("zip_code"));
        return f;
    }

    @Override
    public Facility getById(long facilityId) {
        SqlRowSet rs = jdbc.queryForRowSet("SELECT * FROM pharmacy.facility WHERE facility_id = ?", facilityId);
        if (rs.next()) {
            return mapRow(rs);
        }
        throw new NotFoundException("Facility not found");
    }

    @Override
    public List<Facility> listAll() {
        SqlRowSet rs = jdbc.queryForRowSet("SELECT * FROM pharmacy.facility ORDER BY name");
        List<Facility> facilities = new ArrayList<>();
        while (rs.next()) {
            facilities.add(mapRow(rs));
        }
        return facilities;
    }

    @Override
    public Facility create(Facility facility) {
        String sql = "INSERT INTO pharmacy.facility (name, type, phone, address, city, state, zip_code) VALUES (?, ?, ?, ?, ?, ?, ?) RETURNING facility_id";
        Long id = jdbc.queryForObject(sql, Long.class,
                facility.getName(),
                facility.getType().name(),
                facility.getPhone(),
                facility.getAddress(),
                facility.getCity(),
                facility.getState(),
                facility.getZipCode());
        facility.setFacilityId(id);
        return facility;
    }

    @Override
    public Facility update(Facility facility) {
        String sql = "UPDATE pharmacy.facility SET name = ?, type = ?, phone = ?, address = ?, city = ?, state = ?, zip_code = ? WHERE facility_id = ?";
        int rows = jdbc.update(sql,
                facility.getName(),
                facility.getType().name(),
                facility.getPhone(),
                facility.getAddress(),
                facility.getCity(),
                facility.getState(),
                facility.getZipCode(),
                facility.getFacilityId());
        if (rows == 0) {
            throw new NotFoundException("Facility not found");
        }
        return facility;
    }

    @Override
    public boolean deleteById(long facilityId) {
        return jdbc.update("DELETE FROM pharmacy.facility WHERE facility_id = ?", facilityId) > 0;
    }
}
