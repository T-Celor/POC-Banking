package org.tcelor.poc.banking.resource.model.request;

import java.math.BigDecimal;

public class FundUserRequest {

    private String to;
    private BigDecimal amount;
    private String description;

    public String getTo() {
        return to;
    }
    public void setTo(String to) {
        this.to = to;
    }
    public BigDecimal getAmount() {
        return amount;
    }
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
}
