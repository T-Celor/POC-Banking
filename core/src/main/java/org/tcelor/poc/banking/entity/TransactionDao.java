package org.tcelor.poc.banking.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import io.quarkus.hibernate.reactive.panache.PanacheEntity;
import io.smallrye.mutiny.Uni;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "Transaction")
public class TransactionDao extends PanacheEntity {
    
    public LocalDateTime timestamp;
    @Column(name = "transaction_from")
    public String from;
    @Column(name = "transaction_to")
    public String to;
    public BigDecimal amount;
    public String description;
    public TransactionState state;

    public static Uni<TransactionDao> add(String from, String to, BigDecimal amount, String description) {
        TransactionDao transaction = new TransactionDao();
        transaction.from = from;
        transaction.to = to;
        transaction.amount = amount;
        transaction.description = description;
        transaction.timestamp = LocalDateTime.now();
        transaction.state = TransactionState.PENDING;
        return transaction.persistAndFlush();
    }
}