package com.catrescue;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.catrescue.mapper")
public class CatRescueApplication {
    public static void main(String[] args) {
        SpringApplication.run(CatRescueApplication.class, args);
        System.out.println("流浪猫救助管理系统启动成功！");
        System.out.println("访问地址: http://localhost:8080");
    }
}