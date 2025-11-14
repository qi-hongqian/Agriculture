package com.rfeng.agri;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author 齐洪乾
 * @version 1.00
 * @time 2025/11/13 18:17
 */
//http://localhost:8084/doc.html
@SpringBootApplication
@MapperScan("com.rfeng.agri.mapper")
public class AnswerServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(AnswerServiceApplication.class, args);
    }
}