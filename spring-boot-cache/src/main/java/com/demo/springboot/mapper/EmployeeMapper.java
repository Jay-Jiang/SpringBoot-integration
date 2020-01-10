package com.demo.springboot.mapper;

import com.demo.springboot.entity.Employee;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author - Jianghj
 * @since - 2019-12-14 11:03
 */
@Mapper
public interface EmployeeMapper {

	Employee selectEmpById(Long id);
}
