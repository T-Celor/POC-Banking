package org.tcelor.poc.banking.stream.producer;

import org.eclipse.microprofile.reactive.messaging.Channel;
import org.tcelor.poc.banking.stream.model.TransactionStreamed;

import io.smallrye.reactive.messaging.MutinyEmitter;
import jakarta.inject.Inject;

public class TransactionProducer {

    @Inject
    @Channel("transaction-requests")
    MutinyEmitter<String> transactionRequestEmitter;
    
    public TransactionStreamed emit(TransactionStreamed transaction) {
        transactionRequestEmitter.sendAndForget(transaction.toJsonString());
        return transaction;
    }
}
