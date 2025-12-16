package com.example.demo.Controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")

public class VerifyController {

    @GetMapping
    public ResponseEntity<String> root() {
        return ResponseEntity.ok("Application is running");
    }
}