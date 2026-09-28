package com.example.maritime.controller;

import com.example.maritime.model.Contact;
import com.example.maritime.repository.ContactRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/maritime")
@RequiredArgsConstructor
public class ContactRestController {

    private final ContactRepository contactRepository;

    @GetMapping("/contacts")
    public List<Contact> getAll() {
        return contactRepository.findAll();
    }

    @PostMapping("/contacts")
    public Contact create(@Valid @RequestBody Contact contact) {
        return contactRepository.save(contact);
    }
}
