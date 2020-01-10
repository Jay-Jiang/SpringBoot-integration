package com.demo.springboot.service;

import com.demo.springboot.entity.Employee;
import com.demo.springboot.mapper.EmployeeMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author - Jianghj
 * @since - 2019-12-14 11:11
 */
@Service
public class EmployeeService {

	@Resource
	EmployeeMapper employeeMapper;

	public Employee getEmp(Long empId) {
		return employeeMapper.selectEmpById(empId);
	}
}
