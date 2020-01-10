package com.demo.springboot.controller;

import com.demo.springboot.entity.Department;
import com.demo.springboot.service.DeptService;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author - Jianghj
 * @since - 2019-12-11 13:44
 * 测试缓存注解
 */
@RestController
public class DeptController {

    @Resource
    DeptService deptService;

    @RequestMapping("/depts")
    public List<Department> getDepts() {
        return deptService.getDepts();
    }

    @RequestMapping("/dept/{id}")
    public Department getDepts(@PathVariable("id") Long deptId) {
        return deptService.getDept(deptId);
    }

    @RequestMapping("/dept/add/{name}")
    public Department addNewDept(@PathVariable("name") String deptName) {
        Department dept = new Department();
        dept.setDeptName(deptName);
        return deptService.addNewDept(dept);
    }

    @RequestMapping("/dept/delete/{id}")
    public int deleteDept(@PathVariable("id") Long deptId) {
        return deptService.removeDept(deptId);
    }

    @RequestMapping("/dept/update/{id}/{name}")
    public Department updateDept(@PathVariable("id") Long deptId, @PathVariable("name") String deptName) {
        Department dept = new Department();
        dept.setId(deptId);
        dept.setDeptName(deptName);
        return deptService.updateDept(dept);
    }

}
