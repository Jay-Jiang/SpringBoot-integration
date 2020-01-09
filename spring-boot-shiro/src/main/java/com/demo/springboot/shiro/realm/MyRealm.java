package com.demo.springboot.shiro.realm;

import com.demo.springboot.Entity.User;
import com.demo.springboot.service.UserService;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.ByteSource;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author - Jianghj
 * @since - 2020-01-07 14:42
 * 自定义获取用户验证信息和权限信息
 * 需要将自定义 Realm 注入到容器中，才能生效
 */
@Component
public class MyRealm extends AuthorizingRealm {

    // @Resource
    UserService userService = new UserService();

    /**
     * 重写，便于自定义 Realm 的名称，用于登录认证
     */
    @Override
    public String getName() {
        return "myRealm";
    }

    /**
     * 用户登录：仅用于获取用户认证数据
     * - 不做密码校对，只提供认证的数据即可
     *
     * @param token 用户的登录信息
     * @return 用户的认证数据
     * @throws AuthenticationException 认证异常（用户名不存在、密码错误）
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        /*
         *  token：是登录时，封装的 UsernamePasswordToken 对象
         *   - 包含：用户名、密码、是否RememberMe
         *   - principal：等同于用户名，或用户实例 User
         *   - credential：等同于密码
         *  此处，获取登录的用户名
         */
        String userName = (String) token.getPrincipal();

        /*
         * 通过用户名，查询数据库，获取系统中对应的用户信息
         *  - 如果查询到对应的用户，便将系统保存的用户认证信息，进行封装返回
         *  - 用于外部 shiro 实现的密码校对
         */
        User user = userService.getUserByUsername(userName);

        //如果未查询到有效用户-用户名不存在
        if (user == null) {
            // 当用户不存在时（无有效信息），返回null，shiro 会在外层判断，并抛出用户不存在的异常
            return null;
        }

        /*
         * 如果用户存在，则返回有效的 AuthenticationInfo 实例
         *  - 创建 SimpleAuthenticationInfo 对象
         *  - 参数依次是：登录主体的信息、系统保存的用户认证信息（密码）、加密的盐值、当前 Realm 名称
         *  - 登录主体的信息：user，登录成功后，会保存到系统中，供后续使用 SecurityUtils.getSubject()
         *  - 注意：用户登录的密码校验，shiro 已经在外部封装，无需在此进行操作判断，返回认证数据即可
         */
        return new SimpleAuthenticationInfo(user, user.getPassword(), ByteSource.Util.bytes(userName), getName());
    }

    /**
     * 获取用户权限信息
     * - 只获取当前用户的角色信息或者权限信息，并封装返回
     * - 不做具体的权限检验实现
     *
     * @param principals 当前登录的主体
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        /*
         * principals：登录主体的信息
         *  - 是认证过程中，返回的 SimpleAuthenticationInfo 实例的构造方法中的第一个参数，即 user
         *  - 此时，获取的主体，是已经完成登录的，必然是有效的
         */
        User user = (User) principals.getPrimaryPrincipal();

        // 获取当前主体的角色信息
        List<String> roles = userService.getRolesByUserId(user.getUserId());
        // 获取当前主体的权限信息
        List<String> permissions = userService.getPermissionsByUserId(user.getUserId());

        // 将当前主体的权限信息，包装在 AuthorizationInfo 实例中，返回到外部，进行权限校验
        SimpleAuthorizationInfo authorizationInfo = new SimpleAuthorizationInfo();
        authorizationInfo.addRoles(roles);
        authorizationInfo.addStringPermissions(permissions);
        return authorizationInfo;
    }
}
