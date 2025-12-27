package com.Crud.Crud.Service;

import com.Crud.Crud.Dtos.UserDto;
import com.Crud.Crud.Entity.Provider;
import com.Crud.Crud.Entity.Role;
import com.Crud.Crud.Entity.User;
import com.Crud.Crud.Exceptions.ResourceNotFound;
import com.Crud.Crud.Repository.Rolerepo;
import com.Crud.Crud.Repository.Userrepo;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class Userservice {
    private final Userrepo userrepo;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;

    private final Rolerepo rolerepo;

    @Transactional
    public UserDto Createuser(UserDto user) {
        if (user.getEmail() == null || user.getEmail().isBlank()) {
            throw new IllegalArgumentException("Email cannot be null or blank");
        }
        if (userrepo.existsByEmail(user.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }
        User user1 = modelMapper.map(user, User.class);
        user1.setPassword(passwordEncoder.encode(user.getPassword()));
        // TODO: set roles if needed
        user1.setProvider(user.getProvider() != null ? user.getProvider() : Provider.LOCAL);
        Role role = rolerepo.findByName("ROLE_USER")
                .orElseThrow(() -> new RuntimeException("ROLE_USER not found"));

        user1.getRoles().add(role);
        System.out.println("ROLES SIZE = " + user1.getRoles().size());

//        user1.setRoles(Set.of(role));

        User savedUser = userrepo.save(user1);
        return modelMapper.map(savedUser, UserDto.class);
    }

    public Iterable<UserDto> GetAllUser() {
        return userrepo.findAll()
                .stream()
                .map(user -> modelMapper.map(user, UserDto.class)).
                toList();
    }

    public UserDto findbyemail(String email) {
        User user = userrepo.findByEmail(email).orElseThrow(() -> new ResourceNotFound("User Not Found with email: " + email));
        return modelMapper.map(user, UserDto.class);
    }

    public UserDto findbyId(String userid) {
        UUID uid = UUID.fromString(userid);
        User fuser = userrepo.findById(uid).orElseThrow(() -> new ResourceNotFound("User with Userid" + userid + " Not found"));
        return modelMapper.map(fuser, UserDto.class);
    }

    public UserDto updateuser(String userid, UserDto updateduser) {
        UUID uid = UUID.fromString(userid);
        User existingUser = userrepo.findById(uid).orElseThrow(() -> new ResourceNotFound("User with id " + userid + " not found"));
        if (updateduser.getName() != null) {
            existingUser.setName(updateduser.getName());
        }
        if (updateduser.isEnable() != existingUser.isEnable()) {
            existingUser.setEnable(updateduser.isEnable());
        }
        if (updateduser.getProvider() != null) {
            existingUser.setProvider(updateduser.getProvider());
        }
        // Need to Update later spring Security
        if (updateduser.getPassword() != null) {
            existingUser.setPassword(passwordEncoder.encode(updateduser.getPassword()));
        }
        if (updateduser.getImage() != null) {
            existingUser.setImage(updateduser.getImage());

        }
        existingUser.setUpdatedAt(Instant.now());
        User savedUser = userrepo.save(existingUser);
        return modelMapper.map(savedUser, UserDto.class);
    }

    public void deleteuser(String userid) {
        UUID uid = UUID.fromString(userid);
        User existingUser = userrepo.findById(uid).orElseThrow(() -> new ResourceNotFound("User with id " + userid + " not found"));
        userrepo.delete(existingUser);
    }
}
