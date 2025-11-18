package fr.m2i.cda.springinn.controller;

import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DefaultController {

    @GetMapping("/")
    public Map<String,String> welcome() {
        return Map.of("message", "welcome to this spring app");
    }
}
