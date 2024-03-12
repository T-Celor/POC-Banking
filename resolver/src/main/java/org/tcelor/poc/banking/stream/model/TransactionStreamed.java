package org.tcelor.poc.banking.stream.model;

import java.io.StringReader;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.tcelor.poc.banking.entity.TransactionState;

import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;

public class TransactionStreamed {

    public Long id;
    public LocalDateTime timestamp;
    public String from;
    public String to;
    public BigDecimal amount;
    public String description;
    public TransactionState state;

    public TransactionStreamed() {
    }

    public TransactionStreamed(Long id, LocalDateTime timestamp, String from, String to, BigDecimal amount,
            String description) {
        this.id = id;
        this.timestamp = timestamp;
        this.from = from;
        this.to = to;
        this.amount = amount;
        this.description = description;
    }

    public TransactionStreamed(String jsonString) {
        StringReader reader = new StringReader(jsonString);
        JsonReader jsonReader = Json.createReader(reader);
        
        JsonObject jsonObject = jsonReader.readObject();
        jsonReader.close();

        this.id = jsonObject.getJsonNumber("id").longValue();
        this.timestamp = LocalDateTime.parse(jsonObject.getString("timestamp"), DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        this.from = jsonObject.getString("from");
        this.to = jsonObject.getString("to");
        this.amount = jsonObject.containsKey("amount") ? new BigDecimal(jsonObject.getString("amount")) : null;
        this.description = jsonObject.getString("description");
        this.state = TransactionState.valueOf(jsonObject.getString("state"));
    }
   
    @Override
    public String toString() {
        return "TransactionStreamed [id=" + id + ", timestamp=" + timestamp + ", from=" + from + ", to=" + to
                + ", amount=" + amount + ", description=" + description + ", state=" + state + "]";
    }

    public String toJsonString() {
        DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

        return Json.createObjectBuilder()
                .add("id", this.id)
                .add("timestamp", this.timestamp != null ? formatter.format(this.timestamp) : "null")
                .add("from", this.from != null ? this.from : "null")
                .add("to", this.to != null ? this.to : "null")
                .add("amount", this.amount != null ? this.amount.toString() : "null")
                .add("description", this.description != null ? this.description : "null")
                .add("state", this.state != null ? this.state.toString() : "null")
                .build()
                .toString();
    }
}
