package com.bank.management.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {

    // ==============================
    // Home Page
    // ==============================

    @GetMapping("/")
    public String homePage() {
        return "index";
    }

    // ==============================
    // Customer Registration
    // ==============================

    @GetMapping("/register")
    public String registerPage() {
        return "register";
    }

    // ==============================
    // Customer Login
    // ==============================

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    // ==============================
    // Bank Account
    // ==============================

    @GetMapping("/account")
    public String accountPage() {
        return "account";
    }

    // ==============================
    // Deposit
    // ==============================

    @GetMapping("/deposit")
    public String depositPage() {
        return "deposit";
    }

    // ==============================
    // Withdraw
    // ==============================

    @GetMapping("/withdraw")
    public String withdrawPage() {
        return "withdraw";
    }

    // ==============================
    // Transactions
    // ==============================

    @GetMapping("/transactions")
    public String transactionsPage() {
        return "transactions";
    }

    // ==============================
    // Customer Dashboard
    // ==============================

    @GetMapping("/customer-dashboard")
    public String customerDashboardPage() {
        return "customer-dashboard";
    }
    @GetMapping("/profile")
    public String profilePage() {
        return "profile";
    }
    @GetMapping("/admin-login")
    public String adminLoginPage() {
        return "admin-login";
    }
    @GetMapping("/admin-dashboard")
    public String adminDashboardPage() {
        return "admin-dashboard";
    }
    @GetMapping("/forgot-password")
    public String forgotPasswordPage() {
        return "forgot-password";
    }

    @GetMapping("/reset-password")
    public String resetPasswordPage() {
        return "reset-password";
    }

}