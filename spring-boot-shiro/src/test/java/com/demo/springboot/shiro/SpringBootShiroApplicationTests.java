package com.demo.springboot.shiro;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.UnauthorizedException;
import org.apache.shiro.config.IniSecurityManagerFactory;
import org.apache.shiro.crypto.hash.Md5Hash;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.subject.Subject;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;

@SpringBootTest
class SpringBootShiroApplicationTests {

    /**
     * 查看 shiro 登录认证的底层实现
     */
    @Test
    void authenticateTest() {
        /*
         *  1. 创建 SecurityManagerFactory
         *     - 通过工厂模式，获取 SecurityManager 实例
         *     - 使用 shiro.ini 配置文件，创建 initReam，来保存用户认证信息
         */
        IniSecurityManagerFactory factory = new IniSecurityManagerFactory("classpath:shiro.ini");
        // 2. 通过工厂实例，获取 SecurityManager 实例
        SecurityManager manager = factory.getInstance();
        // 3. 将 SecurityManager 实例绑定到当前运行环境中，使得系统随时随地可以直接使用该 SecurityManager 实例
        SecurityUtils.setSecurityManager(manager);
        // 4. 获取当前登录的主体
        Subject subject = SecurityUtils.getSubject();
        // 5. 封装用户的登录信息，保存在 Token 中
        UsernamePasswordToken userToken = new UsernamePasswordToken("zhangsan", "000");
        try {
            // 6. 用户登录-使用用户的登录信息进行认证操作
            subject.login(userToken);
        } catch (Exception e) {
            // 发生异常，则登录失败
        }
        // 7. 查看用户认证（登录）是否成功
        System.out.println("用户登录成功：" + subject.isAuthenticated());

        // 8. 查看用户是否拥有某个角色
        // hasRole：返回值 boolean，true 表示拥有指定角色，false 表示不拥有
        System.out.println("用户是admin：" + subject.hasRole("admin"));
        System.out.println("用户是super admin：" + subject.hasRole("super-admin"));
        // hasAllRoles：返回值 boolean，true 表示拥有全部指定的角色，false 表示不全部拥有
        System.out.println("hasAllRoles: " + subject.hasAllRoles(Arrays.asList("admin", "role1", "role2")));
        // hasRoles: 返回值 boolean[] ，每个角色都返回对应的拥有结果（一一对应），true 表示拥有该角色，false 表示不拥有
        System.out.println("hasRoles: " + Arrays.toString(subject.hasRoles(Arrays.asList("admin", "role1", "role2"))));

        //checkRole: 没有返回值，如果用户拥有该角色，无任何操作，没有该角色，直接抛出异常：UnauthorizedException
        subject.checkRole("admin");
        //checkRoles: 没有返回值，如果用户拥有指定的全部角色，无任何操作，没有其中之一，就抛出异常：UnauthorizedException
        try {
            subject.checkRoles("admin", "role1", "role2");
        } catch (UnauthorizedException e) {
            //报错信息：Subject does not have role [role1]
            System.out.println("抛异常了 -----> "+e.getMessage());
        }

        // 9. 查看用户是否拥有某个权限
        // isPermitted 单入参: 返回值 boolean ，true 表示用户拥有指定的权限，false 表示不拥有
        System.out.println("isPermitted 单个权限：" + subject.isPermitted("user:delete"));
        // isPermittedAll: 返回值 boolean ，true 表示用户拥有指定的全部权限，false 表示不全部拥有
        System.out.println("isPermittedAll ：" + subject.isPermittedAll("user:list","user:delete"));
        // isPermitted 多入参: 返回值 boolean[] ，返回每一个指定权限的拥有结果（一一对应），true 表示用户拥有指定的权限，false 表示不拥有
        System.out.println("isPermitted 多个权限 ：" + Arrays.toString(subject.isPermitted("user:list","user:delete")));

        // checkPermission：无返回值，如果用户拥有该权限，无任何操作，如果没有，抛出异常：UnauthorizedException
        subject.checkPermission("user:delete");

        // 10. 用户登出-注销操作
        subject.logout();
        // 11. 查看用户登出之后的认证状态
        System.out.println("用户依旧是登录的：" + subject.isAuthenticated());
    }

    /**
     * 测试 shiro 的 MD5 加密算法的使用
     */
    @Test
    void shiroMd5Test() {
        //加密的目标（如用户密码）
        String password = "123456";
        //盐
        String salt = "hello";
        //散列次数（循环加密的次数）
        int hashIterations = 2;

        // 1.不加盐的md5
        Md5Hash md5 = new Md5Hash(password);
        System.out.println(md5.toString());

        // 2.加盐的md5
        md5 = new Md5Hash(password, salt);
        System.out.println(md5.toString());

        // 3.加盐并设置散列次数的md5
        md5 = new Md5Hash(password, salt, hashIterations);
        System.out.println(md5.toString());

        /*
         *  4.利用 SimpleHash 来设置 md5 加密
         *      - 上面三种都可以通过这个来设置，这里举例加盐加散列次数的
         *      - 第一个参数是算法名称（md5、sha..），第二个是要加密的目标，第三个参数是盐，第四个是散列次数
         */
        SimpleHash hash = new SimpleHash("md5", password, salt, hashIterations);
        System.out.println(hash.toString());
    }

    @Test
    void contextLoads() {
    }

}
