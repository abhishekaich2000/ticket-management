package com.ticket.management.controller;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;

@RestController 
@RequestMapping("/users")
public class UserController {

    @GetMapping
    public String getUsers() {
        return "Hello World";
    }
}
