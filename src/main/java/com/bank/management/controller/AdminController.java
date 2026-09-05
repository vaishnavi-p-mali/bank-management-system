package com.bank.management.controller;

import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bank.management.entity.Admin;
import com.bank.management.entity.BankAccount;
import com.bank.management.entity.Customer;
import com.bank.management.entity.Transaction;
import com.bank.management.service.AdminService;
import com.bank.management.repository.BankAccountRepository;
import com.bank.management.repository.CustomerRepository;
import com.bank.management.repository.TransactionRepository;

import jakarta.servlet.http.HttpSession;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final AdminService adminService;
    private final CustomerRepository customerRepository;
    private final BankAccountRepository bankAccountRepository;
    private final TransactionRepository transactionRepository;
    private final PasswordEncoder passwordEncoder;


    // ==========================================
    // Constructor
    // ==========================================

    public AdminController(
            AdminService adminService,
            CustomerRepository customerRepository,
            BankAccountRepository bankAccountRepository,
            TransactionRepository transactionRepository,
            PasswordEncoder passwordEncoder) {

        this.adminService = adminService;
        this.customerRepository = customerRepository;
        this.bankAccountRepository = bankAccountRepository;
        this.transactionRepository = transactionRepository;
        this.passwordEncoder = passwordEncoder;
    }


    // ==========================================
    // Admin Login
    // ==========================================

    @PostMapping("/login")
    public Admin login(
            @RequestBody Admin admin,
            HttpSession session) {

        Admin loggedInAdmin =
                adminService.login(
                        admin.getEmail(),
                        admin.getPassword()
                );

        session.setAttribute(
                "adminId",
                loggedInAdmin.getId()
        );

        session.setAttribute(
                "adminEmail",
                loggedInAdmin.getEmail()
        );

        return loggedInAdmin;
    }


    // ==========================================
    // Admin Logout
    // ==========================================

    @PostMapping("/logout")
    public String logout(HttpSession session) {

        session.invalidate();

        return "Admin logout successful";
    }


    // ==========================================
    // Get All Customers
    // ==========================================

    @GetMapping("/customers")
    public List<Customer> getAllCustomers(
            HttpSession session) {

        checkAdminLogin(session);

        return customerRepository.findAll();
    }


    // ==========================================
    // Search Customers (Name, Email, or ID)
    // ==========================================

    @GetMapping("/customers/search")
    public List<Customer> searchCustomers(
            @RequestParam String query,
            HttpSession session) {

        checkAdminLogin(session);

        try {
            Long id = Long.parseLong(query.trim());

            Optional<Customer> byId = customerRepository.findById(id);

            if (byId.isPresent()) {
                return List.of(byId.get());
            }

        } catch (NumberFormatException ignored) {
        }

        return customerRepository
                .findByNameContainingIgnoreCaseOrEmailContainingIgnoreCase(
                        query, query);
    }


    // ==========================================
    // Get Customer Details (Customer + Account + Transactions)
    // ==========================================

    @GetMapping("/customers/{id}/details")
    public Map<String, Object> getCustomerDetails(
            @PathVariable Long id,
            HttpSession session) {

        checkAdminLogin(session);

        Customer customer = customerRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Customer not found"));

        Optional<BankAccount> accountOpt =
                bankAccountRepository.findByCustomer(customer);

        Map<String, Object> details = new HashMap<>();

        details.put("customer", customer);

        if (accountOpt.isPresent()) {

            BankAccount account = accountOpt.get();

            details.put("account", account);

            List<Transaction> transactions =
                    transactionRepository
                            .findByAccountNumberOrderByDateTimeDesc(
                                    account.getAccountNumber());

            details.put("transactions", transactions);

        } else {

            details.put("account", null);
            details.put("transactions", List.of());

        }

        return details;
    }


    // ==========================================
    // Get All Bank Accounts
    // ==========================================

    @GetMapping("/accounts")
    public List<BankAccount> getAllAccounts(
            HttpSession session) {

        checkAdminLogin(session);

        return bankAccountRepository.findAll();
    }


    // ==========================================
    // Get Account Details (Account + Customer + Transactions)
    // ==========================================

    @GetMapping("/accounts/{id}/details")
    public Map<String, Object> getAccountDetails(
            @PathVariable Long id,
            HttpSession session) {

        checkAdminLogin(session);

        BankAccount account = bankAccountRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Account not found"));

        List<Transaction> transactions =
                transactionRepository
                        .findByAccountNumberOrderByDateTimeDesc(
                                account.getAccountNumber());

        Map<String, Object> details = new HashMap<>();

        details.put("account", account);
        details.put("customer", account.getCustomer());
        details.put("transactions", transactions);

        return details;
    }


    // ==========================================
    // Get All Transactions
    // ==========================================

    @GetMapping("/transactions")
    public List<Transaction> getAllTransactions(
            HttpSession session) {

        checkAdminLogin(session);

        return transactionRepository.findAll();
    }


    // ==========================================
    // Get Dashboard Statistics
    // ==========================================

    @GetMapping("/stats")
    public Map<String, Object> getStats(
            HttpSession session) {

        checkAdminLogin(session);

        long totalCustomers = customerRepository.count();
        long totalAccounts = bankAccountRepository.count();
        long totalTransactions = transactionRepository.count();

        double totalBalance = bankAccountRepository
                .findAll()
                .stream()
                .mapToDouble(BankAccount::getBalance)
                .sum();

        Map<String, Object> stats = new HashMap<>();

        stats.put("totalCustomers", totalCustomers);
        stats.put("totalAccounts", totalAccounts);
        stats.put("totalTransactions", totalTransactions);
        stats.put("totalBalance", totalBalance);

        return stats;
    }


    // ==========================================
    // Check Admin Login
    // ==========================================

    private void checkAdminLogin(
            HttpSession session) {

        Object adminId =
                session.getAttribute("adminId");

        if (adminId == null) {

            throw new RuntimeException(
                    "Admin is not logged in"
            );
        }
    }
    // ==========================================
    // Get Currently Logged-in Admin
    // ==========================================

    @GetMapping("/current")
    public Map<String, Object> getCurrentAdmin(
            HttpSession session) {

        checkAdminLogin(session);

        Object adminEmail = session.getAttribute("adminEmail");
        Object adminId = session.getAttribute("adminId");

        Map<String, Object> response = new HashMap<>();
        response.put("id", adminId);
        response.put("email", adminEmail);

        return response;
    }

}