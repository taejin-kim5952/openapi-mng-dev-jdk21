package com.kt.openapi.web.beast.apigw.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

//-- [tag:PRJ-20220901]
//-- [i][clone from com.kt.openapi.web.apigw.config]
@Component
public class BstgwProperties {

    @Value("${bstgw.api.local.url}")
    public String bstgwApiLocalUrl;

    @Value("${bstgw.api.test.url}")
    public String bstgwApiTestUrl;

    @Value("${bstgw.api.tb.url}")
    public String bstgwApiTbUrl;

    @Value("${bstgw.api.prd.url}")
    public String bstgwApiPrdUrl;

    @Value("${bstgw.api.new.tb.url}")
    public String bstgwApiNewTbUrl;

    @Value("${bstgw.api.new.prd.url}")
    public String bstgwApiNewPrdUrl;

    @Value("${bstgw.api.header.authorization}")
    public String bstgwApiHeaderAuthorization;

    @Value("${bstgw.api.header.baseurl}")
    public String bstgwApiHeaderBaseUrl;
}
