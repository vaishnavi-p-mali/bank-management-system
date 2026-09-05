package com.bank.management.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bank.management.entity.Customer;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

    Optional<Customer> findByEmail(String email);

    List<Customer> findByNameContainingIgnoreCaseOrEmailContainingIgnoreCase(
            String name, String email);

    Optional<Customer> findByResetToken(String resetToken);
}