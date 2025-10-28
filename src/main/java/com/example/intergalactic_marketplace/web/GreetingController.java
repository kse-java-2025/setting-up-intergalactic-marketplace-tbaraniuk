package com.example.intergalactic_marketplace.web;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/greeting")
public class GreetingController {
    @RequestMapping("/")
    public String greeting() {
        return "Hello World!";
    }
}
