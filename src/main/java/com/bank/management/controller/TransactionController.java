package com.bank.management.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bank.management.entity.BankAccount;
import com.bank.management.entity.Transaction;
import com.bank.management.repository.BankAccountRepository;
import com.bank.management.repository.TransactionRepository;
import com.bank.management.repository.CustomerRepository;

import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private final TransactionRepository transactionRepository;
    private final BankAccountRepository bankAccountRepository;
    private final CustomerRepository customerRepository;

    public TransactionController(
            TransactionRepository transactionRepository,
            BankAccountRepository bankAccountRepository,
            CustomerRepository customerRepository) {

        this.transactionRepository = transactionRepository;
        this.bankAccountRepository = bankAccountRepository;
        this.customerRepository = customerRepository;
    }

    // Get transactions of logged-in customer only
    @GetMapping
    public List<Transaction> getTransactions(HttpSession session) {

        Object customerId =
                session.getAttribute("customerId");

        // Check whether customer is logged in
        if (customerId == null) {
            throw new RuntimeException(
                    "Customer is not logged in"
            );
        }

        Long id = (Long) customerId;

        // Find logged-in customer
        Optional<com.bank.management.entity.Customer> customer =
                customerRepository.findById(id);

        if (customer.isEmpty()) {
            throw new RuntimeException(
                    "Customer not found"
            );
        }

        // Find account belonging to logged-in customer
        Optional<BankAccount> account =
                bankAccountRepository.findByCustomer(
                        customer.get()
                );

        if (account.isEmpty()) {
            throw new RuntimeException(
                    "Bank account not found"
            );
        }

        // Get account number automatically
        String accountNumber =
                account.get().getAccountNumber();

        // Return only this customer's transactions
        return transactionRepository
                .findByAccountNumberOrderByDateTimeDesc(
                        accountNumber
                );
    }
}