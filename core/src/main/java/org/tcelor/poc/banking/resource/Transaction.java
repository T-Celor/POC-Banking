package org.tcelor.poc.banking.resource;

import java.math.BigDecimal;
import java.util.List;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.reactive.messaging.OnOverflow;
import org.jboss.resteasy.reactive.RestResponse;
import org.tcelor.poc.banking.config.ResourceConfig;
import org.tcelor.poc.banking.entity.AccountDao;
import org.tcelor.poc.banking.entity.Role;
import org.tcelor.poc.banking.entity.TransactionDao;
import org.tcelor.poc.banking.mapper.TransactionMapper;
import org.tcelor.poc.banking.resource.model.request.FundUserRequest;
import org.tcelor.poc.banking.resource.model.result.DefaultResponse;
import org.tcelor.poc.banking.service.LogService;
import org.tcelor.poc.banking.stream.producer.TransactionProducer;

import io.quarkus.hibernate.reactive.panache.PanacheEntityBase;
import io.quarkus.security.ForbiddenException;
import io.quarkus.security.identity.SecurityIdentity;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;

@Path("/api/transactions")
public class Transaction {

    @Inject
    LogService logService;

    @Inject
    SecurityIdentity identity;

    @Inject
    TransactionProducer transactionProducer;

    @Operation(summary = "Endpoint for getting all transactions, protected by ADMIN role.")
    @RolesAllowed({ "ADMIN" })
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Uni<List<PanacheEntityBase>> getTransactions() {
        return TransactionDao.listAll();
    }
    
    @Operation(summary = "Endpoint for getting one transaction, protected by ADMIN or USER role.\nAn admin can access to all account.\nThe user can only access to his own account.")
    @RolesAllowed({ "ADMIN", "USER" })
    @Path("/{id}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Uni<Object> getAccount(@PathParam(value = "id") Long id) {
        return ResourceConfig.withCommonTimeout(TransactionDao.<TransactionDao>findById(id))
            .onItem()
                .transform(item -> {
                    if (identity.getPrincipal().getName().equals(item.to) || identity.getPrincipal().getName().equals(item.from) || identity.getRoles().contains(Role.ADMIN.stringValue())) {
                        return RestResponse.status(RestResponse.Status.OK, item);
                    } else {
                        return new ForbiddenException("Access Denied.");
                    }
                })
                        .onFailure()
                .recoverWithUni(err -> Uni.createFrom()
                .item(RestResponse.status(RestResponse.Status.OK, new DefaultResponse("Internal error."))));
    }
    
    @Operation(summary = "Endpoint for create account transaction to another user, protected by USER role.\nAn admin can modify any accounts.\nThe user can only access to his own account, if he got enought balance and if the receiver exist.")
    @RolesAllowed({ "USER" })
    @POST()
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @OnOverflow(OnOverflow.Strategy.DROP)
    public Uni<RestResponse<DefaultResponse>> transfert(@QueryParam("to") String to,
        @QueryParam("description") String description,
            @QueryParam("amount") BigDecimal amount) {
        return ResourceConfig.withCommonTimeout(AccountDao.accountExists(identity.getPrincipal().getName()))
            .onItem()
                .transformToUni(isUserFromExist -> {
                    if (!isUserFromExist) 
                        return Uni.createFrom().item(RestResponse.status(RestResponse.Status.OK, new DefaultResponse("The cannot be found.")));
                    return ResourceConfig.withCommonTimeout(AccountDao.accountExists(to)).onItem().transformToUni(isUserToExist -> {
                        if (!isUserToExist) 
                            return Uni.createFrom().item(RestResponse.status(RestResponse.Status.OK, new DefaultResponse("The receiver cannot be found.")));
                        return ResourceConfig.withCommonTimeout(TransactionDao.add(identity.getPrincipal().getName(), to, amount, description))
                            .onItem().transform(transaction -> {
                                    transactionProducer.emit(TransactionMapper.convertToStream(transaction));
                                    return RestResponse.status(RestResponse.Status.OK, new DefaultResponse(transaction));
                                })
                            .onFailure()
                                .recoverWithUni(err -> Uni.createFrom()
                                .item(RestResponse.status(RestResponse.Status.INTERNAL_SERVER_ERROR, new DefaultResponse(err))));
                    });
                })
            .onFailure()
                .recoverWithUni(err -> Uni.createFrom()
                .item(RestResponse.status(RestResponse.Status.OK, new DefaultResponse("Error on accessing account."))));
    }

    @Operation(summary = "Endpoint for adding fund for a user from admin account, protected by ADMIN role.")
    @RolesAllowed({ "ADMIN" })
    @Path("/fund")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Uni<RestResponse<DefaultResponse>> fundAccount(FundUserRequest request) {
        return ResourceConfig.withCommonTimeout(AccountDao.accountExists(request.getTo()))
                .onItem().transformToUni(isUserToExist -> {
                    if (!isUserToExist) 
                        return Uni.createFrom().item(RestResponse.status(RestResponse.Status.NOT_FOUND, new DefaultResponse("The receiver cannot be found.")));
                    return TransactionDao.add(null, request.getTo(), request.getAmount(),
                                    request.getDescription())
                            .onItem().transform(transaction -> {
                                logService.info("System on account [" + identity.getPrincipal().getName() + "] create event to fund user [" + request.getTo() + "] for an amount of : " + request.getAmount() + ".");
                                transactionProducer.emit(TransactionMapper.convertToStream(transaction));
                                return RestResponse.status(RestResponse.Status.OK, new DefaultResponse(transaction));
                            })
                        .onFailure()
                            .recoverWithUni(err -> Uni.createFrom()
                            .item(RestResponse.status(RestResponse.Status.INTERNAL_SERVER_ERROR, new DefaultResponse(err))));
                })
            .onFailure()
                .recoverWithUni(err -> Uni.createFrom()
                .item(RestResponse.status(RestResponse.Status.INTERNAL_SERVER_ERROR, new DefaultResponse("Error on accessing account."))));
    }
}
