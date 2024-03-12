package org.tcelor.poc.banking.entity;

import java.math.BigDecimal;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import io.quarkus.hibernate.reactive.panache.PanacheEntityBase;
import io.quarkus.panache.common.Parameters;
import io.smallrye.mutiny.Uni;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;

@Entity
@Table(name = "Account")
public class AccountDao extends PanacheEntityBase {

    @Id
    public String username;

    public String firstname;

    public String lastname;

    public BigDecimal balance;

    @OneToOne(fetch = FetchType.EAGER)
    @PrimaryKeyJoinColumn
    @OnDelete(action = OnDeleteAction.CASCADE)
    public AuthDao user;

    public static Uni<AccountDao> add(String username) {
        AccountDao account = new AccountDao();
        account.username = username;
        account.balance = new BigDecimal(0.0);
        return account.persistAndFlush();
    }

    public static Uni<Integer> modifyAccount(String existingUsername, String newFirstname, String newLastname) {
        return update("firstname =  ?1, lastname = ?2 WHERE username = ?3", newFirstname,
                newLastname, existingUsername);
    }

    public static Uni<Boolean> accountExists(String username) {
            return find("username = :username", Parameters.with("username", username))
            .firstResult().onItem()
                    .transformToUni(result -> Uni.createFrom().item(result != null));
    }
}
