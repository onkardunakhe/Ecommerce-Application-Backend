package com.Crud.Crud.Service;

import com.Crud.Crud.Dtos.UserDto;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@AllArgsConstructor
public class Authservice {
    private final Userservice userservice;
    private final PasswordEncoder passwordEncoder;

    public UserDto registerUser(UserDto userDto) {
        return userservice.Createuser(userDto);
    }

}
