package com.demo.springboot.endpiont.health;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

/**
 * @author - Jianghj
 * @since - 2020-04-06 21:55
 * 自定义健康指示器，用于在 health EndPoint 信息中进行显示
 * - 注意：将实现类添加到容器中，以便生效
 */
@Component
public class MyAppHealthIndicator implements HealthIndicator {
    /**
     * 实现函数式接口，自定义健康检查逻辑
     */
    @Override
    public Health health() {
        try {
            // 自定义检查标准
            // ...
            // 如果一切正常
            return Health.up().withDetail("msg", "系统正常").build();
        } catch (Exception e) {}

        // 如果存在异常
        return Health.down().withDetail("msg", "程序xxx异常").build();
    }
}
