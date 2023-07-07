package com.example.Phonecontacts.controller;

import com.example.Phonecontacts.exception.ResourceNotFoundException;
import com.example.Phonecontacts.model.Contact;
import com.example.Phonecontacts.repository.ContactRepository;
import com.example.Phonecontacts.repository.UserRepository;
import com.example.Phonecontacts.service.impl.ContactServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/contacts")
public class ContactController {
    @Autowired
    private ContactServiceImpl contactService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ContactRepository contactRepository;



    @PostMapping
    public ResponseEntity<?> createContact(@RequestBody Contact contact) {
        try {
             contactService.createContact(contact);
             return new ResponseEntity<>(contact,HttpStatus.CREATED);
        } catch (Exception e) {
            // Обробка помилки тут
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error creating contact");
        }
    }
    @PutMapping("/{contactId}")
    public ResponseEntity<?> editContact(@PathVariable String contactId, @RequestBody Contact updatedContact) {
        try {
            contactService.editContact(contactId, updatedContact);
            return new ResponseEntity<>(updatedContact,HttpStatus.CREATED);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error editing contact");
        }
    }

    @GetMapping
    public ResponseEntity<?> getAllContacts() {
        try {
            List<Contact> contacts = contactService.getAllContactsForCurrentUser();
            return ResponseEntity.ok(contacts);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No contacts found");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error retrieving contacts: " + e.getMessage());
        }
    }
    @DeleteMapping("/{contactId}")
    public ResponseEntity<String> deleteContactById(@PathVariable String contactId) {
        contactService.deleteContactById(contactId);
        return ResponseEntity.ok("Contact deleted successfully");
    }

}
