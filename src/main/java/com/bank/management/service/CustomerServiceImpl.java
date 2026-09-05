package com.bank.management.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.bank.management.dto.CustomerRegisterRequest;
import com.bank.management.entity.BankAccount;
import com.bank.management.entity.Customer;
import com.bank.management.repository.BankAccountRepository;
import com.bank.management.repository.CustomerRepository;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;
    private final BankAccountRepository bankAccountRepository;
    private final PasswordEncoder passwordEncoder;

    public CustomerServiceImpl(
            CustomerRepository customerRepository,
            BankAccountRepository bankAccountRepository,
            PasswordEncoder passwordEncoder) {

        this.customerRepository = customerRepository;
        this.bankAccountRepository = bankAccountRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Customer registerCustomer(CustomerRegisterRequest request) {

        // Check for duplicate email before creating
        if (customerRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("An account with this email already exists");
        }

        Customer customer = new Customer();

        customer.setName(request.getName());
        customer.setEmail(request.getEmail());
        customer.setPhone(request.getPhone());
        customer.setProfilePhoto(request.getProfilePhoto());

        // Hash the password before saving
        customer.setPassword(
                passwordEncoder.encode(request.getPassword())
        );

        // Save customer first
        Customer savedCustomer =
                customerRepository.save(customer);

        // Create bank account automatically
        BankAccount account = new BankAccount();

        account.setAccountNumber(generateAccountNumber());

        // Default account type
        account.setAccountType("SAVINGS");

        // New account starts with zero balance
        account.setBalance(0.0);

        // Connect account with customer
        account.setCustomer(savedCustomer);

        // Save bank account
        bankAccountRepository.save(account);

        return savedCustomer;
    }

    @Override
    public void updateCustomer(
            Long customerId,
            String name,
            String phone) {

        Customer customer =
                customerRepository.findById(customerId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Customer not found"
                                ));

        customer.setName(name);
        customer.setPhone(phone);

        customerRepository.save(customer);
    }

    @Override
    public String generateResetToken(String email) {

        Customer customer =
                customerRepository.findByEmail(email)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "No account found with this email"
                                ));

        String token = UUID.randomUUID().toString();

        customer.setResetToken(token);

        // Token valid for 15 minutes
        customer.setResetTokenExpiry(
                LocalDateTime.now().plusMinutes(15)
        );

        customerRepository.save(customer);

        return token;
    }

    @Override
    public void resetPassword(String token, String newPassword) {

        Customer customer =
                customerRepository.findByResetToken(token)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Invalid or expired reset token"
                                ));

        if (customer.getResetTokenExpiry() == null ||
                customer.getResetTokenExpiry().isBefore(LocalDateTime.now())) {

            throw new RuntimeException(
                    "Reset token has expired. Please request a new one."
            );
        }

        customer.setPassword(
                passwordEncoder.encode(newPassword)
        );

        // Invalidate the token after use
        customer.setResetToken(null);
        customer.setResetTokenExpiry(null);

        customerRepository.save(customer);
    }

    // Generate 10-digit account number
    private String generateAccountNumber() {

        long number =
                1000000000L +
                (long)(Math.random() * 900000000L);

        return String.valueOf(number);
    }
}