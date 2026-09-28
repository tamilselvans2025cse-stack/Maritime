package com.example.maritime.controller;

import com.example.maritime.model.Account;
import com.example.maritime.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class AccountController {

    private final AccountRepository accountRepository;

    @GetMapping("/accounts")
    public String accounts(Model model) {
        model.addAttribute("accounts", accountRepository.findAll());
        return "accounts";
    }

    @PostMapping("/accounts")
    public String createAccount(@RequestParam String accountCode, @RequestParam String accountName,
                                 @RequestParam String accountType) {
        Account account = Account.builder()
                .accountCode(accountCode).accountName(accountName)
                .accountType(com.example.maritime.model.AccountType.valueOf(accountType))
                .balance(java.math.BigDecimal.ZERO)
                .build();
        accountRepository.save(account);
        return "redirect:/accounts";
    }
}
