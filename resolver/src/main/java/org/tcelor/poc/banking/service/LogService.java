package org.tcelor.poc.banking.service;

import java.time.LocalDateTime;

import org.tcelor.poc.banking.stream.producer.LogProducer;

import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class LogService {

    @Inject
    private LogProducer logProducer;

    public void debug(String message) {
        String messageBuilded = buildMessage(message, 0);
        Log.debug(messageBuilded);
        logProducer.emit(messageBuilded);
    }

    public void info(String message) {
        String messageBuilded = buildMessage(message, 1);
        Log.info(messageBuilded);
        logProducer.emit(messageBuilded);
    }

    public void warn(String message) {
        String messageBuilded = buildMessage(message, 2);
        Log.warn(messageBuilded);
        logProducer.emit(messageBuilded);
    }
    
    public void error(String message) {
        String messageBuilded = buildMessage(message, 3);
        Log.error(messageBuilded);
        logProducer.emit(messageBuilded);
    }
    
    public void fatal(String message) {
        String messageBuilded = buildMessage(message, 4);
        Log.fatal(messageBuilded);
        logProducer.emit(messageBuilded);
    }
    
    private String buildMessage(String message, int level) {
        return "POC_BANKING_RESOLVER_ALERTE_"+level+":-:" + LocalDateTime.now() + ":-:" + message;
    }
}
