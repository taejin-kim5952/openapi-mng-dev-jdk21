package com.kt.openapi.web.apigw.entity.api.cp;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

public class CpApiEntity implements Serializable {
    @JsonProperty("transactionid")
    private String transactionId;
    
    @JsonProperty("sequenceno")
    private String sequenceNo;
    
}
