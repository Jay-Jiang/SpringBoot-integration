package com.demo.springboot.controller;

import com.demo.springboot.entity.Employee;
import com.demo.springboot.service.EmployeeService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author - Jianghj
 * @since - 2019-12-14 11:13
 * 测试缓存
 */
@RestController
public class EmpController {

	@Resource
	EmployeeService employeeService;

	@GetMapping("/emp/{id}")
	public Employee getEmp(@PathVariable("id") Long empId) {
		return employeeService.getEmp(empId);
	}
}
