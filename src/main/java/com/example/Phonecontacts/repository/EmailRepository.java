package com.example.Phonecontacts.repository;

import com.example.Phonecontacts.model.Contact;
import com.example.Phonecontacts.model.Email;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.util.Set;

@Repository
public interface EmailRepository extends JpaRepository<Email,String> {
        @Transactional
        void deleteAllByContact(Contact contact);
        Set<Email> findAllByContactId(String contact);
        }
