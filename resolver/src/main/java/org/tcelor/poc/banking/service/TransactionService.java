package org.tcelor.poc.banking.service;

import org.tcelor.poc.banking.entity.AccountDao;
import org.tcelor.poc.banking.entity.TransactionDao;
import org.tcelor.poc.banking.entity.TransactionState;

import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class TransactionService {

        public Uni<TransactionDao> processTransaction(Long transactionId) {
                return TransactionDao.<TransactionDao>findById(transactionId).onItem().invoke(
                                transaction -> {
                                        if (transaction.state == TransactionState.PENDING) {
                                                if (transaction.from == null) { //Non system transaction
                                                        AccountDao.<AccountDao>findById(transaction.to).subscribe().with(
                                                                        userTo -> {
                                                                                if (userTo != null) {
                                                                                        AccountDao.modifyBalance(transaction.to, userTo.balance.add(transaction.amount)).subscribeAsCompletionStage();
                                                                                        TransactionDao.validate(
                                                                                                        transactionId)
                                                                                                        .subscribeAsCompletionStage();
                                                                                } else {
                                                                                        TransactionDao.reject(transactionId).subscribeAsCompletionStage();
                                                                                }
                                                                        },
                                                                        failure -> {
                                                                                System.out.println("Fail : " + failure);
                                                                                TransactionDao.reject(transactionId).subscribeAsCompletionStage();
                                                                        });
                                                } else {
                                                        AccountDao.<AccountDao>findById(transaction.from).subscribe().with(
                                                                        userFrom -> {
                                                                                if (userFrom != null) {
                                                                                        AccountDao.<AccountDao>findById(transaction.to).subscribe().with(
                                                                                                userTo -> {
                                                                                                        if (userTo != null) {
                                                                                                                AccountDao.modifyBalance(transaction.to, userTo.balance.add(transaction.amount.negate())).subscribeAsCompletionStage();
                                                                                                                AccountDao.modifyBalance(transaction.to, userTo.balance.add(transaction.amount)).subscribeAsCompletionStage();
                                                                                                                TransactionDao.validate(transactionId).subscribeAsCompletionStage();
                                                                                                        } else {
                                                                                                                TransactionDao.reject(transactionId).subscribeAsCompletionStage();
                                                                                                        }
                                                                                                },
                                                                                                failure -> {
                                                                                                        TransactionDao.reject(transactionId).subscribeAsCompletionStage();
                                                                                                });
                                                                                } else {
                                                                                        TransactionDao.reject(transactionId).subscribeAsCompletionStage();
                                                                                }
                                                                        },
                                                                        failure -> {
                                                                                TransactionDao.reject(transactionId).subscribeAsCompletionStage();
                                                                        });
                                                }
                                        }
                                }
                );
        }
}
