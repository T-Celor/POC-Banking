package org.tcelor.poc.banking.stream.producer;

import org.eclipse.microprofile.reactive.messaging.Channel;

import io.smallrye.reactive.messaging.MutinyEmitter;
import jakarta.inject.Inject;

public class LogProducer {

    @Inject
    @Channel("log-requests")
    MutinyEmitter<String> logRequestEmitter;
          
    public String emit(String log) {
        logRequestEmitter.sendAndForget(log);
        return log;
    }

}
