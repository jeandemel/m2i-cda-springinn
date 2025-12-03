package fr.m2i.cda.springinn.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import fr.m2i.cda.springinn.repository.UserRepository;

@Service
public class UserService implements UserDetailsService{
    private UserRepository repo;

    
    public UserService(UserRepository repo) {
        this.repo = repo;
    }
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return repo.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("User does not exist"));
    }

}
