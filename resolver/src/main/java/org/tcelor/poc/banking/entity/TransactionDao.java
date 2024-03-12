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

    public static Uni<Integer> validate(Long transactionId) {
        return TransactionDao.update("state =  ?1 WHERE id = ?2", TransactionState.ACCEPTED,
                transactionId);
    }

    public static Uni<Integer> reject(Long transactionId) {
        return TransactionDao.update("state =  ?1 WHERE id = ?2", TransactionState.REJECTED,
                transactionId);
    }

    @Override
    public String toString() {
        return "TransactionDao [timestamp=" + timestamp + ", from=" + from + ", to=" + to + ", amount=" + amount
                + ", description=" + description + ", state=" + state + "]";
    }
    
}
