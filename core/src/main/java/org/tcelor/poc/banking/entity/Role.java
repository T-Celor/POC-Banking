package org.tcelor.poc.banking.entity;

import java.util.Objects;

public enum Role {
    ADMIN("ADMIN"),
    USER("USER");

    private final String stringValue;

    Role(String stringValue) {
        Objects.requireNonNull(stringValue);
        this.stringValue = stringValue;
    }

    public String stringValue() {
        return stringValue;
    }
}
