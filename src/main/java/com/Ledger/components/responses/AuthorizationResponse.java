package com.Ledger.components.responses;

import com.Ledger.components.schemas.Amount;
import com.Ledger.components.schemas.ResponseCode;

import javax.validation.constraints.Size;

public class AuthorizationResponse {

    @Size(min = 1, message = "String userId must have length of at least {min}")
    private String userId;

    @Size(min = 1, message = "String messageId must have length of at least {min}")
    private String messageId;

    private ResponseCode responseCode;

    private Amount balance;


    // getters and setters
    public @Size(min = 1, message = "String userId must have length of at least {min}") String getUserId() {
        return userId;
    }

    public void setUserId(@Size(min = 1, message = "String userId must have length of at least {min}") String userId) {
        this.userId = userId;
    }

    public ResponseCode getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(ResponseCode responseCode) {
        this.responseCode = responseCode;
    }

    public @Size(min = 1, message = "String messageId must have length of at least {min}") String getMessageId() {
        return messageId;
    }

    public void setMessageId(@Size(min = 1, message = "String messageId must have length of at least {min}") String messageId) {
        this.messageId = messageId;
    }

    public Amount getBalance() {
        return balance;
    }

    public void setBalance(Amount balance) {
        this.balance = balance;
    }

}
