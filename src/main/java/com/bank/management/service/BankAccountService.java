package com.bank.management.service;

import java.util.List;

import com.bank.management.entity.BankAccount;
import com.bank.management.entity.Transaction;

public interface BankAccountService {

    BankAccount createAccount(Long customerId, String accountType);

    BankAccount deposit(Long customerId, double amount);

    BankAccount withdraw(Long customerId, double amount);

    List<Transaction> getTransactions(Long customerId);
}