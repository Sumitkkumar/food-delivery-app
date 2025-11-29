package com.example.frontend.common.utils;

import org.springframework.security.core.context.SecurityContextHolder;

public class Helper {
    public static String currentUser() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }
}
