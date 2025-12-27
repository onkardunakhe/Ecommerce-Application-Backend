package com.Crud.Crud.Security;

import com.Crud.Crud.Entity.User;
import com.Crud.Crud.Exceptions.ResourceNotFound;
import com.Crud.Crud.Repository.Userrepo;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomeUserDetailService implements UserDetailsService {
    private final Userrepo userrepo;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userrepo.findByEmail(email).orElseThrow(() -> new ResourceNotFound("Email is Not Validated"));
    }
}
