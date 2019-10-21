package com.auth.service;

import com.auth.po.User;
import com.auth.query.AuthQuery;
import org.springframework.stereotype.Service;

/**
 * Created by fangyitao on 2019/8/13.
 */
@Service
public class AuthService {
    public User auth(AuthQuery query) {
        return new User(1L);
    }
}
