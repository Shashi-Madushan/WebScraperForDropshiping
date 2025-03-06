package com.shashimadushan.aliscapper.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/hello")
public class AdminController {
    @GetMapping
    public String sayHello() {
        return "Hello World!";
    }

}
