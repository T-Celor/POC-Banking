package org.tcelor.poc.banking.stream;

import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import org.eclipse.microprofile.reactive.messaging.Channel;
import org.tcelor.poc.banking.stream.model.TransactionStreamed;

import io.smallrye.mutiny.Multi;

@Path("/streams")
public class StreamResource {
    @Inject
    @Channel("transaction")
    Multi<TransactionStreamed> transactions;
    
    @Inject
    @Channel("log")
    Multi<String> logs; 

    @Path("/transactions")
    @GET
    @Produces(MediaType.SERVER_SENT_EVENTS) 
    public Multi<TransactionStreamed> transaction() {
        return transactions;
    }
    
    @Path("/logs")
    @GET
    @Produces(MediaType.SERVER_SENT_EVENTS) 
    public Multi<String> log() {
        return logs; 
    }
}