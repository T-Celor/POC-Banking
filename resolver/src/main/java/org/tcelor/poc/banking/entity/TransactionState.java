package org.tcelor.poc.banking.entity;

import java.util.Objects;

public enum TransactionState {
    PENDING("PENDING"),
    REJECTED("REJECTED"),
    ACCEPTED("ACCEPTED");

    private final String stringValue;

    TransactionState(String stringValue) {
        Objects.requireNonNull(stringValue);
        this.stringValue = stringValue;
    }

    public String stringValue() {
        return stringValue;
    }
}
