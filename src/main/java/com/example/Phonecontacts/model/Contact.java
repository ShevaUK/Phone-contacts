package com.example.Phonecontacts.model;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "contacts")
public class Contact {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private User user;

    @Column(nullable = false)
    private String name;


    @OneToMany(mappedBy = "contact", cascade = CascadeType.ALL)
    private Set<Email> emails = new HashSet<>();;

    @OneToMany(mappedBy = "contact", cascade = CascadeType.ALL)
    private Set<PhoneNumber> phoneNumbers = new HashSet<>();;
    public Contact() {
    }

    public Contact(String id, User user, String name, Set<Email> emails, Set<PhoneNumber> phoneNumbers) {
        this.id = id;
        this.user = user;
        this.name = name;
        this.emails = emails;
        this.phoneNumbers = phoneNumbers;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<Email> getEmails() {
        return emails;
    }

    public void setEmails(Set<Email> emails) {
        this.emails = emails;
    }

    public Set<PhoneNumber> getPhoneNumbers() {
        return phoneNumbers;
    }

    public void setPhoneNumbers(Set<PhoneNumber> phoneNumbers) {
        this.phoneNumbers = phoneNumbers;
    }
}