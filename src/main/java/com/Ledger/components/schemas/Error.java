package com.Ledger.components.schemas;

import javax.validation.constraints.Size;

public class Error {

    // error message
    @Size(min = 1, message = "The error message's minimum length of {min} is not met")
    private String message;

    // error code
    @Size(min = 1, message = "The error code's minimum length of {min} is not met")
    private String code;


    // getters and setters
    public @Size(min = 1, message = "The error message's minimum length of {min} is not met") String getMessage() {
        return message;
    }

    public void setMessage(@Size(min = 1, message = "The error message's minimum length of {min} is not met") String message) {
        this.message = message;
    }

    public @Size(min = 1, message = "The error code's minimum length of {min} is not met") String getCode() {
        return code;
    }

    public void setCode(@Size(min = 1, message = "The error code's minimum length of {min} is not met") String code) {
        this.code = code;
    }

}
