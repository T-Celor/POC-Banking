package org.tcelor.poc.banking.stream.consumer;

import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.tcelor.poc.banking.entity.TransactionDao;
import org.tcelor.poc.banking.service.LogService;
import org.tcelor.poc.banking.service.TransactionService;
import org.tcelor.poc.banking.stream.model.TransactionStreamed;

import io.quarkus.hibernate.reactive.panache.common.WithSession;
import io.quarkus.hibernate.reactive.panache.common.WithTransaction;
import io.quarkus.vertx.ConsumeEvent;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class TransactionConsumer {

    @Inject
    private LogService logService;

    @Inject
    private TransactionService transactionService;

    @Incoming("transaction")
    @WithTransaction
    public Uni<TransactionDao> resolveTransaction(String transactionStreamed) throws InterruptedException {
        TransactionStreamed requestTransaction = new TransactionStreamed(transactionStreamed);
        logService.info("Process transaction [" + requestTransaction.id + "] ...");
        return transactionService.processTransaction(requestTransaction.id).invoke(t -> logService.info("Transaction [" + requestTransaction.id + "] proceed."));
    }
}

