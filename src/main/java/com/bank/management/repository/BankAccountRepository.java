package com.bank.management.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bank.management.entity.BankAccount;
import com.bank.management.entity.Customer;

public interface BankAccountRepository
        extends JpaRepository<BankAccount, Long> {

    Optional<BankAccount> findByCustomer(Customer customer);
}