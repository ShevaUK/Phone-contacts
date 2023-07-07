package com.example.Phonecontacts.repository;

import com.example.Phonecontacts.model.Contact;
import com.example.Phonecontacts.model.PhoneNumber;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;


import java.util.Set;

@Repository
public interface PhoneNumberRepository extends JpaRepository<PhoneNumber,String> {
        @Transactional
        void deleteAllByContact(Contact contact);
        Set<PhoneNumber> findAllByContactId(String contact);
}
