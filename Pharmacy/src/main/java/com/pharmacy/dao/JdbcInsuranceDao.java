package com.pharmacy.dao;

import com.pharmacy.exception.NotFoundException;
import com.pharmacy.model.InsuranceInfo;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class JdbcInsuranceDao implements InsuranceDao {

    private final JdbcTemplate jdbc;

    public JdbcInsuranceDao(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    private static InsuranceInfo mapRow(SqlRowSet rs) {
        InsuranceInfo i = new InsuranceInfo();
        i.setInsuranceId(rs.getLong("patient_id")); // Use patient_id as insurance_id
        i.setPatientId  (rs.getLong("patient_id"));
        i.setProvider   (rs.getString("payer_name"));
        i.setMemberId   (rs.getString("member_id"));
        i.setGroupId    (rs.getString("group_no"));
        i.setBin        (rs.getString("bin"));
        i.setPcn        (rs.getString("pcn"));
        return i;
    }

    @Override
    public List<InsuranceInfo> listAll() {
        SqlRowSet rs = jdbc.queryForRowSet(
                "SELECT patient_id, payer_name, member_id, group_no, bin, pcn " +
                        "FROM pharmacy.insurance_profile ORDER BY patient_id");
        List<InsuranceInfo> list = new ArrayList<>();
        while (rs.next()) list.add(mapRow(rs));
        return list;
    }

    @Override
    public InsuranceInfo getById(long insuranceId) {
        SqlRowSet rs = jdbc.queryForRowSet(
                "SELECT patient_id, payer_name, member_id, group_no, bin, pcn " +
                        "FROM pharmacy.insurance_profile WHERE patient_id = ?",
                insuranceId);
        if (rs.next()) return mapRow(rs);
        throw new NotFoundException("Insurance profile not found: id=" + insuranceId);
    }

    @Override
    public List<InsuranceInfo> listByPatient(long patientId) {
        SqlRowSet rs = jdbc.queryForRowSet(
                "SELECT patient_id, payer_name, member_id, group_no, bin, pcn " +
                        "FROM pharmacy.insurance_profile WHERE patient_id = ?",
                patientId);
        List<InsuranceInfo> list = new ArrayList<>();
        while (rs.next()) list.add(mapRow(rs));
        return list;
    }

    @Override
    public InsuranceInfo create(InsuranceInfo info) {
        String sql = """
            INSERT INTO pharmacy.insurance_profile
              (patient_id, payer_name, member_id, group_no, bin, pcn)
            VALUES (?, ?, ?, ?, ?, ?)
        """;
        jdbc.update(sql,
                info.getPatientId(),
                info.getProvider(),
                info.getMemberId(),
                info.getGroupId(),
                info.getBin(),
                info.getPcn());
        return info;
    }

    @Override
    public InsuranceInfo update(InsuranceInfo info) {
        String sql = """
            UPDATE pharmacy.insurance_profile
            SET payer_name = ?, member_id = ?, group_no = ?, bin = ?, pcn = ?
            WHERE patient_id = ?
        """;
        int rows = jdbc.update(sql,
                info.getProvider(),
                info.getMemberId(),
                info.getGroupId(),
                info.getBin(),
                info.getPcn(),
                info.getPatientId());
        if (rows == 0) throw new NotFoundException("Insurance profile not found: patient_id=" + info.getPatientId());
        return info;
    }

    @Override
    public boolean delete(long insuranceId) {
        String sql = "DELETE FROM pharmacy.insurance_profile WHERE patient_id = ?";
        return jdbc.update(sql, insuranceId) > 0;
    }
}
