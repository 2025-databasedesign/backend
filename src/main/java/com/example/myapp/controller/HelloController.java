package com.example.myapp.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @GetMapping("/")
    public String hello() {
        return "Hi from Local Spring Boot!";
    }

    @GetMapping("/insert-test")
    public String insertTest() {
        jdbcTemplate.execute("INSERT INTO employees (id, name, hire_date) VALUES (1000, 'GCP Connect', SYSDATE)");
        return "Inserted";
    }
}
