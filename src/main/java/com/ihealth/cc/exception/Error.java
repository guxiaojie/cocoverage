package com.ihealth.cc.exception;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;

public class Error {
    @JsonProperty("code")
    public String Code;

    @JsonProperty("message")
    public String Message;

    private Date timestamp;
    public Date getTimestamp() {
        return timestamp;
    }
    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    private String details;
    public String getDetails() {
        return details;
    }
    public void setDetails(String details) {
        this.details = details;
    }

    public String getCode() {
        return Code;
    }

    public void setCode(String code) {
        Code = code;
    }

    public String getMessage() {
        return Message;
    }

    public void setMessage(String message) {
        Message = message;
    }
}
