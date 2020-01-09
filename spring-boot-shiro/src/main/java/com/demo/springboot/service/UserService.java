package com.demo.springboot.service;

import com.demo.springboot.Entity.User;
import com.demo.springboot.util.CipherUtil;
import org.apache.shiro.util.ByteSource;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author - Jianghj
 * @since - 2020-01-07 15:04
 */
@Service
public class UserService {

    public User getUserByUsername(String username) {
        if (!Objects.equals("zhangsan", username)) {
            return null;
        }

        User user = new User();
        user.setUserId(1L);
        user.setUsername("zhangsan");
        String passwor = CipherUtil.shiroMd5Encrypt("000", ByteSource.Util.bytes("zhangsan"), 3);
        user.setPassword(passwor);
        return user;
    }

    public List<String> getRolesByUserId(Long userId) {
        ArrayList<String> roles = new ArrayList<>();
        roles.add("admin");
        return roles;
    }

    public List<String> getPermissionsByUserId(Long userId) {
        ArrayList<String> permissions = new ArrayList<>();
        permissions.add("user:*");
        return permissions;
    }
}
