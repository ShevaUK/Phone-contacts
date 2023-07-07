package com.example.Phonecontacts.service.impl;

import com.example.Phonecontacts.exception.ResourceNotFoundException;
import com.example.Phonecontacts.jwt.JwtService;
import com.example.Phonecontacts.model.Contact;
import com.example.Phonecontacts.model.Email;
import com.example.Phonecontacts.model.PhoneNumber;
import com.example.Phonecontacts.model.User;
import com.example.Phonecontacts.repository.ContactRepository;
import com.example.Phonecontacts.repository.EmailRepository;
import com.example.Phonecontacts.repository.PhoneNumberRepository;
import com.example.Phonecontacts.repository.UserRepository;
import com.example.Phonecontacts.service.ContactService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.*;


@Service
public class ContactServiceImpl implements ContactService {
    @Autowired
    private ContactRepository contactRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private EmailRepository emailRepository;
    @Autowired
    private PhoneNumberRepository phoneNumberRepository;

    public ContactServiceImpl(ContactRepository contactRepository, UserRepository userRepository, EmailRepository emailRepository, PhoneNumberRepository phoneNumberRepository, JwtService jwtService) {
        this.contactRepository = contactRepository;
        this.userRepository = userRepository;
        this.emailRepository = emailRepository;
        this.phoneNumberRepository = phoneNumberRepository;
    }



    @Override
    public ResponseEntity<?> createContact(Contact contact) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new ResourceNotFoundException("Invalid token");
        }
        String username = authentication.getName();
        User currentUser = userRepository.findByUsername(username);
        if (currentUser == null) {
            throw new ResourceNotFoundException("User not found");
        }

        try {
            if (contactRepository.existsByNameAndUser(contact.getName(),currentUser)) {
                return ResponseEntity.badRequest().body("Contact with the same name already exists");
            }
            contact.setUser(currentUser);

            for (Email email : contact.getEmails()) {
                if (!isValidEmail(email.getEmail())) {
                    return ResponseEntity.badRequest().body("Invalid email format: " + email.getEmail());
                }
                email.setContact(contact);
            }

            for (PhoneNumber phoneNumber : contact.getPhoneNumbers()) {
                if (!isValidPhoneNumber(phoneNumber.getPhoneNumber())) {
                    return ResponseEntity.badRequest().body("Invalid phone number format: " + phoneNumber.getPhoneNumber());
                }
                phoneNumber.setContact(contact);
            }

            contactRepository.save(contact);


            return new ResponseEntity<>(contact,HttpStatus.CREATED);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error saving contact: " + e.getMessage());
        }

    }


    @Override
    public ResponseEntity<String> editContact(String contactId, Contact updatedContact) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new ResourceNotFoundException("Invalid token");
        }
        String username = authentication.getName();
        User currentUser = userRepository.findByUsername(username);
        if (currentUser == null) {
            throw new ResourceNotFoundException("User not found");
        }

        try {
            Optional<Contact> existingContactOptional = contactRepository.findById(contactId);
            if (existingContactOptional.isEmpty()) {
                return ResponseEntity.badRequest().body("Contact not found");
            }

            Contact existingContact = existingContactOptional.get();


            if (!existingContact.getUser().equals(currentUser)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("You are not authorized to edit this contact");
            }

            if (!existingContact.getName().equals(updatedContact.getName())) {
                if (contactRepository.existsByNameAndUser(updatedContact.getName(), currentUser)) {
                    return ResponseEntity.badRequest().body("Contact with the same name already exists");
                }
            }
            existingContact.setName(updatedContact.getName());


            Set<Email> currentEmails = emailRepository.findAllByContactId(contactId);

            Iterator<Email> currentEmailsIterator = currentEmails.iterator();
            Iterator<Email> updatedEmailsIterator = updatedContact.getEmails().iterator();


            while (currentEmailsIterator.hasNext() && updatedEmailsIterator.hasNext()) {
                Email currentEmail = currentEmailsIterator.next();
                Email updatedEmail = updatedEmailsIterator.next();

                if (!isValidEmail(updatedEmail.getEmail())) {
                    return ResponseEntity.badRequest().body("Invalid email format: " + updatedEmail.getEmail());
                }
                currentEmail.setEmail(updatedEmail.getEmail());
                emailRepository.save(currentEmail);
            }

            Set<PhoneNumber> currentPhoneNumbers = phoneNumberRepository.findAllByContactId(contactId);
            System.out.println(currentPhoneNumbers);

            Iterator<PhoneNumber> currentPhoneNumbersIterator = currentPhoneNumbers.iterator();
            Iterator<PhoneNumber> updatedPhoneNumbersIterator = updatedContact.getPhoneNumbers().iterator();


            while (currentPhoneNumbersIterator.hasNext() && updatedPhoneNumbersIterator.hasNext()) {
                PhoneNumber currentPhoneNumber = currentPhoneNumbersIterator.next();
                PhoneNumber updatedPhoneNumber = updatedPhoneNumbersIterator.next();

                if (!isValidPhoneNumber(updatedPhoneNumber.getPhoneNumber())) {
                    return ResponseEntity.badRequest().body("Invalid phone number format: " + updatedPhoneNumber.getPhoneNumber());
                }

                currentPhoneNumber.setPhoneNumber(updatedPhoneNumber.getPhoneNumber());
                phoneNumberRepository.save(currentPhoneNumber);
            }

            contactRepository.save(existingContact);

            return ResponseEntity.ok("Contact updated successfully");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error updating contact: " + e.getMessage());
        }
    }


    @Override
    public List<Contact> getAllContactsForCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new ResourceNotFoundException("Invalid token");
        }
        String username = authentication.getName();
        User currentUser = userRepository.findByUsername(username);
        if (currentUser == null) {
            throw new ResourceNotFoundException("User not found");
        }

        return contactRepository.findByUser(currentUser);

    }

    @Override
    public int deleteContactById(String contactId) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return HttpStatus.UNAUTHORIZED.value();
        }

        // Знайти контакт за його ідентифікатором
        Optional<Contact> contactOptional = contactRepository.findById(contactId);
        if (!contactOptional.isPresent()) {
            return HttpStatus.NOT_FOUND.value();
        }

        Contact contact = contactOptional.get();

        if (!contact.getUser().equals(authentication.getPrincipal())) {
            return HttpStatus.FORBIDDEN.value();
        }

        contactRepository.deleteById(contactId);

        return HttpStatus.OK.value();
    }
//    public int deleteContactById(String contactId) {
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        if (authentication == null || !authentication.isAuthenticated()) {
//            return HttpStatus.UNAUTHORIZED.value(); // Повернути код помилки 401 (UNAUTHORIZED)
//        }
//        String username = authentication.getName();
//        User currentUser = userRepository.findByUsername(username);
//        if (currentUser == null) {
//            throw new ResourceNotFoundException("User not found");
//        }
//
//        Contact contact = contactRepository.findById(contactId)
//                .orElseThrow(() -> new ResourceNotFoundException("Contact not found"));
//
//        if (!contact.getUser().equals(currentUser)) {
//            throw new ResourceNotFoundException("You are not authorized to delete this contact");
//        }
//
//        contactRepository.deleteById(contactId);
//    }


    private boolean isValidEmail(String email) {
        String regex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.com$";
        return email.matches(regex);
    }

    private boolean isValidPhoneNumber(String phoneNumber) {
        String regex = "^\\+\\d{1,3}\\d{9}$";
        return phoneNumber.matches(regex);
    }
}
