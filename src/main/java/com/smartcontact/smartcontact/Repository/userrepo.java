package com.smartcontact.smartcontact.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.smartcontact.smartcontact.Model.User;

public interface userrepo extends JpaRepository<User, Integer> {

    // @Query("select u from User u where u.email=:email")
    // public User getUserByUserName(@Param("email") String email);
    public User getUserByEmail(String email);

}
