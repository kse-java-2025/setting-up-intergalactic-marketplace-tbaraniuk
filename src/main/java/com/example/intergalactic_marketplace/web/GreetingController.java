package com.example.intergalactic_marketplace.web;

import com.example.intergalactic_marketplace.config.GreetingProperties;
import com.example.intergalactic_marketplace.web.exception.GreetingNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static java.util.Optional.ofNullable;

@RestController
@RequestMapping("/api/v1/greetings")
public class GreetingController {
    private final GreetingProperties greetingProperties;

    public GreetingController (GreetingProperties greetingProperties) {
        this.greetingProperties = greetingProperties;
    }

    @GetMapping("")
    public String greeting() {
        return "Hello and Welcome to Intergalactic Marketplace!";
    }

    @GetMapping("/{name}")
    public ResponseEntity<String> greetingWithName (@PathVariable String name) {
        String greeting = ofNullable(greetingProperties.getGreetings().get(name))
                .map(GreetingProperties.Greeting::getMessage).orElseThrow(() -> new GreetingNotFoundException(name));
        return ResponseEntity.ok(greeting);
    }
}
