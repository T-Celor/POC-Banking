package org.tcelor.poc.banking.resource;

import java.time.Duration;
import java.util.List;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.jboss.resteasy.reactive.RestResponse;
import org.tcelor.poc.banking.config.ConstantConfig;
import org.tcelor.poc.banking.entity.AccountDao;
import org.tcelor.poc.banking.entity.Role;
import org.tcelor.poc.banking.resource.model.request.AccountMetadataRequest;
import org.tcelor.poc.banking.resource.model.result.DefaultResponse;

import io.quarkus.hibernate.reactive.panache.PanacheEntityBase;
import io.quarkus.security.ForbiddenException;
import io.quarkus.security.identity.SecurityIdentity;
import io.smallrye.mutiny.Uni;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.InternalServerErrorException;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/api/accounts")
public class Account {
    @Inject
    SecurityIdentity identity;

    @Operation(summary = "Endpoint for getting all accounts, protected by ADMIN role.")
    @GET
    @RolesAllowed({ "ADMIN" })
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Uni<List<PanacheEntityBase>> getAccounts() {
        return AccountDao.listAll();
    }
    
    @Operation(summary = "Endpoint for getting one account, protected by ADMIN or USER role.\nAn admin can access to all account.\nThe user can only access to his own account.")
    @RolesAllowed({ "ADMIN", "USER" })
    @Path("/{username}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Uni<RestResponse<Object>> getAccount(@PathParam(value = "username") String username) {
        if (identity.getPrincipal().getName().equals(username) || identity.getRoles().contains(Role.ADMIN.stringValue())) {
            return AccountDao.findById(username)
            .map(item -> RestResponse.status(RestResponse.Status.OK, item));
       }
        return Uni.createFrom().item(RestResponse.status(RestResponse.Status.FORBIDDEN, new DefaultResponse("Accès refusé.")));
    }
    
    @Operation(summary = "Endpoint for modifying user bank profile, protected by ADMIN or USER role.\nAn admin can modify any accounts.\nThe user can only access to his own account.")
    @RolesAllowed({ "ADMIN", "USER" })
    @Path("/{username}")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Uni<Object> modifyAccount(@PathParam(value = "username") String username,
            AccountMetadataRequest request) {
        return Uni.createFrom()
                .item(() ->  identity.getPrincipal().getName().equals(username) || identity.getRoles().contains(Role.ADMIN.stringValue()))
                .flatMap(isOwnerOrAdmin -> isOwnerOrAdmin
                        ? AccountDao.modifyAccount(username, request.getFirstname(), request.getLastname())
                            .onItem()
                                .transform(item -> RestResponse.status(RestResponse.Status.OK, item))
                            .ifNoItem().after(Duration.ofMillis(ConstantConfig.TIMEOUT))
                                .failWith(new Exception("💥"))
                            .onFailure().recoverWithUni(err -> Uni.createFrom().failure(new InternalServerErrorException("Error on accessing data.")))
                    : Uni.createFrom().failure(new ForbiddenException("Access Denied."))
                );
    }
}
