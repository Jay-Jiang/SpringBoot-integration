package com.demo.springboot.service;

import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;

/**
 * @author - Jianghj
 * @since - 2020-04-06 14:55
 * 异步任务、定时任务注解使用
 */
public class TaskService {

    @Async
    public void sayHelloAsync() {
        System.out.println("hello world");
    }

    /**
     * cron = "0 * * * * *"：固定时刻，表示任意星期、任意月份、任意日期的任意分钟的 0 秒启动一次，即每个整分钟启动一次（每分钟共计一次）
     * cron = "0/10 * * * * *"：起点时刻/步长，从整分钟开始，每间隔 10 秒启动一次（每分钟共计6次）
     * cron = "0-10 * * * * *"：区间（包头包尾），从整分钟开始，每个整分钟的 0 到 10 秒，每秒钟执行一次（每分钟共计11次）
     * cron = "0,10 * * * * *"：枚举时刻，每个分钟的 0 秒和 10 秒，都会执行一次（每分钟共计 2 次）
     *
     * cron = "0 0/5 14,18 * * ?"：每天的 14 点和 18 点，从整点开始，每隔 5 分钟执行一次
     * cron = "0 15 10 ？ * 1-6"：每个月的周一到周六的 10:15 分准时执行一次
     * cron = "0 0 2 ？ * 6L"：last，每个月的最后一个周六的凌晨 2:00 执行一次
     * cron = "0 0 2 LW * ？"：work，每个月最后一个工作日的凌晨 2:00 执行一次
     * cron = "0 0 2-4 ？ * 1#1"：第几个星期几，每个月的第一个星期一的凌晨 2 点到 4 点，每个整点执行一次（2:00/3:00/4:00）
     */
    @Scheduled(cron = "0 * * * * *")
    public void printScheduled(){
        System.out.println("执行定时任务...");
    }
}
