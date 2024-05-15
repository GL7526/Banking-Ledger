package com.Ledger.components.schemas;

import javax.validation.constraints.Size;

public class Amount {

    // amount in the denomination of the currency
    @Size(min = 1, message = "The minimum length of String, amount, must be at least {min}")
    private String amount;

    // denomination of the currency
    @Size(min = 1, message = "The minimum length of the denomination, currency, must be at least {min}")
    private String currency;

    private DebitCredit debitCredit;


    // getters and setters
    public @Size(min = 1, message = "The minimum length of String, amount, must be at least {min}") String getAmount() {
        return amount;
    }

    public void setAmount(@Size(min = 1, message = "The minimum length of String, amount, must be at least {min}") String amount) {
        this.amount = amount;
    }

    public @Size(min = 1, message = "The minimum length of the denomination, currency, must be at least {min}") String getCurrency() {
        return currency;
    }

    public void setCurrency(@Size(min = 1, message = "The minimum length of the denomination, currency, must be at least {min}") String currency) {
        this.currency = currency;
    }

    public DebitCredit getDebitCredit() {
        return debitCredit;
    }

    public void setDebitCredit(DebitCredit debitCredit) {
        this.debitCredit = debitCredit;
    }

}
