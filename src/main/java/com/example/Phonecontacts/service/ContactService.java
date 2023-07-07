package com.example.Phonecontacts.service;

import com.example.Phonecontacts.model.Contact;
import com.example.Phonecontacts.service.security.UserDetailsCustom;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface ContactService {
    ResponseEntity<?> createContact(Contact contact);
    ResponseEntity<?> editContact(String contactId, Contact updatedContact);
    public List<Contact> getAllContactsForCurrentUser();
    public int deleteContactById(String contactId);


}
