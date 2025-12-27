package com.Crud.Crud.Controllers;

import com.Crud.Crud.Dtos.UserDto;
import com.Crud.Crud.Service.Userservice;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("api/users")
@AllArgsConstructor
public class Usercontrollers {
    private final Userservice userservice;

    @PostMapping
    public ResponseEntity<UserDto> CreateUser(@RequestBody UserDto user) {

        return ResponseEntity.status(HttpStatus.CREATED).body(userservice.Createuser(user));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<Iterable<UserDto>> GetAllUsers() {
        // Implementation for getting all users
        return ResponseEntity.ok().body(userservice.GetAllUser());
    }

    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @GetMapping("/getbyemail/{email}")
    public ResponseEntity<UserDto> GetUserByEmail(@PathVariable String email) {
        return ResponseEntity.ok(userservice.findbyemail(email));
    }

    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @GetMapping("/getbyid/{userid}")
    public ResponseEntity<UserDto> GetUserbyId(@PathVariable String userid) {
        return ResponseEntity.ok().body(userservice.findbyId(userid));

    }

    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @PutMapping("/{userid}")
    public ResponseEntity<UserDto> UpdateUser(@PathVariable String userid, @RequestBody UserDto updateduser) {
        return ResponseEntity.ok(userservice.updateuser(userid, updateduser));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{userid}")
    public ResponseEntity<Void> DeleteUser(@PathVariable String userid) {
        userservice.deleteuser(userid);
        return ResponseEntity.noContent().build();
    }

}
