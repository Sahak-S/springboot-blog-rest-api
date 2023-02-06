package com.example.springbootblogrestapi.controller;

import com.example.springbootblogrestapi.dto.LoginDto;
import com.example.springbootblogrestapi.dto.SignUpDto;
import com.example.springbootblogrestapi.model.Role;
import com.example.springbootblogrestapi.model.User;
import com.example.springbootblogrestapi.repozitory.RoleRepository;
import com.example.springbootblogrestapi.repozitory.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/signin")
    public ResponseEntity<String> authenticateUser(@RequestBody LoginDto loginDto){
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                loginDto.getUsernameOrEmail(), loginDto.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        return new ResponseEntity<>("Օգտագործողը հաջողությամբ մուտք է գործել!.", HttpStatus.OK);
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@RequestBody SignUpDto signUpDto){

        // ավելացնել ստուգել,որ օգտագործողի անունը գոյություն ունի ՏԲ(DB)-ում
        if(userRepository.existsByUsername(signUpDto.getUsername())){
            return new ResponseEntity<>("Օգտվող ավելացվացշծ է", HttpStatus.BAD_REQUEST);
        }

        // ավելացնել ստուգում, որ էլփոստը գոյություն ունի ՏԲ(DB)-ում
        if(userRepository.existsByEmail(signUpDto.getEmail())){
            return new ResponseEntity<>("Էլ.փոստն արդեն ընդունված է:", HttpStatus.BAD_REQUEST);
        }

        // ստեղծել օգտվողի օբյեկտ
        User user = new User();
        user.setName(signUpDto.getName());
        user.setUsername(signUpDto.getUsername());
        user.setEmail(signUpDto.getEmail());
        user.setPassword(passwordEncoder.encode(signUpDto.getPassword()));

        Role roles = roleRepository.findByName("ROLE_ADMIN").get();
        user.setRoles(Collections.singleton(roles));

        userRepository.save(user);

        return new ResponseEntity<>("Օգտատերը հաջողությամբ գրանցվեց", HttpStatus.OK);

    }
}
