package finances.model;

import java.math.BigDecimal;
import java.time.LocalDate;

import java.io.Serializable;

public class Transaction implements Serializable {
    private static final long serialVersionUID = 1L;
    private LocalDate date;
    private String description;
    private BigDecimal amount;
    private Category category;
    private String account;

    public Transaction(LocalDate date, String description, BigDecimal amount, Category category, String account) {
        this.date = date;
        this.description = description;
        this.amount = amount;
        this.category = category;
        this.account = account;
    }

    public LocalDate getDate() {
        return date;
    }

    public String getDescription() {
        return description;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public Category getCategory() {
        return category;
    }

    public String getAccount() {
        return account;
    }
}
