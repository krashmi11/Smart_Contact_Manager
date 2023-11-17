package com.smartcontact.smartcontact.Configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import com.smartcontact.smartcontact.Model.User;
import com.smartcontact.smartcontact.Repository.userrepo;

@Component
public class UserDetailsServiceimpl implements UserDetailsService {

    @Autowired
    private userrepo ur;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // fetching user from database;
        User user = ur.getUserByEmail(email);
        if (user == null) {
            throw new UsernameNotFoundException("Could not found User");
        }
        CustomUserDetails customUserDetails = new CustomUserDetails(user);
        return customUserDetails;
    }

}
