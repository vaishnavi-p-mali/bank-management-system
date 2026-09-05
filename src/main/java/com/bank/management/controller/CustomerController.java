package com.bank.management.controller;

import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bank.management.dto.CustomerRegisterRequest;
import com.bank.management.dto.CustomerUpdateRequest;
import com.bank.management.entity.BankAccount;
import com.bank.management.entity.Customer;
import com.bank.management.repository.BankAccountRepository;
import com.bank.management.repository.CustomerRepository;
import com.bank.management.service.CustomerService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    private final CustomerService customerService;
    private final CustomerRepository customerRepository;
    private final BankAccountRepository bankAccountRepository;
    private final PasswordEncoder passwordEncoder;

    public CustomerController(
            CustomerService customerService,
            CustomerRepository customerRepository,
            BankAccountRepository bankAccountRepository,
            PasswordEncoder passwordEncoder) {

        this.customerService = customerService;
        this.customerRepository = customerRepository;
        this.bankAccountRepository = bankAccountRepository;
        this.passwordEncoder = passwordEncoder;
    }


    // ==========================================
    // Customer Registration
    // ==========================================

    @PostMapping("/register")
    public Customer registerCustomer(
            @Valid @RequestBody CustomerRegisterRequest request) {

        return customerService.registerCustomer(request);
    }


    // ==========================================
    // Customer Login
    // ==========================================

    @PostMapping("/login")
    public Customer loginCustomer(
            @RequestBody Customer loginCustomer,
            HttpSession session) {

        Optional<Customer> customer =
                customerRepository.findByEmail(
                        loginCustomer.getEmail()
                );

        if (customer.isPresent()) {

            Customer existingCustomer =
                    customer.get();

            if (passwordEncoder.matches(
                    loginCustomer.getPassword(),
                    existingCustomer.getPassword())) {

                session.setAttribute(
                        "customerId",
                        existingCustomer.getId()
                );

                session.setAttribute(
                        "customerName",
                        existingCustomer.getName()
                );

                session.setAttribute(
                        "customerEmail",
                        existingCustomer.getEmail()
                );

                return existingCustomer;
            }
        }

        throw new RuntimeException(
                "Invalid email or password"
        );
    }


    // ==========================================
    // Get Currently Logged-in Customer
    // ==========================================

    @GetMapping("/current")
    public Customer getCurrentCustomer(
            HttpSession session) {

        Object customerId =
                session.getAttribute("customerId");

        if (customerId == null) {

            throw new RuntimeException(
                    "Customer not logged in"
            );
        }

        Long id = (Long) customerId;

        Optional<Customer> customer =
                customerRepository.findById(id);

        if (customer.isPresent()) {

            return customer.get();
        }

        throw new RuntimeException(
                "Customer not found"
        );
    }


    // ==========================================
    // Get Logged-in Customer's Bank Account
    // ==========================================

    @GetMapping("/my-account")
    public BankAccount getMyAccount(
            HttpSession session) {

        Object customerId =
                session.getAttribute("customerId");

        if (customerId == null) {

            throw new RuntimeException(
                    "Customer not logged in"
            );
        }

        Long id = (Long) customerId;

        Optional<Customer> customer =
                customerRepository.findById(id);

        if (customer.isEmpty()) {

            throw new RuntimeException(
                    "Customer not found"
            );
        }

        Optional<BankAccount> account =
                bankAccountRepository.findByCustomer(
                        customer.get()
                );

        if (account.isPresent()) {

            return account.get();
        }

        throw new RuntimeException(
                "Bank account not found"
        );
    }


    // ==========================================
    // Update Customer Name and Phone
    // ==========================================

    @PutMapping("/update")
    public String updateCustomer(
            @Valid @RequestBody CustomerUpdateRequest request,
            HttpSession session) {
    	
        Object customerId =
                session.getAttribute("customerId");

        if (customerId == null) {

            throw new RuntimeException(
                    "Customer not logged in"
            );
        }

        Long id = (Long) customerId;

        customerService.updateCustomer(
                id,
                request.getName(),
                request.getPhone()
        );

        // Update session name also
        session.setAttribute(
                "customerName",
                request.getName()
        );

        return "Customer details updated successfully";
    }


    // ==========================================
    // Forgot Password - Generate Reset Token
    // ==========================================

    @PostMapping("/forgot-password")
    public java.util.Map<String, String> forgotPassword(
            @RequestBody java.util.Map<String, String> request) {

        String email = request.get("email");

        String token = customerService.generateResetToken(email);

        java.util.Map<String, String> response = new java.util.HashMap<>();

        response.put("resetToken", token);
        response.put("message", "Reset token generated. Use it to reset your password.");

        return response;
    }


    // ==========================================
    // Reset Password Using Token
    // ==========================================

    @PostMapping("/reset-password")
    public String resetPassword(
            @Valid @RequestBody com.bank.management.dto.ResetPasswordRequest request) {

        customerService.resetPassword(request.getToken(), request.getNewPassword());

        return "Password reset successful. You can now log in with your new password.";
    }

    // ==========================================
    // Customer Logout
    // ==========================================

    @PostMapping("/logout")
    public String logout(HttpSession session) {

        session.invalidate();

        return "Logout successful";
    }

}