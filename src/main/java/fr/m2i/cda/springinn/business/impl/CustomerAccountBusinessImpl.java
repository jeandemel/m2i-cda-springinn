package fr.m2i.cda.springinn.business.impl;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import fr.m2i.cda.springinn.business.CustomerAccountBusiness;
import fr.m2i.cda.springinn.business.exception.AccountValidationException;
import fr.m2i.cda.springinn.business.exception.UserAlreadyExistException;
import fr.m2i.cda.springinn.entity.Customer;
import fr.m2i.cda.springinn.entity.User;
import fr.m2i.cda.springinn.repository.UserRepository;
import fr.m2i.cda.springinn.service.MailService;

@Service
public class CustomerAccountBusinessImpl implements CustomerAccountBusiness{
    private UserRepository userRepo;
    private PasswordEncoder passwordEncoder;
    private MailService mailService;
    @Value("${mail.validation.secret}")
    private String validationSecret;

    public CustomerAccountBusinessImpl(UserRepository userRepo, PasswordEncoder passwordEncoder,
            MailService mailService) {
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
        this.mailService = mailService;
    }

    @Override
    public Customer register(Customer customer) {
        
        if(userRepo.findByEmail(customer.getEmail()).isPresent()) {
            throw new UserAlreadyExistException();
        } 
        String hash = passwordEncoder.encode(customer.getPassword());
        customer.setPassword(hash);
        customer.setRole("ROLE_CUSTOMER");
        customer.setActive(false);
        userRepo.save(customer);
        mailService.sendEmailValidation(customer);
        return customer;
    }

    @Override
    public void activateAccount(String id, String hash) {
        String decoded = new String(Base64.getUrlDecoder().decode(hash), StandardCharsets.UTF_8);
        if(!passwordEncoder.matches(id+validationSecret, decoded)) {
            throw new AccountValidationException();
        }
        User user = userRepo.findById(id).orElseThrow();
        user.setActive(true);
        userRepo.save(user);
    }

}
