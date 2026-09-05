package com.bank.management.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bank.management.entity.BankAccount;
import com.bank.management.entity.Transaction;
import com.bank.management.service.BankAccountService;

import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/api/bank")
public class BankAccountController {

    private final BankAccountService bankAccountService;

    public BankAccountController(
            BankAccountService bankAccountService) {

        this.bankAccountService = bankAccountService;
    }

    // ==========================================
    // CREATE BANK ACCOUNT
    // ==========================================

    @PostMapping("/create")
    public BankAccount createAccount(
            @RequestParam String accountType,
            HttpSession session) {

        Object customerId =
                session.getAttribute("customerId");

        if (customerId == null) {
            throw new RuntimeException(
                    "Customer not logged in");
        }

        Long id = (Long) customerId;

        return bankAccountService.createAccount(
                id,
                accountType
        );
    }

    // ==========================================
    // DEPOSIT MONEY
    // ==========================================

    @PostMapping("/deposit")
    public BankAccount deposit(
            @RequestParam double amount,
            HttpSession session) {

        Object customerId =
                session.getAttribute("customerId");

        if (customerId == null) {
            throw new RuntimeException(
                    "Customer not logged in");
        }

        Long id = (Long) customerId;

        return bankAccountService.deposit(
                id,
                amount
        );
    }

    // ==========================================
    // WITHDRAW MONEY
    // ==========================================

    @PostMapping("/withdraw")
    public BankAccount withdraw(
            @RequestParam double amount,
            HttpSession session) {

        Object customerId =
                session.getAttribute("customerId");

        if (customerId == null) {
            throw new RuntimeException(
                    "Customer not logged in");
        }

        Long id = (Long) customerId;

        return bankAccountService.withdraw(
                id,
                amount
        );
    }

    // ==========================================
    // TRANSACTION HISTORY
    // ==========================================

    @GetMapping("/transactions")
    public List<Transaction> getTransactions(
            HttpSession session) {

        Object customerId =
                session.getAttribute("customerId");

        if (customerId == null) {
            throw new RuntimeException(
                    "Customer not logged in");
        }

        Long id = (Long) customerId;

        return bankAccountService.getTransactions(
                id
        );
    }
}