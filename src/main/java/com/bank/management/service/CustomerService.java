package com.bank.management.service;

import com.bank.management.dto.CustomerRegisterRequest;
import com.bank.management.entity.Customer;

public interface CustomerService {

    Customer registerCustomer(CustomerRegisterRequest request);

    void updateCustomer(Long customerId, String name, String phone);

    String generateResetToken(String email);

    void resetPassword(String token, String newPassword);
}