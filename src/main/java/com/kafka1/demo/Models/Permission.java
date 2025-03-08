package com.kafka1.demo.Models;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Permission {
    UNVERIFIED("UNVERIFIED"),
    USER("USER"),
    ADMIN("ADMIN"),
    RE_SET_PASSWORD("RE_SET_PASSWORD"),
    DOCTOR("DOCTOR");
    private final String permission;
}