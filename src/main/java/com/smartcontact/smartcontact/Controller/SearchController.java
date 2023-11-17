package com.smartcontact.smartcontact.Controller;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.smartcontact.smartcontact.Model.Contact;
import com.smartcontact.smartcontact.Model.User;
import com.smartcontact.smartcontact.Repository.contactrepo;
import com.smartcontact.smartcontact.Repository.userrepo;

@RestController
public class SearchController {
    @Autowired
    private contactrepo cr;
    @Autowired
    private userrepo ur;

    @GetMapping("/search/{query}")
    public ResponseEntity<?> search(@PathVariable("query") String query, Principal p, Model m) {
        String username = p.getName();
        User user = this.ur.getUserByEmail(username);
        List<Contact> contacts = this.cr.findByCNameContainingAndUser(query, user);
        return ResponseEntity.ok(contacts);
    }

}
