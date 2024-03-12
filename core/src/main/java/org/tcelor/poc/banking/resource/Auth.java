package org.tcelor.poc.banking.resource;

import java.time.Duration;
import java.util.List;


import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.jboss.resteasy.reactive.RestResponse;
import org.tcelor.poc.banking.entity.Role;
import org.tcelor.poc.banking.resource.model.request.InscriptionRequest;
import org.tcelor.poc.banking.config.ConstantConfig;
import org.tcelor.poc.banking.entity.AccountDao;
import org.tcelor.poc.banking.entity.AuthDao;

import io.quarkus.hibernate.reactive.panache.PanacheEntityBase;
import io.quarkus.hibernate.reactive.panache.common.WithTransaction;
import io.quarkus.logging.Log;
import io.smallrye.mutiny.Uni;
import jakarta.annotation.security.RolesAllowed;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/api/auths")
@ApplicationScoped
public class Auth {

    @Operation(summary = "Endpoint for user inscription.")
    @WithTransaction
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Uni<RestResponse<Object>> inscription(@RequestBody InscriptionRequest request) {
    return AuthDao.add(request.getUsername(), request.getPassword(), Role.USER)
        .ifNoItem()
            .after(Duration.ofMillis(ConstantConfig.TIMEOUT)).failWith(new Exception("💥"))
        .onItem().transformToUni((user) -> {
            return AccountDao.add(request.getUsername())
                        .map(item -> RestResponse.status(Response.Status.CREATED))
                    .onFailure().recoverWithItem((ex) -> {
                        Log.error("Cannot add the user " + request.getUsername() + " to the system. Cause : " + ex.getMessage());
                        return RestResponse.status(Response.Status.INTERNAL_SERVER_ERROR); 
                    });
        })
        .onFailure()
            .recoverWithItem((ex) -> {
                Log.error("Cannot add the user " + request.getUsername() + " to the system. Cause : " + ex.getMessage());
                return RestResponse.status(Response.Status.INTERNAL_SERVER_ERROR); 
            });
    }

    @Operation(summary = "Endpoint for getting all users, protected by ADMIN role.")
    @WithTransaction
    @GET
    @RolesAllowed({ "ADMIN" })
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Uni<RestResponse<List<PanacheEntityBase>>> getUsers() {
        return AuthDao.listAll()
                .ifNoItem()
                    .after(Duration.ofMillis(ConstantConfig.TIMEOUT)).failWith(new Exception("💥"))
                .onItem()
                    .transform((users ->  RestResponse.status(Response.Status.OK, users)))
                .onFailure()
                    .recoverWithItem((ex) -> {
                        Log.error("Cannot get all users. Cause : " + ex.getMessage());
                        return RestResponse.status(Response.Status.INTERNAL_SERVER_ERROR); 
                    });
    }
}
