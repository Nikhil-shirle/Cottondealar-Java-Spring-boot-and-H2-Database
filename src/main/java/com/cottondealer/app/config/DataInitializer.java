package com.cottondealer.app.config;

import com.cottondealer.app.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserService userService;

    @Override
    public void run(String... args) throws Exception {
        if (userService.findByUsername("admin").isEmpty()) {
            userService.registerUser("admin", "admin123", "ADMIN");
            System.out.println("Default admin user created: admin / admin123");
        }
    }
}
