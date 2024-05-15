package com.Ledger.components.requestBodies;

import com.Ledger.components.schemas.Amount;

import javax.validation.constraints.Size;

public class LoadRequest {

    @Size(min = 1, message = "String userId must have length of at least {min}")
    private String userId;

    @Size(min = 1, message = "String messageId must have length of at least {min}")
    private String messageId;

    private Amount amount;


    // getters and setters
    public @Size(min = 1, message = "String userId must have length of at least {min}") String getUserId() {
        return userId;
    }

    public void setUserId(@Size(min = 1, message = "String userId must have length of at least {min}") String userId) {
        this.userId = userId;
    }

    public @Size(min = 1, message = "String messageId must have length of at least {min}") String getMessageId() {
        return messageId;
    }

    public void setMessageId(@Size(min = 1, message = "String messageId must have length of at least {min}") String messageId) {
        this.messageId = messageId;
    }

    public Amount getAmount() {
        return amount;
    }

    public void setAmount(Amount amount) {
        this.amount = amount;
    }

}
