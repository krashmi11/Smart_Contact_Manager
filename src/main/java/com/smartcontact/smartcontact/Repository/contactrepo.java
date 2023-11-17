package com.smartcontact.smartcontact.Repository;

import java.util.List;

// import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.smartcontact.smartcontact.Model.Contact;
import com.smartcontact.smartcontact.Model.User;

public interface contactrepo extends JpaRepository<Contact, Integer> {

    @Query("from Contact as c where c.user.u_id=:userId")
    public Page<Contact> findContactByUser(@Param("userId") int userId, Pageable pageable);

    @Query("SELECT c FROM Contact c WHERE LOWER(c.c_name) LIKE LOWER(CONCAT('%', :query, '%')) AND c.user = :user")
    List<Contact> findByCNameContainingAndUser(@Param("query") String query, @Param("user") User user);
}
