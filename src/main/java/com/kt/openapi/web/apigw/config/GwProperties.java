package com.kt.openapi.web.apigw.config;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class GwProperties {
     //--[drm][chg]@Value("${gateway.ml.tbUrl}")
    @Value("${gateway.ml.tbUrl}")
    private String tbMlServer;

    //--[drm][chg]@Value("${gateway.ml.prodUrl}")
    @Value("${gateway.ml.prodUrl}")
    private String prodMlServer;

    //--[drm][chg]@Value("${gateway.pl.tbList}")
    @Value("${gateway.pl.tbList}")
    private String tbPlList;

    //--[drm][chg]@Value("${gateway.route.tbList}")
    @Value("${gateway.route.tbList}")
    private String tbRouteList;

    //--[drm][chg]@Value("${gateway.al.tbList}")
    @Value("${gateway.al.tbList}")
    private String tbAlList;

//    @Value("${gateway.pl.prodList}")
//    private String prodPlList;
//
//    @Value("${gateway.route.prodList}")
//    private String prodRouteList;
//
//    @Value("${gateway.al.prodList}")
//    private String prodAlList;

//    @Value("${gateway.pl.prodBList}")
    @Value("${gateway.pl.prodBList}")
    private String prodBPlList;

//    @Value("${gateway.route.prodBList}")
    @Value("${gateway.route.prodBList}")
    private String prodBRouteList;

//    @Value("${gateway.al.prodBList}")
    @Value("${gateway.al.prodBList}")
    private String prodBAlList;

//    @Value("${gateway.pl.prodDList}")
    @Value("${gateway.pl.prodDList}")
    private String prodDPlList;

//    @Value("${gateway.route.prodDList}")
    @Value("${gateway.route.prodDList}")
    private String prodDRouteList;

//    @Value("${gateway.al.prodDList}")
    @Value("${gateway.al.prodDList}")
    private String prodDAlList;

    public List<String> getTbPlList() {
        return Arrays.stream(StringUtils.split(tbPlList,",")).collect(Collectors.toList());
    }

    public List<String> getTbRouteList() {
        return Arrays.stream(StringUtils.split(tbRouteList, ",")).collect(Collectors.toList());
    }

    public List<String> getTbAlList() {
        return Arrays.stream(StringUtils.split(tbAlList,",")).collect(Collectors.toList());
    }

    public List<String> getProdBPlList() {
        return Arrays.stream(StringUtils.split(prodBPlList,",")).collect(Collectors.toList());
    }

    public List<String> getProdBRouteList() {
        return Arrays.stream(StringUtils.split(prodBRouteList, ",")).collect(Collectors.toList());
    }

    public List<String> getProdBAlList() {
        return Arrays.stream(StringUtils.split(prodBAlList,",")).collect(Collectors.toList());
    }

    public List<String> getProdDPlList() {
        return Arrays.stream(StringUtils.split(prodDPlList,",")).collect(Collectors.toList());
    }

    public List<String> getProdDRouteList() {
        return Arrays.stream(StringUtils.split(prodDRouteList, ",")).collect(Collectors.toList());
    }

    public List<String> getProdDAlList() {
        return Arrays.stream(StringUtils.split(prodDAlList,",")).collect(Collectors.toList());
    }
}
