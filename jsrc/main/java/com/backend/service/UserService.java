package com.backend.service;

import com.backend.model.User;
import java.util.List;

public interface UserService {
    User getUserById(String userId);
    void saveUser(User user);
}