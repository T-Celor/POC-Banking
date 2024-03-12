package org.tcelor.poc.banking.stream.producer;

import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Outgoing;

import io.quarkus.logging.Log;
import io.smallrye.reactive.messaging.MutinyEmitter;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class LogProducer {

          
    public String emit(String log) {
        Log.debug(log);
        //logRequestEmitter.sendAndForget(log.toString());
        return log;
    }

}
