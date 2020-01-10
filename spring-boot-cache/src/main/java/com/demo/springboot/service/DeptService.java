package com.demo.springboot.service;

import com.demo.springboot.entity.Department;
import com.demo.springboot.mapper.DepartmentMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author - Jianghj
 * @since - 2019-12-11 15:44
 */
@Service
@Slf4j
public class DeptService {

    /**
     * 纯注解方式：使用 Mybatis 实现数据库访问
     */
    @Resource
    DepartmentMapper deptMapper;

    public Department addNewDept(Department dept) {
        int result = deptMapper.insertDept(dept);
        if (result > 0) {
            return dept;
        }
        return null;
    }

    /**
     * 清空缓存
     *
     * @param id
     */
    @CacheEvict(cacheNames = "{deptCache}", key = "\"dept_\"+#id", beforeInvocation = true)
    public int removeDept(Long id) {
        //默认情况下，即 beforeInvocation=false，如果方法发生异常中断，则不会执行缓存清除
        try {
            int i = 1 / 0;
        } catch (ArithmeticException e) {
            //当添加了 try-catch 捕获和处理异常，方法正常执行完毕，缓存会被清空
            log.error("删除 dept[id={}] 时，发生异常：[{}]", id, e.getMessage());
            //仅方便测试
            return 0;
        }
        return deptMapper.deleteDeptById(id);
    }

    /**
     * 更新缓存
     * 注意：此方法的返回值，应该与缓存中的结果类型保持一致
     * 不能一个是 int，一个是 Department 对象
     * 通过 condition 将更新与否和返回值绑定
     *
     * @param dept
     */
    @CachePut(cacheNames = "{deptCache}", key = "\"dept_\"+#dept.getId()"/*, unless = "#result == null"*/)
    public Department updateDept(Department dept) {
        int result = deptMapper.updateDeptById(dept);
        if (result > 0) {
            /*
             *  如果方法出现异常中断，则不会再更新缓存（此处需要注意）
             *  如果没有事务管理，数据库已经更新成功了，而缓存没有及时更新（数据不一致）
             */
            int i = 1 / 0;
            return dept;
        }
        return null;
    }

    /**
     * 缓存中没有，执行方法，将返回值添加到缓存中
     * 缓存中有，不再执行方法，直接返回缓存中的值
     *
     * @param id
     */
    @Cacheable(cacheNames = "{deptCache}", key = "\"dept_\"+#id", condition = "#id % 2 == 0")
    public Department getDept(Long id) {
        log.info("本次通过数据库来获取 dept 信息，id=[{}]", id);
        return deptMapper.selectDeptById(id);
    }

    @Cacheable(cacheNames = "deptCache", key = "#root.methodName")
    public List<Department> getDepts() {
        log.info("本次通过数据库来获取 depts 信息");
        return deptMapper.selectAllDepts();
    }
}
