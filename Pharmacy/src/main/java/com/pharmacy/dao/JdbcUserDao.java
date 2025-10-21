package com.pharmacy.dao;

import com.pharmacy.exception.NotFoundException;
import com.pharmacy.model.User;
import com.pharmacy.model.enums.Role;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class JdbcUserDao implements UserDao {

    private final JdbcTemplate jdbc;

    public JdbcUserDao(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    private static User mapRow(SqlRowSet rs) {
        User user = new User();
        user.setUserId              (rs.getLong("user_id"));
        user.setUsername            (rs.getString("username"));
        user.setPasswordHash        (rs.getString("password_hash"));
        user.setEmail               (rs.getString("email"));
        user.setRole                (Role.valueOf(rs.getString("role")));
        java.sql.Timestamp cts = rs.getTimestamp("created_at");
        user.setCreatedAt(cts != null ? cts.toLocalDateTime() : null);
        return user;
    }

    @Override
    public User getById(long userId) {
        String sql = "SELECT user_id, username, password_hash, email, role, created_at FROM pharmacy.app_user WHERE user_id = ?";
        SqlRowSet rs = jdbc.queryForRowSet(sql, userId);
        if (rs.next()) {
            return mapRow(rs);
        }
        throw new NotFoundException("User not found");
    }

    @Override
    public User getByUsername(String username) {
        String sql = "SELECT user_id, username, password_hash, email, role, created_at FROM pharmacy.app_user WHERE username = ?";
        SqlRowSet rs = jdbc.queryForRowSet(sql, username);
        if (rs.next()) {
            return mapRow(rs);
        }
        return null;
    }

    @Override
    public User getByEmail(String email) {
        String sql = "SELECT user_id, username, password_hash, email, role, created_at FROM pharmacy.app_user WHERE email = ?";
        SqlRowSet rs = jdbc.queryForRowSet(sql, email);
        if (rs.next()) {
            return mapRow(rs);
        }
        return null;
    }

    @Override
    public List<User> getAllUsers() {
        String sql = "SELECT user_id, username, password_hash, email, role, created_at FROM pharmacy.app_user ORDER BY username";
        SqlRowSet rs = jdbc.queryForRowSet(sql);
        List<User> users = new ArrayList<>();
        while (rs.next()) {
            users.add(mapRow(rs));
        }
        return users;
    }

    @Override
    public List<User> listAll() {
        return getAllUsers();
    }

    @Override
    public User create(User user) {
        String sql = "INSERT INTO pharmacy.app_user (username, password_hash, email, role) " +
                        "VALUES (?, ?, ?, ?::pharmacy.app_role) RETURNING user_id";
        Long userId = jdbc.queryForObject(sql, Long.class,
                user.getUsername(),
                user.getPasswordHash(),
                user.getEmail(),
                user.getRole().name()
        );
        user.setUserId(userId);
        return getById(user.getUserId());
    }

    @Override
    public User update(User user) {
        String sql = "UPDATE pharmacy.app_user SET username = ?, password_hash = ?, email = ?, " +
                        "role = ?::pharmacy.app_role " +
                        "WHERE user_id = ?";
        jdbc.update(sql,
                user.getUsername(),
                user.getPasswordHash(),
                user.getEmail(),
                user.getRole().name(),
                user.getUserId());
        return getById(user.getUserId());
    }

    @Override
    public void delete(long userId) {
        String sql = "DELETE FROM pharmacy.app_user WHERE user_id = ?";
        jdbc.update(sql, userId);
    }

    @Override
    public boolean usernameExists(String username) {
        String sql = "SELECT COUNT(*) FROM pharmacy.app_user WHERE username = ?";
        Integer count = jdbc.queryForObject(sql, Integer.class, username);
        return count != null && count > 0;
    }

    @Override
    public boolean emailExists(String email) {
        String sql = "SELECT COUNT(*) FROM pharmacy.app_user WHERE email = ?";
        Integer count = jdbc.queryForObject(sql, Integer.class, email);
        return count != null && count > 0;
    }
}
