package com.ihealth.cc.exception;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties
public class ErrorResponse {
    @JsonProperty("error")
    public Error error;

    @JsonProperty("version")
    public String version;
}
