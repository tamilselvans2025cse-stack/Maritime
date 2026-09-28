package com.example.maritime.controller;

import com.example.maritime.model.Account;
import com.example.maritime.repository.AccountRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/maritime")
@RequiredArgsConstructor
public class AccountRestController {

    private final AccountRepository accountRepository;

    @GetMapping("/accounts")
    public List<Account> getAll() {
        return accountRepository.findAll();
    }

    @PostMapping("/accounts")
    public Account create(@Valid @RequestBody Account account) {
        return accountRepository.save(account);
    }
}
