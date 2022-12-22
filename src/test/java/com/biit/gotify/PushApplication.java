package com.biit.gotify;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Service;

@SpringBootApplication
@Service
public class PushApplication {
    public static void main(String[] args) {
        SpringApplication.run(PushApplication.class, args);
    }
}
