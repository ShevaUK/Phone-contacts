package com.example.Phonecontacts.repository;

import com.example.Phonecontacts.model.Contact;
import com.example.Phonecontacts.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ContactRepository extends JpaRepository<Contact,String> {
    boolean existsByNameAndUser(String name,User user);
    List<Contact> findByUser(User user);
    void deleteById(String id);

    Optional<Contact> findById(String id);
}
