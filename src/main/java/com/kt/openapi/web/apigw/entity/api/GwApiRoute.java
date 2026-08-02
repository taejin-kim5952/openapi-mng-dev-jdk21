package com.kt.openapi.web.apigw.entity.api;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.kt.openapi.web.apigw.constants.GwConstants;

public class GwApiRoute implements Serializable {
	@Serial
	private static final long serialVersionUID = -9001333462553699920L;
	
	@JsonProperty("route-id")
	private List<String> routeIds;
	
	@JsonProperty("route-target")
    private String routeTargets = GwConstants.API_ROUTE_INFO_ROUTE_TARGET;
	
	public GwApiRoute() {
    }

    public GwApiRoute(List<String> routeIds) {
        this.routeIds = routeIds;
    }


    public List<String> getRouteIds() {
        return routeIds;
    }

    public void setRouteIds(List<String> routeIds) {
        this.routeIds = routeIds;
    }
    

	public String getRouteTargets() {
		return routeTargets;
	}

	public void setRouteTargets(String routeTargets) {
		this.routeTargets = routeTargets;
	}
}


