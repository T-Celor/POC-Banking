package org.tcelor.poc.banking.entity;

import java.math.BigDecimal;
import java.time.Duration;

import org.tcelor.poc.banking.config.ConstantConfig;

import io.quarkus.hibernate.reactive.panache.PanacheEntityBase;
import io.quarkus.panache.common.Parameters;
import io.smallrye.mutiny.Uni;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "Account")
public class AccountDao extends PanacheEntityBase {

    @Id
    public String username;

    public String firstname;

    public String lastname;

    public BigDecimal balance;

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

    public static Uni<Integer> modifyBalance(String username, BigDecimal value) {
        return update("balance =  ?1 WHERE username = ?2", value, username);
    }

    @Override
    public String toString() {
        return "AccountDao [username=" + username + ", firstname=" + firstname + ", lastname=" + lastname + ", balance="
                + balance + "]";
    }

}
