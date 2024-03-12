package org.tcelor.poc.banking.mapper;

import org.tcelor.poc.banking.entity.TransactionDao;
import org.tcelor.poc.banking.stream.model.TransactionStreamed;

public class TransactionMapper {

    public static TransactionStreamed convertToStream(TransactionDao transactionDao) {
        TransactionStreamed t = new TransactionStreamed();
        t.amount = transactionDao.amount;
        t.description = transactionDao.description;
        t.from = transactionDao.from;
        t.id = transactionDao.id;
        t.timestamp = transactionDao.timestamp;
        t.to = transactionDao.to;
        t.state = transactionDao.state;
        return t;
    }
}
