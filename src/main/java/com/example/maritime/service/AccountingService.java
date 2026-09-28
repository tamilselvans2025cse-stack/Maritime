package com.example.maritime.service;

import com.example.maritime.exception.ResourceNotFoundException;
import com.example.maritime.model.*;
import com.example.maritime.repository.AccountRepository;
import com.example.maritime.repository.JournalEntryRepository;
import com.example.maritime.repository.JournalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Core double-entry bookkeeping service.
 *
 * Double-entry accounting means every financial transaction is recorded as
 * at least two "lines" - a debit on one account and a matching credit on
 * another - and the sum of all debits in a journal entry MUST equal the
 * sum of all credits. This keeps the accounting equation
 * (Assets = Liabilities + Equity) balanced at all times.
 */
@Service
@RequiredArgsConstructor
public class AccountingService {

    private final JournalEntryRepository journalEntryRepository;
    private final JournalRepository journalRepository;
    private final AccountRepository accountRepository;

    /** Throws if the entry's total debits do not equal its total credits. */
    public void validateBalancedEntry(JournalEntry entry) {
        BigDecimal totalDebit = entry.getLines().stream()
                .map(JournalEntryLine::getDebit)
                .filter(java.util.Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalCredit = entry.getLines().stream()
                .map(JournalEntryLine::getCredit)
                .filter(java.util.Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (totalDebit.compareTo(totalCredit) != 0) {
            throw new IllegalStateException(
                    "Unbalanced journal entry: total debit (" + totalDebit +
                            ") must equal total credit (" + totalCredit + ")");
        }
    }

    /**
     * Creates and persists a balanced journal entry with a single debit
     * account and a single credit account for the given amount. This is the
     * most common shape of entry used across the docking/purchase flows.
     */
    public JournalEntry postSimpleEntry(String journalCode, String description, String reference,
                                         Account debitAccount, Account creditAccount, BigDecimal amount) {
        Journal journal = journalRepository.findAll().stream()
                .filter(j -> j.getJournalCode().equals(journalCode))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Journal not found: " + journalCode));

        JournalEntry entry = JournalEntry.builder()
                .entryDate(LocalDate.now())
                .description(description)
                .journal(journal)
                .reference(reference)
                .build();

        List<JournalEntryLine> lines = new ArrayList<>();
        lines.add(JournalEntryLine.builder()
                .journalEntry(entry)
                .account(debitAccount)
                .debit(amount)
                .credit(BigDecimal.ZERO)
                .build());
        lines.add(JournalEntryLine.builder()
                .journalEntry(entry)
                .account(creditAccount)
                .debit(BigDecimal.ZERO)
                .credit(amount)
                .build());
        entry.setLines(lines);

        validateBalancedEntry(entry);

        // Update running account balances (simplified: debit increases balance,
        // credit decreases it - suitable for a teaching example, not a full GAAP model).
        debitAccount.setBalance(debitAccount.getBalance().add(amount));
        creditAccount.setBalance(creditAccount.getBalance().subtract(amount));
        accountRepository.save(debitAccount);
        accountRepository.save(creditAccount);

        return journalEntryRepository.save(entry);
    }

    public Account getAccountByCode(String code) {
        return accountRepository.findAll().stream()
                .filter(a -> a.getAccountCode().equals(code))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Account not found: " + code));
    }

    public List<JournalEntry> findAllEntries() {
        return journalEntryRepository.findAll();
    }
}
