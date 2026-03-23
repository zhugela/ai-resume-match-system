package com.liangge.resumematch;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.liangge.resumematch.mapper")
public class ResumeMatchApplication {
    public static void main(String[] args) {
        SpringApplication.run(ResumeMatchApplication.class, args);
    }
}
