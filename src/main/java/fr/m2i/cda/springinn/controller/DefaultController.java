package fr.m2i.cda.springinn.controller;

import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import fr.m2i.cda.springinn.entity.User;
import fr.m2i.cda.springinn.service.MailService;

@RestController
public class DefaultController {

    private MailService mailService;

    public DefaultController(MailService mailService) {
        this.mailService = mailService;
    }

    @GetMapping("/")
    public Map<String,String> welcome() {
        return Map.of("message", "welcome to this spring app");
    }

    @GetMapping("/test-mail")
    public String sendTestMail() {
        User user = new User();
        user.setEmail("customer2@test.com");
        user.setId("user3");
        mailService.sendEmailValidation(user);
        return "Mail sent";
    }
}
