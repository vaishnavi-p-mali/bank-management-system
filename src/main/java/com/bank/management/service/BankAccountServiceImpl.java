package com.bank.management.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

import org.springframework.stereotype.Service;

import com.bank.management.entity.BankAccount;
import com.bank.management.entity.Customer;
import com.bank.management.entity.Transaction;
import com.bank.management.repository.BankAccountRepository;
import com.bank.management.repository.CustomerRepository;
import com.bank.management.repository.TransactionRepository;

@Service
public class BankAccountServiceImpl implements BankAccountService {

    private final BankAccountRepository bankAccountRepository;
    private final CustomerRepository customerRepository;
    private final TransactionRepository transactionRepository;

    public BankAccountServiceImpl(
            BankAccountRepository bankAccountRepository,
            CustomerRepository customerRepository,
            TransactionRepository transactionRepository) {

        this.bankAccountRepository = bankAccountRepository;
        this.customerRepository = customerRepository;
        this.transactionRepository = transactionRepository;
    }

    @Override
    public BankAccount createAccount(Long customerId, String accountType) {

        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() ->
                        new RuntimeException("Customer not found"));

        String accountNumber = generateAccountNumber();

        BankAccount account = new BankAccount();

        account.setAccountNumber(accountNumber);
        account.setAccountType(accountType);
        account.setBalance(0.0);
        account.setCustomer(customer);

        return bankAccountRepository.save(account);
    }

    @Override
    public BankAccount deposit(Long customerId, double amount) {

        if (amount <= 0) {
            throw new RuntimeException(
                    "Amount must be greater than zero");
        }

        // Find logged-in customer
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() ->
                        new RuntimeException("Customer not found"));

        // Find account belonging to THIS customer
        BankAccount account =
                bankAccountRepository.findByCustomer(customer)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Bank account not found"));

        // Update actual account balance
        account.setBalance(
                account.getBalance() + amount
        );

        // Save updated account
        BankAccount savedAccount =
                bankAccountRepository.save(account);

        // Create transaction
        Transaction transaction = new Transaction();

        transaction.setAccountNumber(
                savedAccount.getAccountNumber()
        );

        transaction.setType("DEPOSIT");

        transaction.setAmount(amount);

        transaction.setBalanceAfterTransaction(
                savedAccount.getBalance()
        );

        transaction.setDateTime(
                LocalDateTime.now()
        );

        transactionRepository.save(transaction);

        return savedAccount;
    }

    @Override
    public BankAccount withdraw(Long customerId, double amount) {

        if (amount <= 0) {
            throw new RuntimeException(
                    "Amount must be greater than zero");
        }

        // Find logged-in customer
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() ->
                        new RuntimeException("Customer not found"));

        // Find account belonging to THIS customer
        BankAccount account =
                bankAccountRepository.findByCustomer(customer)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Bank account not found"));

        // Check balance
        if (account.getBalance() < amount) {

            throw new RuntimeException(
                    "Insufficient balance");
        }

        // Update actual account balance
        account.setBalance(
                account.getBalance() - amount
        );

        // Save updated account
        BankAccount savedAccount =
                bankAccountRepository.save(account);

        // Create transaction
        Transaction transaction = new Transaction();

        transaction.setAccountNumber(
                savedAccount.getAccountNumber()
        );

        transaction.setType("WITHDRAW");

        transaction.setAmount(amount);

        transaction.setBalanceAfterTransaction(
                savedAccount.getBalance()
        );

        transaction.setDateTime(
                LocalDateTime.now()
        );

        transactionRepository.save(transaction);

        return savedAccount;
    }

    @Override
    public List<Transaction> getTransactions(Long customerId) {

        // Find logged-in customer
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() ->
                        new RuntimeException("Customer not found"));

        // Find account belonging to THIS customer
        BankAccount account =
                bankAccountRepository.findByCustomer(customer)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Bank account not found"));

        // Return only this customer's transactions
        return transactionRepository
                .findByAccountNumberOrderByDateTimeDesc(
                        account.getAccountNumber()
                );
    }

    private String generateAccountNumber() {

        Random random = new Random();

        long number =
                1000000000L +
                random.nextInt(900000000);

        return String.valueOf(number);
    }
}