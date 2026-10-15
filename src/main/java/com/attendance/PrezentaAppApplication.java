package com.attendance;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(exclude = {
        org.springframework.boot.autoconfigure.r2dbc.R2dbcAutoConfiguration.class
})
public class PrezentaAppApplication {
    public static void main(String[] args) {
        SpringApplication.run(PrezentaAppApplication.class, args);
    }
}


