package com.example.maritime.controller;

import com.example.maritime.model.Contact;
import com.example.maritime.repository.ContactRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class ContactController {

    private final ContactRepository contactRepository;

    @GetMapping("/contacts")
    public String contacts(Model model) {
        model.addAttribute("contacts", contactRepository.findAll());
        return "contacts";
    }

    @PostMapping("/contacts")
    public String createContact(@RequestParam String name, @RequestParam String contactType,
                                 @RequestParam(required = false) String email,
                                 @RequestParam(required = false) String phone,
                                 @RequestParam(required = false) String address) {
        Contact contact = Contact.builder()
                .name(name)
                .contactType(com.example.maritime.model.ContactType.valueOf(contactType))
                .email(email).phone(phone).address(address)
                .build();
        contactRepository.save(contact);
        return "redirect:/contacts";
    }
}
