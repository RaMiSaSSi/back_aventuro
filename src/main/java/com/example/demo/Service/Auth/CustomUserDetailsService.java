package com.example.demo.Service.Auth;
import com.example.demo.Model.UtilisateurInscrit;

import com.example.demo.Repository.UtilisateurInscritRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UtilisateurInscritRepository utilisateurRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        UtilisateurInscrit user = utilisateurRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getMotDePasse(),
                List.of(new SimpleGrantedAuthority(user.getRole().name())) // Remove ROLE_ prefix here
        );
    }
}