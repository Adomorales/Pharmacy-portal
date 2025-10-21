package com.pharmacy.dao;

import com.pharmacy.model.User;

import java.util.List;

public interface UserDao {

    User getById(long userId);

    User getByUsername(String username);

    User getByEmail(String email);

    List<User> listAll();

    User create(User user);

    User update(User user);

    void delete(long userId);

    boolean usernameExists(String username);

    boolean emailExists(String email);
}
