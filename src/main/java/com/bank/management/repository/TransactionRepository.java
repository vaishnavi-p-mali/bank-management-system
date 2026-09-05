package com.bank.management.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bank.management.entity.Transaction;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    List<Transaction> findByAccountNumberOrderByDateTimeDesc(String accountNumber);
}