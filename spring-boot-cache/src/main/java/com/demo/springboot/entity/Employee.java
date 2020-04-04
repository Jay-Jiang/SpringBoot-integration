package com.demo.springboot.entity;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * @author - Jianghj
 * @since - 2019-12-11 15:11
 */
@Getter
@Setter
public class Employee implements Serializable {

	private Long id;
	private String lastName;
	private String email;
	private int gender;
	private Long deptId;
}
