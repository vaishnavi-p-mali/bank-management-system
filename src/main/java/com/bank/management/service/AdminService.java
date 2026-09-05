package com.bank.management.service;

import com.bank.management.entity.Admin;

public interface AdminService {

    Admin login(String email, String password);
}