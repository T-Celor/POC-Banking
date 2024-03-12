package org.tcelor.poc.banking.entity;

import io.quarkus.elytron.security.common.BcryptUtil;
import io.quarkus.hibernate.reactive.panache.PanacheEntityBase;
import io.quarkus.security.jpa.Password;
import io.quarkus.security.jpa.Roles;
import io.quarkus.security.jpa.UserDefinition;
import io.quarkus.security.jpa.Username;
import io.smallrye.mutiny.Uni;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "Auth")
@UserDefinition
public class AuthDao extends PanacheEntityBase {
    @Id
    @Username
    public String username;
    @Password
    public String password;
    @Roles
    public String role;

    public static Uni<AuthDao> add(String username, String password, Role role) {
        AuthDao user = new AuthDao();
        user.username = username;
        user.password = BcryptUtil.bcryptHash(password);
        user.role = role.stringValue();
        return user.persistAndFlush();
    }
}
