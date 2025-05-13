package com.openelements.conduct.endpoint;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MainEndpoint {

    @GetMapping("/")
    public String index() {
        return "Welcome to the OpenElements Conduct Guardian";
    }
}
