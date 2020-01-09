package com.demo.springboot.Entity;

import lombok.Getter;
import lombok.Setter;

/**
 * @author - Jianghj
 * @since - 2020-01-07 15:05
 */
@Setter
@Getter
public class User {

    private Long userId;
    private String username;
    private String password;
}
