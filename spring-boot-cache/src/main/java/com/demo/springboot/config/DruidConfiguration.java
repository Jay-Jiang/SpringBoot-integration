package com.demo.springboot.config;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.support.http.StatViewServlet;
import com.alibaba.druid.support.http.WebStatFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

/**
 * @author - Jianghj
 * @since - 2020-01-10 09:36
 * 进行 Druid 连接池的属性配置
 */
@Configuration
public class DruidConfiguration {
    private static final Logger logger = LoggerFactory.getLogger(DruidConfiguration.class);

    /** druid 监控页面登录用户名 */
    @Value("${spring.druid.login.username:admin}")
    private String druidUsername;

    /** druid 监控页面登录密码 */
    @Value("${spring.druid.login.password:admin}")
    private String druidPassword;

    /** 允许访问监控页面的 ip，默认所有ip 都可访问 */
    @Value("${spring.druid.admin.allow:}")
    private String allow = "";

    /** 不允许访问监控页面的 ip，默认为空 */
    @Value("${spring.druid.admin.deny:}")
    private String deny = "";

    /** druid 不拦截的请求，默认是静态资源 */
    @Value("${spring.druid.admin.exclusions:*.js,*.css,*.ico,*.gif,*.jpg,*.png,/druid/*}")
    private String exclusions;

    /**
     * 将自定义配置的 Druid 注入到容器中
     */
    @Bean(initMethod = "init", destroyMethod = "close")
    @ConfigurationProperties(prefix = "spring.druid")
    public DataSource druidDataSource() {
        logger.info("custom durid datasource init...");
        return new DruidDataSource();
    }

    /**
     * 配置 Druid 监控
     * 添加监控的 Servlet ，拦截所有 /druid 请求
     */
    @Bean
    public ServletRegistrationBean statViewServlet() {

        String adminUrl = "/druid/*";
        ServletRegistrationBean<StatViewServlet> registrationBean = new ServletRegistrationBean<>();
        registrationBean.setServlet(new StatViewServlet());
        // url 匹配
        registrationBean.addUrlMappings(adminUrl);
        // IP 白名单 (没有配置或者为空，则允许所有访问)
        registrationBean.addInitParameter("allow", this.allow);
        // IP 黑名单 (存在共同时，deny 优先于allow)
        registrationBean.addInitParameter("deny", this.deny);
        // druid 管理页面登录名
        registrationBean.addInitParameter("loginUsername", this.druidUsername);
        // druid 管理页面登录密码
        registrationBean.addInitParameter("loginPassword", this.druidPassword);
        // 禁用 druid 管理页面上的“Reset All” 功能
        registrationBean.addInitParameter("resetEnable", "false");
        logger.info("Druid 数据库监控地址：[{}]", adminUrl);
        return registrationBean;
    }

    /**
     * 配置 Druid 监控
     * 添加监控的 Filter ，拦截所有web 请求
     */
    @Bean
    public FilterRegistrationBean webStatFilter() {
        FilterRegistrationBean registrationBean = new FilterRegistrationBean();
        registrationBean.setFilter(new WebStatFilter());
        // 拦截所有请求
        registrationBean.addUrlPatterns("/*");
        // 忽略资源
        registrationBean.addInitParameter("exclusions", this.exclusions);
        registrationBean.addInitParameter("profileEnable", "true");
        registrationBean.addInitParameter("principalCookieName", "USER_COOKIE");
        registrationBean.addInitParameter("principalSessionName", "USER_SESSION");
        return registrationBean;
    }
}
